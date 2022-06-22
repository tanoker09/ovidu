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

import pro.siberians.ws.stream.model.ContestModel;
import pro.siberians.ws.stream.model.LastEvaluatedKey;
import pro.siberians.ws.stream.model.PagedResults;

@Component
public class ContestRepository {

	private DynamoDBMapper mapper;

	@Autowired
	ContestRepository(AmazonDynamoDB dynamoDb) {
		this.mapper = new DynamoDBMapper(dynamoDb);
	}

	public Optional<ContestModel> find(String guid) {
		ContestModel contest = mapper.load(ContestModel.class, ContestModel.PK, guid);
		return Optional.ofNullable(contest);
	}

	public PagedResults<ContestModel> findAllOnPage(LastEvaluatedKey lek, Integer limit) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":pk", new AttributeValue().withS(ContestModel.PK));

		Map<String, AttributeValue> lekMap = LastEvaluatedKeyMapper.toMap(lek);

		DynamoDBQueryExpression<ContestModel> qe = new DynamoDBQueryExpression<ContestModel>()
			.withKeyConditionExpression("pk = :pk")
			.withExpressionAttributeValues(eav)
			.withExclusiveStartKey(lekMap)
			.withScanIndexForward(false);

		QueryResultPage<ContestModel> page = mapper.queryPage(ContestModel.class, qe);
		PagedResults<ContestModel> ret = new PagedResultsMapper<ContestModel>().fromPage(page);
		return ret;
	}

	public void save(ContestModel contest) {
		mapper.save(contest);
	}
}
