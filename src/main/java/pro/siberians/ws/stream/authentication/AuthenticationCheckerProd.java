package pro.siberians.ws.stream.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import pro.siberians.ws.stream.model.UserModel;
import pro.siberians.ws.stream.service.UserService;
import pro.siberians.ws.stream.exception.NotAuthenticatedException;

@Profile("prod")
@Component
public final class AuthenticationCheckerProd implements AuthenticationChecker {

	@Autowired private UserService userService;

	private Logger log = LoggerFactory.getLogger(AuthenticationCheckerDev.class);

	// Example headers
	//
	// x-amzn-oidc-data header:
	// {
	//   "typ": "JWT",
	//   "kid": "40361c18-acdf-4817-a9a3-bb8980d2d398",
	//   "alg": "ES256",
	//   "iss": "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_uHRNi5PWq",
	//   "client": "7lm77ih28rn5kfpdrbonvekf72",
	//   "signer": "arn:aws:elasticloadbalancing:eu-central-1:557325395715:loadbalancer/app/Contest-Streamer-LB/62b80c5e3490cfa7",
	//   "exp": 1606744613
	// }
	//
	// x-amzn-oidc-data payload:
	// {
	//   "sub": "937477e1-3b78-416b-a6f1-bbca7a927256",
	//   "email_verified": "false",
	//   "given_name": "????",
	//   "email": "stream_test1@nsalab.org",
	//   "username": "azurestreamapp_stream_test1@nsalab.org",
	//   "exp": 1606744613,
	//   "iss": "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_uHRNi5PWq"
	// }

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AmznOidcDataPayload {
		public @JsonProperty("email") String email;
		public @JsonProperty("name") String name;
	}

	public final UserModel authenticateUser(HttpServletRequest request) throws NotAuthenticatedException {
		String amznOidcIdentity = request.getHeader("x-amzn-oidc-identity");
		String amznOidcAccessToken = request.getHeader("x-amzn-oidc-accesstoken");
		String amznOidcData = request.getHeader("x-amzn-oidc-data");

		log.debug("x-amzn-oidc-identity: {}", amznOidcIdentity);
		log.debug("x-amzn-oidc-accesstoken: {}", amznOidcAccessToken);
		log.debug("x-amzn-oidc-data: {}", amznOidcData);

		if (amznOidcData != null) {
			String jwtHeader, jwtPayload;
			try {
				// TODO: check JWT signature!

				jwtHeader = AwsCognitoJwtParser.getHeader(amznOidcData);
				jwtPayload = AwsCognitoJwtParser.getPayload(amznOidcData);

				log.debug("x-amzn-oidc-data header: {}", jwtHeader);
				log.debug("x-amzn-oidc-data payload: {}", jwtPayload);

				AmznOidcDataPayload dataPayload = new ObjectMapper()
					.readValue(jwtPayload, AmznOidcDataPayload.class);

				String userEmail = dataPayload.email;
				String userName = dataPayload.name;

				UserModel foundUser = userService.findByEmail(userEmail)
					.map((UserModel userModel) -> {
						log.info(
							"Authenticating user with email {}, name {} and role {}",
							userModel.getEmail(), userModel.getName(), userModel.getRole()
						);

						// Update user name if it is empty
						if (userName != null && (userModel.getName() == null || userModel.getName().equals(""))) {
							log.info("Change empty user name to: {}", userName);
							userService.changeName(userModel, userName);
						}

						return userModel;
					})
					.orElseGet(() -> {
						log.info("User not found! Creating new user with email {} and name {}", userEmail, userName);
						UserModel userModel = userService.create(userEmail, userName);
						return userModel;
					});

				return foundUser;
			} catch (UnsupportedEncodingException e) {
				log.error("Error parsing x-amzn-oidc-data header", e);
				throw new NotAuthenticatedException("Authentication header encoding is invalid!");
			} catch (JsonProcessingException e) {
				log.error("Error parsing x-amzn-oidc-data header as JSON", e);
				throw new NotAuthenticatedException("Authentication header parsing failed!");
			}
		} else {
			log.error("Header x-amzn-oidc-data value is null!");
			throw new NotAuthenticatedException("Authentication header is not set!");
		}
	}

	public final void unauthenticateUser(HttpServletResponse response) {
		final String[] cookieNames = {
			"AWSELBAuthSessionCookie",
			"AWSELBAuthSessionCookie-0",
			"AWSELBAuthSessionCookie-1"
		};
		for (String cookieName : cookieNames) {
			Cookie authCookie = new Cookie(cookieName, null);
			authCookie.setMaxAge(0);
			authCookie.setPath("/");
			response.addCookie(authCookie);
		}
	}

}
