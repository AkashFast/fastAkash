package prism.akash.container.charEngnie.chartEnum;

public enum axisSiteType {


    right("right"),
    left("left");

    private String value;

    axisSiteType(String value) {
        this.value = value;
    }

    public String getaxisSiteType() {
        return value;
    }

    //获取枚举对象
    public static axisSiteType getaxisSiteType(String value) {
        if (value != null) {
            for (axisSiteType co : axisSiteType.values()) {
                if (value.equals(co.name())) {
                    return co;
                }
            }
        }
        return left;
    }

}
