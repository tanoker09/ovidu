package pro.siberians.ws.stream.repository;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.UserContestModel;

@Component
public class UserContestRepository {

	private DynamoDBMapper mapper;

	@Autowired
	UserContestRepository(AmazonDynamoDB dynamoDb) {
		this.mapper = new DynamoDBMapper(dynamoDb);
	}

	public Optional<UserContestModel> find(String userEmail, String contestStatus, String contestGuid) {
		UserContestModel userContest = mapper.load(
			UserContestModel.class,
			UserContestModel.PK_PREFIX + userEmail,
			contestStatus + "#" + contestGuid
		);
		return Optional.ofNullable(userContest);
	}

	public List<UserContestModel> findAll(String userEmail) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		String pk = UserContestModel.PK_PREFIX + userEmail;
		eav.put(":pk", new AttributeValue().withS(pk));

		DynamoDBQueryExpression<UserContestModel> qe = new DynamoDBQueryExpression<UserContestModel>()
			.withKeyConditionExpression("pk = :pk")
			.withExpressionAttributeValues(eav);

		return mapper.query(UserContestModel.class, qe);
	}

	public List<UserContestModel> findAllWithStatus(String userEmail, String contestStatus) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		String pk = UserContestModel.PK_PREFIX + userEmail;
		String sk = contestStatus + "#";
		eav.put(":pk", new AttributeValue().withS(pk));
		eav.put(":sk", new AttributeValue().withS(sk));

		DynamoDBQueryExpression<UserContestModel> qe = new DynamoDBQueryExpression<UserContestModel>()
			.withKeyConditionExpression("pk = :pk and begins_with(sk, :sk)")
			.withExpressionAttributeValues(eav);

		return mapper.query(UserContestModel.class, qe);
	}

	public void save(UserContestModel userContest) {
		mapper.save(userContest);
	}

	public void delete(UserContestModel userContest) {
		mapper.delete(userContest);
	}

}
