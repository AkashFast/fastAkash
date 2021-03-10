package prism.akash.container.charEngnie.chartEnum;

/**
 * 设置主数据源格式
 */
public enum chartType {

    line("line"),//折线
    bar("bar"),//条形
    pie("pie"),//饼
    ring("ring"),//环形
    waterfall("waterfall"),//瀑布
    funnel("funnel"),//漏斗
    radar("radar"),//雷达
    histogram("histogram");//柱状

    private String value;

    chartType(String value) {
        this.value = value;
    }

    //获取枚举对象
    public static chartType getchartType(String value) {
        if (value != null) {
            for (chartType co : chartType.values()) {
                if (value.equals(co.name())) {
                    return co;
                }
            }
        }
        return line;
    }
}
