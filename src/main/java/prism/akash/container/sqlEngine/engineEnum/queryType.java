package prism.akash.container.sqlEngine.engineEnum;

public enum queryType {

    and("and"),
    or("or"),
    andMerge("andMerge"),
    orMerge("orMerge"),
    andMergeEnd("andMergeEnd"),
    orMergeEnd("orMergeEnd");

    private String value;

    queryType(String value){
        this.value = value;
    }

    public String getQueryType(){
        return value;
    }

    //获取枚举对象
    public static queryType getQueryType(String value){
        if (value != null) {
            for (queryType qo : queryType.values()) {
                if (value.equals(qo.name())) {
                    return qo;
                }
            }
        }
        return and;
    }
}
