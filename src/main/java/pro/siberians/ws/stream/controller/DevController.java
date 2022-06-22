package pro.siberians.ws.stream.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import pro.siberians.ws.stream.model.PagedResults;
import pro.siberians.ws.stream.model.UserModel;
import pro.siberians.ws.stream.service.UserService;

@Profile("dev")
@RestController
@RequestMapping("/api/dev")
public class DevController {

	@Autowired UserService userService;

	private Logger log = LoggerFactory.getLogger(DevController.class);

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public PagedResults<UserModel> users() {
		log.info("Getting users list");
		return userService.findAllOnPage(null, 1000);
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public void authenticate(
		@RequestParam(name = "userEmail") String userEmail,
		@RequestParam(name = "redirectUri") String redirectUri,
		HttpServletResponse response
	) {
		log.info("Athenticating user");

		log.info("Setting auth cookie for user " + userEmail);
		Cookie authCookie = new Cookie("DevAuthSessionCookie", userEmail);
		authCookie.setMaxAge(3600);
		authCookie.setPath("/");
		response.addCookie(authCookie);

		log.info("Redirecting to " + redirectUri);
		response.setHeader("Location", redirectUri);
		response.setStatus(302);
    }

}
