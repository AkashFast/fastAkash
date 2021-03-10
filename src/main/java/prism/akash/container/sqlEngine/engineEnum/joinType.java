package prism.akash.container.sqlEngine.engineEnum;

/**
 * Enum Join Pattern (表关系枚举)
 */
public enum joinType {

    N(" , "),//普通形式的全连接
    R(" right join "),
    L(" left join "),
    I(" inner join "),
    C(" join "),
    S(" straight_join "),
    RO(" right outre join "),
    LO(" left outre join ");

    private String value;

    joinType(String value){
        this.value = value;
    }

    public String getJoinType(){
        return value;
    }

    //获取枚举对象
    public static joinType getJoinType(String value){
        if (value != null) {
            for (joinType jo : joinType.values()) {
                if (value.equals(jo.name())) {
                    return jo;
                }
            }
        }
        return L;
    }
}
