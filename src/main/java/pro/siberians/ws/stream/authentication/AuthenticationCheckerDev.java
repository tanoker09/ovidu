package pro.siberians.ws.stream.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.UserModel;
import pro.siberians.ws.stream.service.UserService;
import pro.siberians.ws.stream.exception.NotAuthenticatedException;

@Profile("dev")
@Component
public final class AuthenticationCheckerDev implements AuthenticationChecker {

	@Autowired private UserService userService;

	private Logger log = LoggerFactory.getLogger(AuthenticationCheckerDev.class);

	public final UserModel authenticateUser(HttpServletRequest request) throws NotAuthenticatedException {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			String email = null;
			for (Cookie c : cookies) {
				if (c.getName().equals("DevAuthSessionCookie")) {
					email = c.getValue();
					log.info("Cookie DevAuthSessionCookie value: {}", email);
					break;
				}
			}
			if (email != null) {
				UserModel foundUser = userService.findByEmail(email)
					.map((user) -> {
						log.info(
							"Authenticating user with email {}, name {} and role {}",
							user.getEmail(), user.getName(), user.getRole()
						);
						return user;
					})
					.orElseThrow(() -> new NotAuthenticatedException("No user found with specified ID!"));
				return foundUser;
			} else {
				throw new NotAuthenticatedException("Auth cookie not found in request!");
			}
		} else {
			throw new NotAuthenticatedException("There are no cookies in request!");
		}
	}

	public final void unauthenticateUser(HttpServletResponse response) {
		Cookie authCookie = new Cookie("DevAuthSessionCookie", null);
		authCookie.setMaxAge(0);
		authCookie.setPath("/");
		response.addCookie(authCookie);
	}

}
