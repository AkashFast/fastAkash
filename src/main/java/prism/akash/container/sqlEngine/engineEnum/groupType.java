package prism.akash.container.sqlEngine.engineEnum;

public enum groupType {

    DEF("DEF"),//默认
    PATCH_FILED("FILED"),//字段匹配
    PRIVACY_TEL(" PRIVACY_TEL "),//隐私字段
    PRIVACY_IDCARD(" PRIVACY_ID "),//隐私字段
    DISTINCT(" DISTINCT"),//去重
    AVG(" AVG"),//返回某列的平均值
    COUNTDIS(" COUNTDIA"),//TODO:行数，数据去重
    COUNT(" COUNT"),//返回某列的行数
    MAX(" MAX"),//返回某列的最大值
    MIN(" MIN"),//返回某列的最小值
    SUM(" SUM");//返回某列值之和


    private String value;

    groupType(String value){
        this.value = value;
    }

    public String getgroupType(){
        return value;
    }

    //获取枚举对象
    public static groupType getgroupType(String value){
        if (value != null){
            for (groupType go:groupType.values()) {
                if(value.equals(go.name())){
                    return go;
                }
            }
        }
        return DEF;
    }
}
