package pro.siberians.ws.stream.web.response;

import java.util.Date;

import pro.siberians.ws.stream.model.UserModel;

public class UserResponse {

	public String email;
	public String name;
	public String role;
	public Date createdAt;
	public Date updatedAt;

	public UserResponse(UserModel user) {
		this.email = user.getEmail();
		this.name = user.getName();
		this.role = user.getRole();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
	}

}
