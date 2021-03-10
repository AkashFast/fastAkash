package prism.akash.container.charEngnie;

import prism.akash.container.BaseData;
import prism.akash.container.charEngnie.chartEnum.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * E-Chars图表引擎
 * ※主要用于处理图表数据
 * <p>
 * TODO : 工具：图表数据处理引擎
 *
 * @author HaoNan Yan
 */
public class chartEngine implements Serializable {

    private static final long serialVersionUID = 1L;

    BaseData chartData = null;         //经由chartEngine处理的可视化数据
    BaseData settingData = null;       //settings图表基本设置
    BaseData extend = null;            //charts图表拓展 - label标签设置
    BaseData legendData = null;
    List<BaseData> coreData = null;    //核心数据 - 经由sqlEngine查询后返回的数据
    chartType chartType = null;

    /**
     * 初始化chart图表引擎
     *
     * @param chartTyped 图表类型
     */
    public chartEngine(chartType chartTyped) {
        extend = new BaseData();
        settingData = new BaseData();
        legendData = new BaseData();
        chartType = chartTyped;
        chartData = new BaseData();
        coreData = new ArrayList<>();
    }

    /**
     * settings 「设置数据展示列」
     *
     * @param column
     * @return
     */
    public chartEngine setColumn(String... column) {
        int i = 0;
        // 最少需要两个值
        if (column.length > 1) {
            String[] metrics = new String[column.length - 1];
            String[] newCol = new String[column.length];
            for (String col : column) {
                String[] co = col.split("\\#");
                // 第1个参数默认为维度
                if (i == 0) {
                    this.dimension(co[0]);
                } else {
                    metrics[i - 1] = co[0];
                }
                // 如果存在别名
                if (col.indexOf("#") > -1) {
                    this.line_labelMap(co[0], co[1]);
                }
                newCol[i] = co[0];
                i++;
            }
            this.metrics(metrics);
            this.chartData.put("columns", newCol);
        }
        return this;
    }

    /**
     * out  「输出标准的Chart数据格式」
     *
     * @param needSort 是否需要对数据排序「建议日期类数值类进行排序」
     * @return
     */
    public BaseData parseChart(boolean needSort) {
        BaseData result = new BaseData();
        result.put("extend", extend);
        result.put("settingData", settingData);

        // 若当前处理模式为主副模式
        if (parseType.chiefDeputy == chartData.get("parseType")) {
            data_deputyParse(needSort);
        }
        // 若当前处理模式为非线性模式
        if (parseType.mistakeLine == chartData.get("parseType")) {
            data_mistakeLineParse();
        }
        removeChartDataParms();
        chartData.put("rows", coreData);
        result.put("charData", chartData);
        result.put("legendData",legendData);
        return result;
    }


    private String standardData() {
        String chartType = this.chartType.toString();
        return chartType;
    }

    /**
     * out 「移除chartData不需要的数据」
     *
     * @return
     */
    private chartEngine removeChartDataParms() {
        chartData.remove("chiefDeputy");
        chartData.remove("parseType");
        chartData.remove("patchKey");
        chartData.remove("outKey");
        chartData.remove("deputy");
        chartData.remove("valueAlias");
        return this;
    }


    /**
     * coreData  「设置主数据源」
     * TODO 系统默认识别standard标准数据类型,如需对数据进行二次处理请调用data_parseType进行设置
     *
     * @param data
     * @return
     */
    public chartEngine data_setData(List<BaseData> data) {
        coreData = data;
        if (data.size() == 0) {
            settingData.put("dataEmpty", true);
        }
        //默认为标准格式
        data_parseType(parseType.standard);
        return this;
    }

    /**
     * coreData  「待处理的数据格式类型」
     * TODO 标准模式：与column完全匹配的数据格式「默认√」
     * TODO 主副模式：需要根据特定字段进行数据再处理的数据格式
     * ↓主副模式给定的数据格式
     * - date  type val
     * - 1/1    0    1
     * - 1/1    1    2
     * ===> 期待输出的最终结果:
     * -date type0 type1
     * - 1/1   1     2
     *
     * @param parseType
     * @return
     */
    public chartEngine data_parseType(parseType parseType) {
        chartData.put("parseType", parseType);
        return this;
    }


    /**
     * coreData 「设置需要二次标化处理的主数据列key值」
     * TODO 仅在主副模式使用 「chiefDeputy」
     *
     * @param key
     * @return
     */
    public chartEngine data_patchKey(String key) {
        chartData.put("patchKey", key);
        return this;
    }

    /**
     * coreData 「设置需要二次标化处理的待输出数据列key值」
     * TODO 仅在主副模式使用 「chiefDeputy」
     *
     * @param key
     * @return
     */
    public chartEngine data_outKey(String key) {
        chartData.put("outKey", key);
        return this;
    }


