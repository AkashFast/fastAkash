package prism.akash.container.charEngnie.chartEnum;

/**
 * 左右坐标轴数据类型「X轴」
 */
public enum yAxisType {

    KMB("KMB"),
    normal("normal"),
    percent("percent");

    private String value;

    yAxisType(String value) {
        this.value = value;
    }

    public String getyAxisType() {
        return value;
    }

    //获取枚举对象
    public static yAxisType getyAxisType(String value) {
        if (value != null) {
            for (yAxisType co : yAxisType.values()) {
                if (value.equals(co.name())) {
                    return co;
                }
            }
        }
        return normal;
    }
}
