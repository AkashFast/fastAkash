package prism.akash.schema.analysis;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prism.akash.container.BaseData;
import prism.akash.container.charEngnie.chartEngine;
import prism.akash.container.charEngnie.chartEnum.chartType;
import prism.akash.container.charEngnie.chartEnum.parseType;
import prism.akash.container.sqlEngine.engineEnum.conditionType;
import prism.akash.container.sqlEngine.engineEnum.groupType;
import prism.akash.container.sqlEngine.engineEnum.queryType;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.schema.BaseSchema;
import prism.akash.tools.NumberKit;
import prism.akash.tools.StringKit;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.Schema;
import prism.akash.tools.annocation.checked.AccessType;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据分析·驾驶舱核心处理类
 * ※主要用于业务基础数据的分析处理
 * <p>
 * TODO : 业务·数据分析「驾驶舱」相关接口
 *
 * @author HaoNan Yan
 */
@Service
@Transactional(readOnly = true)
@Schema(code = "analysis", name = "业务：数据分析·驾驶舱")
public class analysisSchema extends BaseSchema {

    /**
     * 数据·日志分析
     * 「本方法仅超级管理员可以访问」
     *
     * @param executeData
     * @return
     */
    @Access({AccessType.ADMIN})
    public BaseData getLoggerAnalysis(BaseData executeData) {
        //1.获取并解析关键参数信息
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        BaseData re = new BaseData();
        String result = redisTool.get("system:log:ana:key"+data.getInter("type"));
        if (result.isEmpty()){
            String todayDate = dateParse.formatDate("yyyy-MM-dd", new Date());
            String today = dateParse.formatDate("yyyy-MM-dd HH:mm:ss", new Date());

            String lastDay = dateParse.addTime("yyyy-MM-dd", todayDate, -1, 1);
            String weekDay = dateParse.addTime("yyyy-MM-dd", todayDate, -7, 1);

            float today_number = loggerAnalysis(todayDate, today, data.getString("reson"), data.getString("sourceTag"));
            float last_number = loggerAnalysis(lastDay, todayDate + " 00:00:00", data.getString("reson"), data.getString("sourceTag"));
            float week_number = loggerAnalysis(weekDay, todayDate + " 00:00:00", data.getString("reson"), data.getString("sourceTag"));
            float all_number = loggerAnalysis("", "", data.getString("reson"), data.getString("sourceTag"));

            re.put("today", today_number);
            re.put("lastDay", last_number);
            re.put("weekDay", week_number);
            re.put("all", all_number);

            //同比数值计算
            NumberKit numberKit = new NumberKit(2,false,true);
            float today_last = today_number > 0 ? ((today_number - last_number) / today_number)  : 0;
            float today_week = today_number > 0 ? ((today_number -(week_number / 7)) / today_number) : 0;

            re.put("today_last", numberKit.formatPercentage(today_last * 100));
            re.put("today_last_tag",today_last > 0 ? "up" : today_last == 0 ? "" : "down");
            re.put("today_week", numberKit.formatPercentage(today_week * 100));
            re.put("today_week_tag",today_week > 0 ? "up" : today_week == 0 ? "" : "down");
            redisTool.set("system:log:ana:key"+data.getInter("type"), JSON.toJSONString(re),10*60*1000);
        } else {
            re = StringKit.parseBaseData(result);
        }
        return re;
    }

    /**
     * 内部方法：指定查询的时间范围
     *
     * @param sDate 开始时间
     * @param eDate 结束时间
     * @return
     */
    private sqlEngine loggerEngine(String sDate, String eDate) {
        sqlEngine sqlEngine = new sqlEngine()
                .execute("cr_logger", "c");
        if (!sDate.isEmpty()) {
            sqlEngine.queryBuild(queryType.and, "c", "@updateTime", conditionType.GTEQ, groupType.DEF, sDate + " 00:00:00")
                    .queryBuild(queryType.and, "c", "@updateTime", conditionType.LTEQ, groupType.DEF, eDate);
        }
        return sqlEngine;
    }