    /**
     * coreData 「设置与期待输出项匹配的标化值」
     * TODO 仅在主副模式使用 「chiefDeputy」
     *
     * @param DeputyPatchValue 标化值
     * @param colKey           column中的期待输出列
     * @return
     */
    public chartEngine data_setDeputy(String DeputyPatchValue, String colKey) {
        Object setDeputy = chartData.get("deputy");
        BaseData deputys = setDeputy == null ? new BaseData() : (BaseData) setDeputy;
        deputys.put(DeputyPatchValue, colKey);
        chartData.put("deputy", deputys);
        return this;
    }


    /**
     * coreData - 「非线性模式数据标化处理」
     *
     * @return
     */
    private chartEngine data_mistakeLineParse() {
        String patchKey = chartData.getString("patchKey");
        String outKey = chartData.getString("outKey");

        List<BaseData> newData = new ArrayList<>();

        for (BaseData data : coreData) {
            BaseData nData = new BaseData();
            nData.put(patchKey,valueOfAlias(data.get(patchKey)));
            nData.put(outKey,data.get(outKey));
            newData.add(nData);
        }
        coreData = newData;
        return this;
    }

    /**
     * 内部方法：非线性数据主键「指标」等值替换
     * @param patchValue
     * @return
     */
    private String valueOfAlias(Object patchValue) {
        String result = "";
        Object setValueAlias = chartData.get("valueAlias");
        BaseData valueAlias = setValueAlias == null ? new BaseData() : (BaseData) setValueAlias;
        Iterator<Map.Entry<String, String>> entries = valueAlias.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            if (entry.getKey().equals(patchValue + "")) {
                result = entry.getValue();
                break;
            }
        }
        return result;
    }

    /**
     * coreData - 「非线性数据别名设置」
     *
     * @param values
     * @param alias
     * @return
     */
    public chartEngine mistakeLine_valueAlias(Object values, String alias) {
        Object setValueAlias = chartData.get("valueAlias");
        BaseData valueAlias = setValueAlias == null ? new BaseData() : (BaseData) setValueAlias;
        valueAlias.put(values, alias);
        chartData.put("valueAlias", valueAlias);
        return this;
    }

    /**
     * coreData - 「主副模式数据标化处理」
     *
     * @param needSort 是否需要对数据排序「建议日期类数值类进行排序」
     * @return
     */
    private chartEngine data_deputyParse(boolean needSort) {
        List<BaseData> newData = new ArrayList<>();
        //获取维度及指标列数据
        String[] dimension = (String[]) settingData.get("dimension");
        String[] metrics = (String[]) settingData.get("metrics");
        String patchKey = chartData.getString("patchKey");
        String outKey = chartData.getString("outKey");
        BaseData deputy = chartData.get("deputy") == null ? null : (BaseData) chartData.get("deputy");

        //关键参数是否全部符合要求?
        if (deputy != null && !patchKey.isEmpty() && !outKey.isEmpty()) {
            //获取X轴主维度值
            Map<String, List<BaseData>> xAixs = coreData.stream().collect(Collectors.groupingBy(e -> e.get(dimension[0]) + ""));
            //迭代处理
            Iterator<Map.Entry<String, List<BaseData>>> entries = xAixs.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, List<BaseData>> entry = entries.next();
                List<BaseData> metricsList = entry.getValue();
                BaseData data = new BaseData();
                //设置横轴属性
                data.put(dimension[0], entry.getKey());
                //纵轴属性匹配
                for (String met : metrics) {
                    for (String mt : met.split(",")) {
                        if (!mt.isEmpty()) {
                            Object patch = isPatch(deputy, mt);
                            if (patch != null) {
                                // 对数据进行匹配处理
                                List<BaseData> re = metricsList.stream().filter(e -> (e.get(patchKey) + "").equals(patch)).collect(Collectors.toList());
                                int val = 0;
                                for (BaseData r : re) {
                                    val += r.getInter(outKey);
                                }
                                //设置纵轴数据列属性
                                data.put(mt, val);
                            }
                        }
                    }
                }
                newData.add(data);
            }
        }
        if (needSort) {
            newData.sort((BaseData o1, BaseData o2) -> o1.getString(dimension[0]).compareTo(o2.getString(dimension[0])));
        }
        coreData = newData;

        return this;
    }

    /**
     * coreData 「判断当前数据列是否有效并返回key值」
     *
     * @param deputy
     * @param metric
     * @return
     */
    private Object isPatch(BaseData deputy, String metric) {
        Object result = null;
        Iterator<Map.Entry<String, String>> entries = deputy.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            String value = entry.getValue();
            if (value.equals(metric)) {
                result = entry.getKey();
                break;
            }
        }
        return result;
    }

    /**
     * extend  「设置图形上的文本标签是否展示，展示位置，距图形距离」
     *
     * @param extendTag    是否展示
     * @param positionType 标签位置
     * @param distance     距图形距离(px)
     * @return
     */
    public chartEngine extend_label(boolean extendTag, positionType positionType, Integer distance) {
        BaseData normal = new BaseData();
        normal.put("show", extendTag);
        if (positionType != null) {
            normal.put("position", positionType);
            normal.put("distance", distance == null ? 0 : distance);
        }
        normal.put("distance", distance == null ? 0 : distance);
        BaseData label = new BaseData();
        label.put("normal", normal);
        BaseData series = new BaseData();
        series.put("label", label);
        extend.put("series", series);
        return this;
    }


    /**
     * settings 「数据指标（纵轴）」
     * *
     *
     * @param metrics
     * @return
     */
    public chartEngine metrics(String... metrics) {
        settingData.put("metrics", metrics);
        return this;
    }

    /**
     * settings 「数据维度（横轴）」
     * * X轴
     *
     * @param dimension
     * @return
     */
    public chartEngine dimension(String... dimension) {
        settingData.put("dimension", dimension);
        return this;
    }

    /**
     * settings 「横轴数据类型」
     *
     * @param xAxisType
     * @return
     */
    public chartEngine xAxisType(xAxisType xAxisType) {
        settingData.put("xAxisType", xAxisType);
        return this;
    }

    /**
     * settings 「左右坐标纵轴数据类型」
     *
     * @param yAxisType
     * @return
     */
    public chartEngine yAxisType(yAxisType... yAxisType) {
        settingData.put("yAxisType", yAxisType);
        return this;
    }

    /**
     * settings 「左右纵坐标轴标题」
     *
     * @param yAxisName
     * @return
     */
    public chartEngine yAxisName(String... yAxisName) {
        settingData.put("yAxisName", yAxisName);
        return this;
    }


    /**
     * settings  「指标所在的轴」
     *
     * @param axisSiteType 指标所在轴
     * @param key          对应指标
     * @return
     */
    public chartEngine axisSite(axisSiteType axisSiteType, String... key) {
        BaseData axisSite = new BaseData();
        axisSite.put(axisSiteType, key);
        settingData.put("axisSite", axisSite);
        return this;
    }

    /**
     * settings  「数据类型为percent时保留的位数」
     *
     * @param digit
     * @return
     */
    public chartEngine digit(int digit) {
        settingData.put("digit", digit);
        return this;
    }

    /**
     * settings  「图例设置」
     *
     * @param isScroll     是否需要滚动
     * @param top          绝对定位-顶部偏移量
     * @param right        绝对定位-左侧偏移量
     * @param bottom       绝对定位-底部偏移量
     * @param left         绝对定位-右侧偏移量
     * @param isHorizontal 是否为水平「false为垂直」
     * @return
     */
    public chartEngine legend(boolean isScroll, Object top, Object right, Object bottom, Object left, boolean isHorizontal) {
        BaseData legend = new BaseData();
        legend.put("type", isScroll ? "scroll" : "plain");
        if (top != null) {
            legend.put("top", top);
        }
        if (right != null) {
            legend.put("right", right);
        }
        if (bottom != null) {
            legend.put("bottom", bottom);
        }
        if (left != null) {
            legend.put("left", left);
        }
        legend.put("orient", isHorizontal ? "horizontal" : "vertical");
        legendData.put("legend", legend);
        return this;
    }


    /**
     * settings 「堆叠选项」
     * TODO 限 「折线图 柱状图 条形图」
     *
     * @param key        堆叠后的显示值
     * @param stackValue 被堆叠的数据字段
     * @return
     */
    public chartEngine line_stack(String key, String... stackValue) {
        BaseData stack = new BaseData();
        stack.put(key, stackValue);

        settingData.put("stack", stack);
        settingData.put("area", true);
        return this;
    }

    /**
     * settings 「指标的别名，同时作用于提示框和图例」
     * TODO 限 「折线图 柱状图 条形图」
     *
     * @param key   指标名
     * @param alias 指标别名
     * @return
     */
    public chartEngine line_labelMap(String key, String alias) {
        Object labelMapData = settingData.get("labelMap");
        BaseData labelMap = labelMapData == null ? new BaseData() : (BaseData) labelMapData;
        labelMap.put(key, alias);
        settingData.put("labelMap", labelMap);
        return this;
    }

    /**
     * settings 「图表上方图例的别名」
     * TODO 限 「折线图 柱状图 条形图」
     *
     * @param key   指标名
     * @param alias 指标别名
     * @return
     */
    public chartEngine line_legendName(String key, String alias) {
        Object legendNameData = settingData.get("legendName");
        BaseData legendName = legendNameData == null ? new BaseData() : (BaseData) legendNameData;
        legendName.put(key, alias);
        settingData.put("legendName", legendName);
        return this;
    }

    /**
     * settings 「横纵轴是否是脱离 0 值比例」
     * TODO 限 「折线图 柱状图 条形图」
     *
     * @param x 横轴设置
     * @param y 纵轴设置
     * @return
     */
    public chartEngine line_scale(boolean x, boolean y) {
        List<Boolean> scale = new ArrayList<>();
        scale.add(x);
        scale.add(y);
        settingData.put("scale", scale.toArray());
        return this;
    }

    /**
     * settings 「透明度」
     * TODO 限 「柱状图 条形图」
     * 若数值型横轴显示多指标，建议将本职调整为0.5
     *
     * @param opacity 设置为折线图的指标
     * @return
     */
    public chartEngine line_opacity(int opacity) {
        settingData.put("opacity", opacity);
        return this;
    }

    /**
     * settings 「将指标设置为折线图」
     * TODO 限 「柱状图」
     *
     * @param showLineCol 设置为折线图的指标
     * @return
     */
    public chartEngine histogram_showLine(String... showLineCol) {
        settingData.put("showLine", showLineCol);
        return this;
    }

    /**
     * settings 「数据类型」
     * TODO 限 「饼图 环图 瀑布图 漏斗图 雷达图」
     *
     * @param xAxisType
     * @return
     */
    public chartEngine special_dataType(xAxisType xAxisType) {
        settingData.put("dataType", xAxisType);
        return this;
    }

    /**
     * settings 「legend显示数量限制」
     * TODO 限 「饼图 环图」
     *
     * @param legendLimit
     * @return
     */
    public chartEngine pie_legendLimit(int legendLimit) {
        settingData.put("legendLimit", legendLimit);
        return this;
    }

    /**
     * settings 「选中模式」
     * TODO 限 「饼图 环图」
     *
     * @param isSingle true为单选/false为多选
     * @return
     */
    public chartEngine pie_selectedMode(boolean isSingle) {
        settingData.put("selectedMode", isSingle ? "single" : "multiple");
        return this;
    }

    /**
     * settings 「设置为南丁格尔玫瑰图」
     * TODO 限 「饼图 环图」
     *
     * @param isRose
     * @return
     */
    public chartEngine pie_rose(boolean isRose) {
        if (isRose) {
            settingData.put("roseType", "radius");
        }
        return this;
    }

    /**
     * settings「限制饼图及环形图展示数量」
     * TODO 限 「饼图 环图」
     *
     * @param limitShowNum
     * @return
     */
    public chartEngine pie_limitShowNum(int limitShowNum) {
        settingData.put("limitShowNum", limitShowNum);
        return this;
    }

    /**
     * settings「多圆饼图」
     * * 每次调用均视为为一个完成的饼图设置
     * TODO 限 「饼图」
     *
     * @param level
     * @return
     */
    public chartEngine pie_only_level(String... level) {
        Object levelData = settingData.get("level");
        List<String[]> levelList = levelData == null ? new ArrayList<>() : (List<String[]>) levelData;
        levelList.add(level);
        settingData.put("level", levelList);
        return this;
    }

    /**
     * settings 「设置数据总量」
     * TODO 限 「瀑布图」
     *
     * @param totalNum
     * @return
     */
    public chartEngine water_totalNum(int totalNum) {
        settingData.put("totalNum", totalNum);
        return this;
    }

    /**
     * settings「总量的显示文案」
     * TODO 限 「瀑布图」
     *
     * @param totalName
     * @return
     */
    public chartEngine water_totalName(String totalName) {
        settingData.put("totalName", totalName);
        return this;
    }

    /**
     * settings「剩余的显示文案」
     * TODO 限 「瀑布图」
     *
     * @param remainName
     * @return
     */
    public chartEngine water_remainName(String remainName) {
        settingData.put("remainName", remainName);
        return this;
    }

    /**
     * settings「数据显示顺序」
     * TODO 限 「漏斗图」
     *
     * @param sequence
     * @return
     */
    public chartEngine funnel_sequence(String... sequence) {
        settingData.put("sequence", sequence);
        return this;
    }

    /**
     * settings「是否启用金字塔模式」
     * TODO 限 「漏斗图」
     *
     * @param enable
     * @return
     */
    public chartEngine funnel_ascending(boolean enable) {
        settingData.put("ascending", enable);
        return this;
    }

    /**
     * settings「是否自动按照数值大小排序」
     * TODO 限 「漏斗图」
     *
     * @param enable
     * @return
     */
    public chartEngine funnel_useDefaultOrder(boolean enable) {
        settingData.put("useDefaultOrder", enable);
        return this;
    }

    /**
     * settings「是否过滤指标为0的数据」
     * TODO 限 「漏斗图」
     *
     * @param enable
     * @return
     */
    public chartEngine funnel_filterZero(boolean enable) {
        settingData.put("filterZero", enable);
        return this;
    }

}
