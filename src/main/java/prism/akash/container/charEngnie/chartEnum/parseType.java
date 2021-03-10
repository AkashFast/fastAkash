package prism.akash.container.charEngnie.chartEnum;

/**
 * 设置主数据源格式
 */
public enum parseType {

    standard("standard"),//标准数据格式
    mistakeLine("mistakeLine"),//非线性模式「适用于饼图、环图、瀑布图及漏斗图」
    chiefDeputy("chiefDeputy");//主副列数据格式

    private String value;

    parseType(String value) {
        this.value = value;
    }

    //获取枚举对象
    public static parseType getparseType(String value) {
        if (value != null) {
            for (parseType co : parseType.values()) {
                if (value.equals(co.name())) {
                    return co;
                }
            }
        }
        return standard;
    }
}
