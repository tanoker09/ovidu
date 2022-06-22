package pro.siberians.ws.stream.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.*;
import pro.siberians.ws.stream.web.response.*;
import pro.siberians.ws.stream.service.*;
import pro.siberians.ws.stream.authorization.*;
import pro.siberians.ws.stream.config.*;

@RestController
@RequestMapping("/api/watcher")
public class WatcherController {

	private Logger log = LoggerFactory.getLogger(WatcherController.class);

	@Autowired private MembershipService membershipService;
	@Autowired private ContestService contestService;
	@Autowired private StreamingService streamingService;
	@Autowired private UserAuthenticator userAuthenticator;
	@Autowired private AuthorizationChecker authorizationChecker;

	@RequestMapping(value = "/contests", method = RequestMethod.GET)
	public ContestsResponse getContests(HttpServletRequest request) {
		log.info("Watcher is getting all contests");
		UserModel user = userAuthenticator.getUserFromRequest(request);
		List<UserContestModel> userContests = membershipService.findUserContests(user);
		ContestsResponse response = new ContestsResponse(userContests);
		return response;
	}

	@RequestMapping(value = "/contests/{guid}/tokens", method = RequestMethod.POST)
	public TokenResponse getOpenViduToken(HttpServletRequest request, @PathVariable("guid") String contestGuid) throws Exception {
		log.info("Watcher is getting contest {} streaming token", contestGuid);
		UserModel user = userAuthenticator.getUserFromRequest(request);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		authorizationChecker.userMustBeAMemberOfContest(user, contest);
		authorizationChecker.contestMustBeActive(contest);
		String token = streamingService.getStreamingToken(contest);
		TokenResponse response = new TokenResponse(token);
		return response;
	}

}
