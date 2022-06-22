package pro.siberians.ws.stream.repository;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import pro.siberians.ws.stream.model.LastEvaluatedKey;

public class LastEvaluatedKeyMapper {
    public static LastEvaluatedKey fromMap(Map<String, AttributeValue> map) {
        if (map == null) return null;
        LastEvaluatedKey lek = new LastEvaluatedKey();
        lek.setPk(map.get("pk").getS());
        lek.setSk(map.get("sk").getS());
        return lek;
    }

    public static Map<String, AttributeValue> toMap(LastEvaluatedKey lek) {
        if (lek == null) return null;
        Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
        map.put("pk", new AttributeValue().withS(lek.getPk()));
        map.put("sk", new AttributeValue().withS(lek.getSk()));
        return map;
    }
}
