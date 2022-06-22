package pro.siberians.ws.stream.repository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.ContestUserModel;

@Component
public class ContestUserRepository {

	private DynamoDBMapper mapper;

	@Autowired
	ContestUserRepository(AmazonDynamoDB dynamoDb) {
		this.mapper = new DynamoDBMapper(dynamoDb);
	}

	public Optional<ContestUserModel> find(String contestGuid, String userEmail) {
		ContestUserModel contestUser = mapper.load(
			ContestUserModel.class,
			ContestUserModel.PK_PREFIX + contestGuid,
			userEmail
		);
		return Optional.ofNullable(contestUser);
	}

	public List<ContestUserModel> findAll(String contestGuid) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		String pk = ContestUserModel.PK_PREFIX + contestGuid;
		eav.put(":pk", new AttributeValue().withS(pk));

		DynamoDBQueryExpression<ContestUserModel> qe = new DynamoDBQueryExpression<ContestUserModel>()
			.withKeyConditionExpression("pk = :pk")
			.withExpressionAttributeValues(eav);

		return mapper.query(ContestUserModel.class, qe);
	}

	public void save(ContestUserModel contestUser) {
		mapper.save(contestUser);
	}

	public void delete(ContestUserModel contestUser) {
		mapper.delete(contestUser);
	}

}
