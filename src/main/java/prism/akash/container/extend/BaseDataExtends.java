package prism.akash.container.extend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import prism.akash.api.BaseApi;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.engineEnum.*;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.tools.StringKit;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 基础数据拓展棱镜
 */
public class BaseDataExtends implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(BaseDataExtends.class);

    @Autowired
    BaseApi baseApi;

    /**
     * 新增拓展构造器
     *
     * @param id
     * @param data
     * @return
     */
    public sqlEngine invokeInsertData(String id, String data) {
        sqlEngine sqlEngine = null;
        //TODO : 确认待执行表存在
        if (id != null) {
            if (!id.equals("")) {
                //TODO : 数据载入缓存
                // 2019/11/18 -> 数据表变更,逻辑变更,仅查询启用状态下的数据
                List<BaseData> colArray = baseApi.selectBase(
                        new sqlEngine()
                                .execute("cr_field", "c")
                                .appointColumn("c", groupType.DEF, "code#c_code")
                                .appointColumn("t", groupType.DEF, "code#t_code")
                                .joinBuild("cr_tables", "t", joinType.R)
                                .joinColunm("c", "tid", "id").joinFin()
                                .queryBuild(queryType.and, "c", "@tid", conditionType.EQ, null, id)
                                .queryBuild(queryType.and, "c", "@state", conditionType.EQ, null, "0")
                                .selectFin(""));
                if (colArray.size() > 0) {
                    sqlEngine = new sqlEngine();
                    sqlEngine.execute(colArray.get(0).get("t_code") + "", "");
                    LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(data);
                    //TODO : 确认传入字段存在
                    for (String key : params.keySet()) {
                        for (BaseData col : colArray) {
                            if (col.get("c_code").equals(key)) {
                                String value = params.get(key).toString();
                                sqlEngine.addData("@" + key, value);
                            }
                        }
                    }
                    sqlEngine.insertFin("");
                }
            }
        }
        return sqlEngine;
    }

    /**
     * 动态SQL构造器
     *
     * @param sqlEngine SQL引擎对象
     * @param engineId  需要动态获取的数据引擎编号
     * @param data      入参参数
     * @return
     */
    public sqlEngine invokeDataInteraction(sqlEngine sqlEngine, String engineId, String data, boolean isChild) {
        long startTime = System.currentTimeMillis();
        //对engineId进行格式化
        engineId = engineId == null ? "" : engineId;
        if (!engineId.trim().equals("")) {
            BaseData sel = new BaseData();
            sel.put("eid", engineId);
            //TODO: 判断当前是否为嵌套子查询
            List<BaseData> engineFlow = baseApi.selectBase(isChild ?
                    new sqlEngine()
                            .execute("cr_engineexecute", "e")
                            .queryBuild(queryType.and, "e", "eid", conditionType.EQ, null,"eid")
                            .queryBuild(queryType.and, "e", "@state", conditionType.EQ, null, "0")
                            .dataSort("e", "sorts", sortType.ASC)
                            .selectFin(JSON.toJSONString(sel))
                    :
                    new sqlEngine()
                            .execute("cr_engineexecute", "e")
                            .joinBuild("cr_engine", "en", joinType.L).joinColunm("e", "eid", "id").joinFin()
                            .queryBuild(queryType.and, "e", "eid", conditionType.EQ, null,"eid")
                            .queryBuild(queryType.and, "en", "@state", conditionType.EQ, null, "0")
                            .queryBuild(queryType.and, "e", "@state", conditionType.EQ, null, "0")
                            .dataSort("e", "sorts", sortType.ASC)
                            .selectFin(JSON.toJSONString(sel)));
            if (engineFlow.size() > 0) {
                for (BaseData bd : engineFlow) {
                    try {
                        //TODO: 操作标识非空判定
                        if (bd.get("executeTag") != null) {
                            String executeTag = bd.getString("executeTag");
                            //TODO: 操作标识句柄结束返回判定
                            if (executeTag.equals("selectFin")) {
                                sqlEngine.selectFin(data);
                            } else if (executeTag.equals("joinFin")) {
                                sqlEngine.joinFin();
                            } else if (executeTag.equals("insertFin")) {
                                sqlEngine.insertFin(data);
                            } else if(executeTag.equals("updateFin")){
                                sqlEngine.updateFin(data);
                            } else if(executeTag.equals("selectIntactFin")){
                                //TODO : 若需要返回数据与总条数时，请使用selectIntactFin方法，另selectIntactFin与selectFin不能同时使用！
                                sqlEngine.selectFin(data).selectTotal();
                            } else {
                                JSONObject jo = JSONObject.parseObject(bd.get("executeData") + "");
                                switch (executeTag) {
                                    case "execute":
                                        sqlEngine.execute(jo.get("tableName") + "", jo.get("alias") + "");
                                        break;
                                    case "executeChild":
                                        sqlEngine.executeChild(this.invokeDataInteraction(new sqlEngine(), bd.getString("id"), data, true)
                                                , jo.get("alias") + "");
                                        break;
                                    case "joinWhere":
                                        sqlEngine.joinWhere(queryType.getQueryType(jo.getString("queryType"))
                                                , jo.get("table") + "",jo.get("key") + "",
                                                conditionType.getconditionType(jo.getString("conditionType")),
                                                jo.get("value") + "");
                                        break;
                                    case "joinWhereChild":
                                        sqlEngine.joinWhereChild(queryType.getQueryType(jo.getString("queryType"))
                                                , jo.get("table") + "", jo.get("key") + ""
                                                , conditionType.getconditionType(jo.getString("conditionType"))
                                                , this.invokeDataInteraction(new sqlEngine(), bd.getString("id"), data, true));
                                        break;
                                    case "joinBuild":
                                        sqlEngine.joinBuild(jo.get("joinTable") + "", jo.get("joinAlias") + "", joinType.getJoinType(jo.getString("joinType")));
                                        break;
                                    case "joinChildBuild":
                                        sqlEngine.joinChildBuild(this.invokeDataInteraction(new sqlEngine(),
                                                JSON.parseObject(bd.getString("executeData")).get("id")+"", data, true), jo.get("joinAlias") + "",
                                                joinType.getJoinType(jo.getString("joinType")));
                                        break;
                                    case "joinColunm":
                                        sqlEngine.joinColunm(jo.get("joinTable") + "", jo.get("joinFrom") + "", jo.get("joinTo") + "");
                                        break;
                                    case "dataPaging":
                                        sqlEngine.dataPaging(jo.get("pageNo") + ""
                                                , jo.get("pageSize") + "");
                                        break;
                                    case "dataSort":
                                        sqlEngine.dataSort(jo.get("table") + "", jo.get("key") + "", sortType.getSortType(jo.getString("sortType")));
                                        break;
                                    case "caseBuild":
                                        sqlEngine.caseBuild(jo.get("caseAlias") + "");
                                        break;
                                    case "caseWhenQuery":
                                        sqlEngine.caseWhenQuery(queryType.getQueryType(jo.getString("whenQuery"))
                                                , jo.get("whenTable") + "", jo.get("whenColumn") + ""
                                                , conditionType.getconditionType(jo.getString("whenCondition"))
                                                , groupType.getgroupType(jo.getString("exCaseType"))
                                                , jo.get("whenValue") + "");
                                        break;
                                    case "caseWhenQueryChild":
                                        sqlEngine.caseWhenQueryChild(queryType.getQueryType(jo.getString("whenQuery"))
                                                , jo.get("whenTable") + "", jo.get("whenColumn") + ""
                                                , conditionType.getconditionType(jo.getString("whenCondition"))
                                                , groupType.getgroupType(jo.getString("exCaseType"))
                                                , this.invokeDataInteraction(new sqlEngine(), bd.getString("id"), data, true));
                                        break;
                                    case "caseThen":
                                        sqlEngine.caseThen(jo.get("thenValue") + "");
                                        break;
                                    case "caseFin":
                                        sqlEngine.caseFin(jo.get("elseValue") + "");
                                        break;
                                    case "appointColumn":
                                        sqlEngine.appointColumn(jo.get("appointTable") + ""
                                                , groupType.getgroupType(jo.getString("exAppointType")),
                                                jo.get("appointColumns") + "");
                                        break;
                                    case "groupBuild":
                                        sqlEngine.groupBuild(jo.get("groupTable") + "", jo.get("groupColumns") + "");
                                        break;
                                    case "groupColumn":
                                        sqlEngine.groupColumn(groupType.getgroupType(jo.getString("groupType"))
                                                , jo.get("groupTable") + ""
                                                , jo.get("groupColumns") + "");
                                        break;
                                    case "groupHaving":
                                        sqlEngine.groupHaving(groupType.getgroupType(jo.getString("groupType"))
                                                , queryType.getQueryType(jo.getString("queryType"))
                                                , jo.get("groupTable") + "", jo.get("groupColumn") + ""
                                                , conditionType.getconditionType(jo.getString("conditionType"))
                                                , jo.get("value") + "");
                                        break;
                                    case "groupHavingChild":
                                        sqlEngine.groupHavingChild(groupType.getgroupType(jo.getString("groupType"))
                                                , queryType.getQueryType(jo.getString("queryType"))
                                                , jo.get("groupTable") + "", jo.get("groupColumn") + ""
                                                , conditionType.getconditionType(jo.getString("conditionType"))
                                                , this.invokeDataInteraction(new sqlEngine(), bd.getString("id"), data, true));
                                        break;
                                    case "queryBuild":
                                        sqlEngine.queryBuild(queryType.getQueryType(jo.getString("queryType"))
                                                , jo.get("table") + "", jo.get("key") + ""
                                                , conditionType.getconditionType(jo.getString("conditionType"))
                                                , groupType.getgroupType(jo.getString("exQueryType"))
                                                , jo.get("value") + "");
                                        break;
                                    case "queryChild":
                                        sqlEngine.queryChild(queryType.getQueryType(jo.getString("queryType"))
                                                , jo.get("table") + "", jo.get("key") + ""
                                                , conditionType.getconditionType(jo.getString("conditionType"))
                                                , this.invokeDataInteraction(new sqlEngine(), bd.getString("id"), data, true));
                                        break;
                                    case "addData":
                                        sqlEngine.addData(jo.get("addkey") + "",
                                                jo.get("addValue") + "");
                                        break;
                                    case "insertCopy":
                                        sqlEngine.insertCopy(this.invokeDataInteraction(new sqlEngine(),bd.getString("id"),data,true));
                                        break;
                                    case "insertFetchPush":
                                        sqlEngine.insertFetchPush(data,jo.get("keys") + "");
                                        break;
                                    case "updateData":
                                        sqlEngine.updateData(jo.get("updKey") + "",
                                                jo.get("updValue") + "");
                                        break;
                                    case "deleteFin":
                                        sqlEngine.deleteFin(data);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        logger.error("executeData:数据对象转储出现错误 -> (" + e.getMessage() + ")");
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("sql生成耗时:" + (endTime - startTime));
        return sqlEngine;
    }
}
