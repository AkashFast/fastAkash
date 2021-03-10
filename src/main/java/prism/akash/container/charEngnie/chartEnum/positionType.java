package prism.akash.container.charEngnie.chartEnum;

/**
 * 设置标签显示位置类型
 */
public enum positionType {

    top("top"),
    left("left"),
    right("right"),
    bottom("bottom"),
    inside("inside"),
    insideLeft("insideLeft"),
    insideRight("insideRight"),
    insideTop("insideTop"),
    insideBottom("insideBottom"),
    insideTopLeft("insideTopLeft"),
    insideBottomLeft("insideBottomLeft"),
    insideTopRight("insideTopRight"),
    insideBottomRight("insideBottomRight");

    private String value;

    positionType(String value) {
        this.value = value;
    }

    //获取枚举对象
    public static positionType getpositionType(String value) {
        if (value != null) {
            for (positionType co : positionType.values()) {
                if (value.equals(co.name())) {
                    return co;
                }
            }
        }
        return top;
    }
}
