package pro.siberians.ws.stream.model;

public class LastEvaluatedKey {

    private String pk;
    private String sk;

    public LastEvaluatedKey() {}

    public LastEvaluatedKey(String pk, String sk) {
        this.pk = pk;
        this.sk = sk;
    }

    public static LastEvaluatedKey fromRequestParams(String pk, String sk) {
        if (pk == null || sk == null) return null;
        return new LastEvaluatedKey(pk, sk);
    }

    public String getPk() { return pk; }
    public String getSk() { return sk; }

    public void setPk(String pk) { this.pk = pk; }
    public void setSk(String sk) { this.sk = sk; }

}
