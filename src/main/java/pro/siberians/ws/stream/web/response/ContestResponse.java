package pro.siberians.ws.stream.web.response;

import java.util.Date;

import pro.siberians.ws.stream.model.UserContestModel;

public class ContestResponse {

	public String guid;
	public String name;
	public String status;
	public Date startAt;
	public Date finishAt;

	public ContestResponse(UserContestModel userContest) {
		this.guid = userContest.getContestGuid();
		this.name = userContest.getContestName();
		this.status = userContest.getContestStatus();
		this.startAt = userContest.getContestStartAt();
		this.finishAt = userContest.getContestFinishAt();
	}

}
