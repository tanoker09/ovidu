package pro.siberians.ws.stream.web.response.admin;

import java.util.List;

import pro.siberians.ws.stream.model.ContestModel;
import pro.siberians.ws.stream.model.ContestUserModel;

public class ContestWithUsersResponse {

	private ContestModel contest;
	private List<ContestUserModel> contestUsers;

	public ContestWithUsersResponse(ContestModel contest, List<ContestUserModel> contestUsers) {
		this.contest = contest;
		this.contestUsers = contestUsers;
	}

	public ContestModel getContest() {
		return contest;
	}

	public List<ContestUserModel> getContestUsers() {
		return contestUsers;
	}

}
