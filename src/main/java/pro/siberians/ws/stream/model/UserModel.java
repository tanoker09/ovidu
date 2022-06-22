package pro.siberians.ws.stream.model;

import java.util.Date;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "contest-streamer-data")
public class UserModel {

	public static String PK = "USER";

	private String pk = PK;
	private String sk;

	private String name;
	private String role;

	private Date createdAt;
	private Date updatedAt;

	// Primary key

	@DynamoDBHashKey public String getPk() { return pk; }
	@DynamoDBRangeKey public String getSk() { return sk; }

	public void setPk(String pk) { this.pk = pk; }
	public void setSk(String sk) { this.sk = sk; }

	// Working with user email

	@DynamoDBIgnore public String getEmail() { return sk; }

	public void setEmail(String email) { this.sk = email; }

	// Other attribute getters

	@DynamoDBAttribute public String getName() { return name; }
	@DynamoDBAttribute public String getRole() { return role; }

	@DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.CREATE)
	public Date getCreatedAt() { return createdAt; }

	@DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.ALWAYS)
	public Date getUpdatedAt() { return updatedAt; }

	// Other attribute setters

	public void setName(String name) { this.name = name; }
	public void setRole(String role) { this.role = role; }

	public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
	public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

}
