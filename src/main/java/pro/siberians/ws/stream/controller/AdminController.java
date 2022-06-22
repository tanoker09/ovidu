package pro.siberians.ws.stream.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.*;
import pro.siberians.ws.stream.service.*;
import pro.siberians.ws.stream.service.ec2.Ec2ChangeStateService;
import pro.siberians.ws.stream.service.ec2.Ec2DescribeService;
import pro.siberians.ws.stream.tasks.*;
import pro.siberians.ws.stream.authorization.*;
import pro.siberians.ws.stream.web.request.admin.*;
import pro.siberians.ws.stream.web.response.MessageResponse;
import pro.siberians.ws.stream.web.response.admin.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private Logger log = LoggerFactory.getLogger(AdminController.class);

	@Autowired private UserService userService;
	@Autowired private ContestService contestService;
	@Autowired private MembershipService membershipService;
	@Autowired private AuthorizationChecker authorizationChecker;

	@Autowired private Ec2DescribeService ec2DescribeService;
	@Autowired private Ec2ChangeStateService ec2ChangeStateService;

	@Autowired private CreateContestOpenViduInstTask createContestOpenViduInstTask;
	@Autowired private DeleteContestOpenViduInstTask deleteContestOpenViduInstTask;

	@RequestMapping(value = "/contests", method = RequestMethod.GET)
	public PagedResults<ContestModel> getContests(
		@RequestParam(name = "limit", defaultValue = "10") Integer limit,
		@RequestParam(name = "lekPk", required = false) String lekPk,
		@RequestParam(name = "lekSk", required = false) String lekSk
	) {
		log.info("Admin is getting contests list");
		LastEvaluatedKey lek = LastEvaluatedKey.fromRequestParams(lekPk, lekSk);
		return contestService.findAllOnPage(lek, limit);
	}

	@RequestMapping(value = "/contests", method = RequestMethod.POST)
	public ContestModel createContest(@RequestBody ContestInfoRequest request) throws Exception {
		log.info("Admin is creating new contest");
		ContestModel contest = contestService.create(
			request.name,
			request.startAt,
			request.finishAt,
			request.awsInstType
		);
		createContestOpenViduInstTask.createOpenViduInstAsync(contest.getGuid());
		return contest;
	}

	@RequestMapping(value = "/contests/{contestGuid}", method = RequestMethod.GET)
	public ContestWithUsersResponse getContest(@PathVariable String contestGuid) throws Exception {
		log.info("Admin is getting contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		List<ContestUserModel> contestUsers = membershipService.findContestUsers(contest);
		return new ContestWithUsersResponse(contest, contestUsers);
	}

	@RequestMapping(value = "/contests/{contestGuid}", method = RequestMethod.POST)
	public ContestModel updateContest(
		@PathVariable String contestGuid,
		@RequestBody ContestInfoRequest request
	) throws Exception {
		log.info("Admin is updating contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		contestService.update(
			contest,
			request.name,
			request.startAt,
			request.finishAt
		);
		membershipService.updateContestInfo(contest);
		return contest;
	}

	@RequestMapping(value = "/contests/{contestGuid}", method = RequestMethod.DELETE)
	public ContestModel stopContest(@PathVariable String contestGuid) throws Exception {
		log.info("Admin is deleting contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		authorizationChecker.contestMustBeActive(contest);
		contestService.delete(contest);
		// TODO: recreate membership records for contest with changed status
		// TODO: upload recording to S3
		deleteContestOpenViduInstTask.deleteOpenViduInstAsync(contestGuid);
		return contest;
	}

	@RequestMapping(value = "/contests/{contestGuid}/addusers", method = RequestMethod.POST)
	public MessageResponse addContestUsers(
		@PathVariable String contestGuid,
		@RequestBody EmailsRequest request
	) throws Exception {
		log.info("Admin is adding members to contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		for (String userEmail : request.getValidatedEmails()) {
			log.info("Adding member {}", userEmail);
			membershipService.addMemberByEmail(userEmail, contest);
		}
		return new MessageResponse("Users have been added to the contest!");
	}

	@RequestMapping(value = "/contests/{contestGuid}/deleteusers", method = RequestMethod.POST)
	public MessageResponse deleteContestUsers(
		@PathVariable String contestGuid,
		@RequestBody EmailsRequest request
	) throws Exception {
		log.info("Admin is deleting members from contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		for (String userEmail : request.getValidatedEmails()) {
			log.info("Deleting member {}", userEmail);
			membershipService.deleteMemberByEmail(userEmail, contest);
		}
		return new MessageResponse("Users have been removed from the contest!");
	}

	@RequestMapping(value = "/contests/{contestGuid}/ec2start", method = RequestMethod.POST)
	public MessageResponse startContestEC2Instance(@PathVariable String contestGuid) throws Exception {
		log.info("Admin is starting EC2 instance for contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		String awsInstId = contest.getAwsInstId();
		ec2ChangeStateService.startEc2Instance(awsInstId);
		return new MessageResponse("Instance " + awsInstId + " is starting!");
	}

	@RequestMapping(value = "/contests/{contestGuid}/ec2stop", method = RequestMethod.POST)
	public MessageResponse stopContestEC2Instance(@PathVariable String contestGuid) throws Exception {
		log.info("Admin is stopping EC2 instance for contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		String awsInstId = contest.getAwsInstId();
		ec2ChangeStateService.stopEc2Instance(awsInstId);
		return new MessageResponse("Instance " + awsInstId + " is stopping!");
	}

	@RequestMapping(value = "/contests/{contestGuid}/ec2reboot", method = RequestMethod.POST)
	public MessageResponse rebootContestEC2Instance(@PathVariable String contestGuid) throws Exception {
		log.info("Admin is rebooting EC2 instance for contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		String awsInstId = contest.getAwsInstId();
		ec2ChangeStateService.rebootEc2Instance(awsInstId);
		return new MessageResponse("Instance " + awsInstId + " is rebooting!");
	}

	@RequestMapping(value = "/contests/ec2states", method = RequestMethod.POST)
	public Map<String, String> getContestEC2InstanceStates(
		@RequestBody AwsInstanceIdsRequest request
	) throws Exception {
		log.info("Admin is getting EC2 instance states");
		return ec2DescribeService.getInstanceStates(request.instanceIds);
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public PagedResults<UserModel> getUsers(
		@RequestParam(name = "limit", defaultValue = "10") Integer limit,
		@RequestParam(name = "lekPk", required = false) String lekPk,
		@RequestParam(name = "lekSk", required = false) String lekSk,
		@RequestParam(name = "email", required = false) String email
	) {
		log.info("Admin is getting users list");
		LastEvaluatedKey lek = LastEvaluatedKey.fromRequestParams(lekPk, lekSk);
		PagedResults<UserModel> response;
		if (email != null && !email.equals(""))
			response = userService.findAllOnPageWithEmail(lek, limit, email);
		else
			response = userService.findAllOnPage(lek, limit);
		return response;
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public UserModel createUser(@RequestBody UserInfoRequest request) throws Exception {
		log.info("Admin is creating new user");
		return userService.create(request.email, request.name, request.role);
	}

	@RequestMapping(value = "/users/{userEmail}", method = RequestMethod.GET)
	public UserWithContestsResponse getUser(@PathVariable String userEmail) throws Exception {
		log.info("Admin is getting user {}", userEmail);
		UserModel user = userService.findByEmailOrThrow(userEmail);
		List<UserContestModel> userContests = membershipService.findUserContests(user);
		return new UserWithContestsResponse(user, userContests);
	}

	@RequestMapping(value = "/users/{userEmail}", method = RequestMethod.POST)
	public UserModel updateUser(
		@PathVariable String userEmail,
		@RequestBody UserInfoRequest request
	) throws Exception {
		log.info("Admin is updating user {}", userEmail);
		UserModel user = userService.findByEmailOrThrow(userEmail);
		userService.update(user, request.name, request.role);
		return user;
	}

}
