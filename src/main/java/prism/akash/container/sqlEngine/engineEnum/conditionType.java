package prism.akash.container.sqlEngine.engineEnum;

public enum conditionType {

    EQ(" = "),//等于
    LIKE(" LIKE "),//模糊查询
    NOT_LIKE(" NOT LIKE "),//反向模糊查询
    LIKE_BINARY(" LIKE BINARY "),//忽略中英文大小写匹配
    LIKE_ESCAPE(" LIKE ESCAPE"),//模糊查询(通配符转义)
    NOT_LIKE_ESCAPE(" NOT LIKE ESCAPE"),//反向模糊查询(通配符转义)
    LIKE_BINARY_ESCAPE(" LIKE BINARY ESCAPE"),//忽略中英文大小写匹配(通配符转义)
    GT(" > "),//大于
    GTEQ("  >= "),//大于等于
    LT("  < "),//小于
    LTEQ("  <=  "),//小于等于
    NEQ(" <> "),//不等于
    IN(" IN "),//包含
    NOTIN(" NOT IN "),//不包含
    ISNULL(" IS NULL "),//为空
    NOTNULL(" IS NOT NULL "),//不为空
    QUERY_ROLE(" QUERY_ROLE "),//调用函数方法
    BET(" BETWEEN ");//在……区间,Between and……


    private String value;

    conditionType(String value){
        this.value = value;
    }

    public String getconditionType(){
        return value;
    }

    //获取枚举对象
    public static conditionType getconditionType(String value){
        if (value != null) {
            for (conditionType co : conditionType.values()) {
                if (value.equals(co.name())) {
                    return co;
                }
            }
        }
        return EQ;
    }
}
