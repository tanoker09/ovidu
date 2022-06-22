package pro.siberians.ws.stream.web.response.admin;

import java.util.List;

import pro.siberians.ws.stream.model.UserModel;
import pro.siberians.ws.stream.model.UserContestModel;

public class UserWithContestsResponse {

	private UserModel user;
	private List<UserContestModel> userContests;

	public UserWithContestsResponse(UserModel user, List<UserContestModel> userContests) {
		this.user = user;
		this.userContests = userContests;
	}

	public UserModel getUser() {
		return user;
	}

	public List<UserContestModel> getUserContests() {
		return userContests;
	}

}
