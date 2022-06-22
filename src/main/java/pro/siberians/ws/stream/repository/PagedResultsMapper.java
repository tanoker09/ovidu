package pro.siberians.ws.stream.repository;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import pro.siberians.ws.stream.model.PagedResults;
import pro.siberians.ws.stream.model.LastEvaluatedKey;

public class PagedResultsMapper<T> {
    public PagedResults<T> fromPage(QueryResultPage<T> page) {
        Map<String, AttributeValue> lekMap = page.getLastEvaluatedKey();
        LastEvaluatedKey lek = LastEvaluatedKeyMapper.fromMap(lekMap);

        PagedResults<T> ret = new PagedResults<T>();
        ret.setResults(page.getResults());
        ret.setLastEvaluatedKey(lek);
        return ret;
    }
}
