package pro.siberians.ws.stream.model;

import java.util.List;

public class PagedResults<T> {

    private List<T> results;
    private LastEvaluatedKey lastEvaluatedKey;

    public List<T> getResults() { return results; }
    public LastEvaluatedKey getLastEvaluatedKey() { return lastEvaluatedKey; }

    public void setResults(List<T> results) { this.results = results; }
    public void setLastEvaluatedKey(LastEvaluatedKey lastEvaluatedKey) { this.lastEvaluatedKey = lastEvaluatedKey; }

}
