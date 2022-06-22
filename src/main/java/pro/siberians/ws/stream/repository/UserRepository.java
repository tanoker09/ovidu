package pro.siberians.ws.stream.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.LastEvaluatedKey;
import pro.siberians.ws.stream.model.PagedResults;
import pro.siberians.ws.stream.model.UserModel;

@Component
public class UserRepository {

	private DynamoDBMapper mapper;

	@Autowired
	UserRepository(AmazonDynamoDB dynamoDb) {
		this.mapper = new DynamoDBMapper(dynamoDb);
	}

	public Optional<UserModel> find(String email) {
		UserModel user = mapper.load(UserModel.class, UserModel.PK, email);
		return Optional.ofNullable(user);
	}

	public PagedResults<UserModel> findAllOnPage(LastEvaluatedKey lek, Integer limit) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":pk", new AttributeValue().withS(UserModel.PK));

		Map<String, AttributeValue> lekMap = LastEvaluatedKeyMapper.toMap(lek);

		DynamoDBQueryExpression<UserModel> qe = new DynamoDBQueryExpression<UserModel>()
			.withKeyConditionExpression("pk = :pk")
			.withExpressionAttributeValues(eav)
			.withExclusiveStartKey(lekMap)
			.withLimit(limit);

		QueryResultPage<UserModel> page = mapper.queryPage(UserModel.class, qe);
		PagedResults<UserModel> ret = new PagedResultsMapper<UserModel>().fromPage(page);
		return ret;
	}

	public PagedResults<UserModel> findAllOnPageWithEmail(LastEvaluatedKey lek, Integer limit, String email) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":pk", new AttributeValue().withS(UserModel.PK));
		eav.put(":sk", new AttributeValue().withS(email));

		Map<String, AttributeValue> lekMap = LastEvaluatedKeyMapper.toMap(lek);

		DynamoDBQueryExpression<UserModel> qe = new DynamoDBQueryExpression<UserModel>()
			.withKeyConditionExpression("pk = :pk and begins_with(sk, :sk)")
			.withExpressionAttributeValues(eav)
			.withExclusiveStartKey(lekMap)
			.withLimit(limit);

		QueryResultPage<UserModel> page = mapper.queryPage(UserModel.class, qe);
		PagedResults<UserModel> ret = new PagedResultsMapper<UserModel>().fromPage(page);
		return ret;
	}

	public void save(UserModel user) {
		mapper.save(user);
	}
}