    /**
     * 内部方法:追加特殊的查询参数
     *
     * @param sDate     开始时间
     * @param eDate     结束时间
     * @param reson     请求类型
     * @param sourceTag 执行标识
     * @return
     */
    private int loggerAnalysis(String sDate, String eDate, String reson, String sourceTag) {
        sqlEngine visitToday = loggerEngine(sDate, eDate);
        if (!reson.isEmpty()) {
            visitToday.queryBuild(queryType.and, "c", "@reson", conditionType.EQ, groupType.DEF, reson);
        }
        if (!sourceTag.isEmpty()) {
            visitToday.queryBuild(queryType.and, "c", "@sourceTag", conditionType.NEQ, groupType.DEF, sourceTag);
        }
        visitToday.appointColumn("c", groupType.DEF, "reson").selectFin("");
        return baseApi.selectBase(visitToday).size();
    }

    /**
     * 数据·日志分析·折线图
     * 「本方法仅超级管理员可以访问」
     *
     * @param executeData
     * @return
     */
    @Access({AccessType.ADMIN})
    public BaseData chart_resonLog(BaseData executeData) {
        BaseData re = new BaseData();
        String result = redisTool.get("system:log:ana:resonLog");
        if (result.isEmpty()) {
            sqlEngine sqlEngine = new sqlEngine()
                    .setSelect("SELECT date( updateTime ) as dates, reson, count( id ) as ct FROM cr_logger where date_sub(curdate(), interval 7 day) <= updateTime  GROUP BY reson, date( updateTime ) ORDER BY date( updateTime ) ASC ");

            List<BaseData> list = baseApi.selectBase(sqlEngine);
            chartEngine chartEngine = new chartEngine(chartType.line)
                    .setColumn("dates#日期", "reson0#登陆", "reson3#数据导出", "reson2#文件上传", "reson9#文件下载")
                    .data_setData(list)
                    .data_parseType(parseType.chiefDeputy)
                    .data_outKey("ct")
                    .data_patchKey("reson")
                    .data_setDeputy("0", "reson0")
                    .data_setDeputy("9", "reson9")
                    .data_setDeputy("2", "reson2")
                    .data_setDeputy("3", "reson3")
                    .histogram_showLine("reson0")
                    .extend_label(true,null,null);
            re = chartEngine.parseChart(true);
            redisTool.set("system:log:ana:resonLog", JSON.toJSONString(re), 10 * 60 * 1000);
        } else {
            re = StringKit.parseBaseData(result);
        }
        return re;
    }

    /**
     * 数据·24小时系统错误日志分析·饼图
     * 「本方法仅超级管理员可以访问」
     *
     * @param executeData
     * @return
     */
    @Access({AccessType.ADMIN})
    public BaseData chart_errorLogger(BaseData executeData) {
        BaseData re = new BaseData();
        String result = redisTool.get("system:log:ana:errorChart");
        if (result.isEmpty()) {

            sqlEngine sqlEngine = new sqlEngine()
                    .setSelect("select sourceTag,COUNT(1) as value from cr_logger where updateTime >= DATE_ADD(CURDATE(),INTERVAL -1 day) GROUP BY sourceTag");

            List<BaseData> list = baseApi.selectBase(sqlEngine);

            chartEngine chartEngine = new chartEngine(chartType.pie)
                    .setColumn("sourceTag#错误类型","value#数量")
                    .data_setData(list)
                    .data_parseType(parseType.mistakeLine)
                    .data_outKey("value")
                    .data_patchKey("sourceTag")
                    .mistakeLine_valueAlias("0", "程序异常")
                    .mistakeLine_valueAlias("1", "执行成功")
                    .mistakeLine_valueAlias("2", "非法请求")
                    .mistakeLine_valueAlias("3", "越权访问")
                    .pie_only_level("执行成功")
                    .pie_only_level("程序异常","非法请求","越权访问");
//                    .legend(true, 20, null, 20, 10, false);
            re = chartEngine.parseChart(false);
            redisTool.set("system:log:ana:errorChart", JSON.toJSONString(re), 10 * 60 * 1000);
        } else {
            re = StringKit.parseBaseData(result);
        }
        return re;
    }
}
