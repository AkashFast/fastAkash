package prism.akash.container.sqlEngine.engineEnum;

public enum sortType {

    ASC("ASC"),
    DESC("DESC"),
    UTF_ASC("UASC"),
    UTF_DESC("UDESC");

    private String value;

    sortType(String value){
        this.value = value;
    }

    public String getSortType(){
        return value;
    }

    //获取枚举对象
    public static sortType getSortType(String value){
        if (value != null) {
            for (sortType so : sortType.values()) {
                if (value.equals(so.name())) {
                    return so;
                }
            }
        }
        return DESC;
    }

}
