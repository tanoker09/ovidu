package pro.siberians.ws.stream.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.UserModel;
import pro.siberians.ws.stream.web.response.MessageResponse;
import pro.siberians.ws.stream.web.response.UserResponse;
import pro.siberians.ws.stream.config.UserAuthenticator;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private UserAuthenticator userAuthenticator;

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public UserResponse getInfo(HttpServletRequest request) {
		log.info("Getting user authentication info");
		UserModel user = userAuthenticator.getUserFromRequest(request);
		return new UserResponse(user);
    }

	@RequestMapping(value = "/info", method = RequestMethod.DELETE)
	public MessageResponse deleteInfo(HttpServletResponse response) {
		log.info("Deleting user authentication info");
		userAuthenticator.deleteAuthenticationInfo(response);
		return new MessageResponse("Authentication info deleted!");
    }

}
