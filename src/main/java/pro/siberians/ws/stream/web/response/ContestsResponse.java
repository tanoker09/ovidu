package pro.siberians.ws.stream.web.response;

import java.util.ArrayList;
import java.util.List;

import pro.siberians.ws.stream.model.UserContestModel;

public class ContestsResponse {

	private List<ContestResponse> contests;

	public ContestsResponse(List<UserContestModel> userContests) {
		this.contests = new ArrayList<ContestResponse>();
		for (UserContestModel userContest : userContests) {
			this.contests.add(new ContestResponse(userContest));
		}
	}

	public List<ContestResponse> getContests() {
		return contests;
	}

}
