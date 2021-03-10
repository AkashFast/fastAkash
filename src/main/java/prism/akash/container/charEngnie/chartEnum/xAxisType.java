package prism.akash.container.charEngnie.chartEnum;

/**
 * 左右坐标轴数据类型「X轴」
 */
public enum xAxisType {

    category("category"),
    value("value"),
    log("log"),
    time("time");

    private String val;

    xAxisType(String value) {
        this.val = value;
    }

    //获取枚举对象
    public static xAxisType getxAxisType(String value) {
        if (value != null) {
            for (xAxisType co : xAxisType.values()) {
                if (value.equals(co.name())) {
                    return co;
                }
            }
        }
        return category;
    }
}
