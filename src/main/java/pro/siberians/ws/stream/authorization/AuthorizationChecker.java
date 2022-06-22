package pro.siberians.ws.stream.authorization;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.exception.NotAuthorizedException;
import pro.siberians.ws.stream.exception.SystemLogicException;
import pro.siberians.ws.stream.model.*;
import pro.siberians.ws.stream.service.*;

@Component
public class AuthorizationChecker {

	@Autowired private MembershipService membershipService;

	public void userMustHaveRoleAtLeast(UserModel user, String role) throws NotAuthorizedException {
		String userRole = user.getRole();
		boolean ok = (
			(role.equals("contestant") && (userRole.equals("contestant") || userRole.equals("watcher") || userRole.equals("admin")))
			|| (role.equals("watcher") && (userRole.equals("watcher") || userRole.equals("admin")))
			|| (role.equals("admin") && userRole.equals("admin"))
		);
		if (!ok) throw new NotAuthorizedException("Your role doesn't allow this action!");
	}

	public void userMustBeAMemberOfContest(UserModel user, ContestModel contest) throws NotAuthorizedException
	{
		if (!membershipService.isMember(user, contest))
			throw new NotAuthorizedException("User has no access to this contest!");
	}

	public void contestMustBeActive(ContestModel contest) throws SystemLogicException {
		if (!contest.getStatus().equals("active"))
			throw new SystemLogicException("Contest must be active!");
	}
}
