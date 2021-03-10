package prism.akash.container.sqlEngine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.engineEnum.*;
import prism.akash.tools.StringKit;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class sqlEngine implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(sqlEngine.class);

    BaseData engine = null;
    boolean isGroup = false;
    boolean isExecute = false;
    //执行-提前判别数据有效性
    String dataList = "";

    public sqlEngine() {
        engine = new BaseData();
    }

    //执行时调用
    public sqlEngine(String data){
        engine = new BaseData();
        isExecute = true;
        dataList = data == null ? "[]" : data;
    }

    /**
     * 注入异常提示
     *
     * @return
     */
    private sqlEngine error() {
        logger.error("⚠ 检测到异常注入攻击，引擎已进行拦截。");
        engine.put("error", "Error in data format");
        return this;
    }

    /**
     * 通用赋值方法
     *
     * @param data
     * @param parseSql
     * @return
     */
    private String assignment(String data, String parseSql) {
        if (!data.equals("")) {
            //TODO: 数据参数赋值操作
            LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(data);

            String executeParam = engine.get("executeParam") + "";
            for (String key : params.keySet()) {
                String queryValue = params.get(key) + "";
                StringBuffer query = new StringBuffer();
                //赋值处理
                if(key.indexOf("@in") > -1){
                    //针对IN类型Tag做处理
                    query.append(" (");
                    for (String iv : queryValue.split(",")) {
                        if (!iv.trim().equals("") || iv != null)
                            query.append("'").append(iv).append("',");
                    }
                    query.deleteCharAt(query.length() - 1).append(")");
                    parseSql = parseSql.replaceAll("params_" + key,  query.toString());
                }
                else if(key.indexOf("@bt") > -1){
                    //针对BETWEEN类型Tag做处理
                    query.append("'").append(queryValue.split(",")[0]).append("'");
                    query.append(" and ");
                    query.append("'").append(queryValue.split(",")[1]).append("'");
                }
                else{
                    //非特殊类型标识处理
                    query.append(queryValue);
                }
                //TODO : 赋值替换
                parseSql = parseSql.replaceAll("params_" + key, query.toString());
                //TODO : 将已传参的参数消除
                executeParam = executeParam.replaceAll(key, "");
            }

            //TODO : 将未传参的参数统一变更为NULL
            for (String notParam : executeParam.split(",")) {
                if (!notParam.equals("")) {
                    // TODO : trim清空空格
                    parseSql = parseSql.replaceAll("'params_" + notParam.trim() + "'", "NULL");
                }
            }
        }
        return parseSql;
    }

    //TODO : 查询相关    ↓↓↓↓↓↓↓↓↓

    private sqlEngine queryType(queryType queryType) {
        engine.put("queryType", queryType.getQueryType());
        return this;
    }

    private sqlEngine queryConditionType(conditionType conditionType) {
        engine.put("conditionType", conditionType.getconditionType());
        return this;
    }

    private sqlEngine queryKey(String key) {
        if (key.indexOf(".") > -1) {
            engine.put("queryKey", checkJson(key.toString()));
        } else {
            engine.put("queryKey", StringEscapeUtils.escapeSql(key.toString()));
        }
        return this;
    }

    private sqlEngine queryTable(String table) {
        engine.put("queryTable", StringEscapeUtils.escapeSql(table.toString()));
        return this;
    }

    private sqlEngine queryValue(String value) {
        String key = engine.getString("queryKey");
        //TODO : 获取锁定标识，当查询Key值带有@，value将直接作为参数使用，反之则作为常量占位符
        if (key.contains("@")) {
            /**
             * 判断标识中是否含有#标记，若有则表示为字段匹配
             */
            if (key.contains("#")) {
                engine.put("exType", "-");
                key = key.replaceAll("#", "");
            }
            engine.put("queryValue", value);
            engine.put("queryKey", key.replaceAll("@", ""));
        } else {
            String params = StringEscapeUtils.escapeSql(value);
            engine.put("queryValue", "params_" + params.split("#")[0]);
            //动态参数另存为
            engine.put("executeParam", engine.get("executeParam") == null ? params : (engine.get("executeParam") + ", " + params));
        }

        return this;
    }

    /**
     * case构造器
     * @param caseAlias  a#b  code#注释
     * @return
     */
    public sqlEngine caseBuild(String caseAlias) {
        String alias = StringEscapeUtils.escapeSql(caseAlias);
        engine.put("caseAlias", alias.split("#")[0]);
        //2020-06-01新增：回显数据
        if (engine.get("outFiled") == null) {
            engine.put("outFiled", alias);
        } else {
            engine.put("outFiled", engine.get("outFiled").toString() + "," + alias);
        }
        return this;
    }

    public sqlEngine caseWhenQuery(queryType whenQuery, String whenTable, String whenColumn, conditionType whenCondition, groupType exCaseType, String whenValue) {
        //TODO: 调用queryBuild获取筛选条件
        this.queryBuild(whenQuery, whenTable, whenColumn, whenCondition, exCaseType, whenValue);
        engine.put("caseWhenQuery", engine.get("caseWhenQuery") == null ? engine.get("query") : engine.get("caseWhenQuery") + whenQuery.getQueryType() + engine.get("query"));
        //将生产好的查询语句转存后清空
        engine.remove("query");
        return this;
    }

    /**
     * 新增：caseWhen支持使用引擎关联
     * @param whenQuery
     * @param whenTable
     * @param whenColumn
     * @param whenCondition
     * @param exCaseType
     * @param child
     * @return
     */
    public sqlEngine caseWhenQueryChild(queryType whenQuery, String whenTable, String whenColumn, conditionType whenCondition, groupType exCaseType, sqlEngine child) {
        //TODO: 调用queryBuild获取筛选条件
        this.isGroup = false;
        engine.put("child", "child");
        this.queryType(whenQuery).queryTable(whenTable).queryKey(whenColumn).queryConditionType(whenCondition).queryValue("(" + child.engine.get("select") + ")").queryFin(true);

        engine.put("caseWhenQuery", engine.get("caseWhenQuery") == null ? engine.get("query") : engine.get("caseWhenQuery") + whenQuery.getQueryType() + engine.get("query"));
        //将生产好的查询语句转存后清空
        engine.remove("query");
        return this;
    }

    public sqlEngine caseThen(String thenValue) {
        StringBuffer caseThen = new StringBuffer(" WHEN ");
        caseThen.append(engine.get("caseWhenQuery")).append(" THEN '").append(thenValue).append("'");
        engine.put("caseQuery", engine.get("caseQuery") == null ? caseThen : (engine.get("caseQuery").toString() + caseThen));
        //TODO：清空
        engine.remove("caseWhenQuery");
        return this;
    }

    public sqlEngine caseFin(String elseValue) {
        StringBuffer caseFin = new StringBuffer(" case ");
        caseFin.append(engine.get("caseQuery"))
                .append(" else '").append(elseValue)
                .append("' end as ").append(engine.get("caseAlias"));
//                .append(engine.get("caseTable")).append(".").append(engine.get("caseColumn"))
//                .append(" ")
        engine.put("caseFin", caseFin);
        //TODO：清空
        engine.remove("caseQuery");
        engine.remove("caseTable");
        engine.remove("caseColumn");
        engine.remove("caseAlias");
        return this;
    }

    /**
     * 查询语句生成
     *
     * @param selType true  -> where 使用
     *                false -> join where 使用
     * @return
     */
    private sqlEngine queryFin(boolean selType) {
        //连表或非连表查询（join where / where）
        String queryName = selType ? this.isGroup ? "groupQuery" : "query" : "joinQuery";

        StringBuffer query = new StringBuffer();


        //TODO : 若queryType未填写,则默认使用and作为连接条件
        String queryType = engine.get("queryType") == null ? "and" : engine.getString("queryType");
        //TODO : 如果当前引擎查询语句未生成,则忽略queryType指向
        if (engine.get(queryName) == null) {
            query.append(" ").append(queryType.contains("Merge") ? " (" : "");
        } else {
            String end = queryType.contains("End") ? " " : " (";
            query.append(" ").append(queryType.contains("Merge") ? queryType.contains("and") ? " and" + end : " or" + end : queryType).append(" ");
        }

        //TODO：2020/06/02 解决入参NULL值出现错误的问题。
        //鉴别是生成还是执行
        if (isExecute){
            if(engine.getString("queryValue").indexOf("params_") > -1) {
                LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(dataList);
                if (params.size() > 0) {
                    for (String key : params.keySet()) {
                        //判断key是否为null
                        if (("params_" + key).equals(engine.get("queryValue"))) {
                            if(params.get(key) != null){
                                queryBind(query);
                            }else{
                                query.append(" 1 = 1 ");
                            }
                        }
                    }
                } else {
                    queryBind(query);
                }
            }else{
                queryBind(query);
            }
        }else{
            queryBind(query);
        }

        //TODO : 判断本次组合查询是否已结束
        if (queryType.contains("End")) {
            query.append(" ) ");
        }

        engine.remove("queryType");
        engine.remove("conditionType");
        engine.remove("queryKey");
        engine.remove("queryValue");
        engine.remove("exType");

        //TODO : 将生成的查询语句保存在引擎对象
        engine.put(queryName, (engine.get(queryName) == null ? " " : engine.getString(queryName)) + " " + query);
        return this;
    }

    /**
     * queryFin方法提出
     * @param query
     * @return
     */
    private StringBuffer queryBind(StringBuffer query){
        //TODO : 根据当前筛选条件对数据进行格式重组
        String conditionType = engine.get("conditionType") == null ? "" : engine.getString("conditionType");
        String queryValue = engine.get("queryValue") == null ? "" : engine.getString("queryValue");

        StringBuffer nKey = new StringBuffer();
        if (engine.get("queryTable") != null) {
            nKey.append(engine.get("queryTable")).append(".");
        }
        nKey.append(engine.get("queryKey"));

        // 判断当前是否需要使用权限查询函数方法
        if (conditionType.contains("QUERY_ROLE")){
            if (!queryValue.isEmpty()){
                //标记标识
                engine.put("QueryRoleSql"," ( select getChildLst('" + queryValue + "') akash_query_role ) query_role_data,");
                query.append(" find_in_set(").append(nKey).append(",akash_query_role)");
            }
        } else {
            query.append(nKey);
            boolean isEscape = conditionType.contains("ESCAPE");
            boolean executeValue = false;
            if (conditionType.equals("")) {
                query.append(" = ");
            } else {
                //需要进行通配符转义
                if (isEscape) {
                    query.append(conditionType.replaceAll("ESCAPE", ""));
                }
                query.append(conditionType);
                executeValue = conditionType.contains("LIKE") || conditionType.contains("NULL") ;
            }
            if (executeValue) {
                if (conditionType.equals(" LIKE ")) {
                    query.append(" '").append(isEscape ? queryValue.split("\\|")[1] : queryValue)
                            .append("' ").append(isEscape ? ("escape '" + queryValue.split("\\|")[0] + "'") : "");
                } else if (conditionType.equals(" NOT LIKE ")) {
                    query.append(" '").append(isEscape ? queryValue.split("\\|")[1] : queryValue)
                            .append("' ").append(isEscape ? ("escape '" + queryValue.split("\\|")[0] + "'") : "");
                } else if (conditionType.equals(" LIKE BINARY ")) {
                    query.append(" concat ('%',upper('").append(isEscape ? queryValue.split("\\|")[1] : queryValue).append("','%')")
                            .append("' ").append(isEscape ? ("escape '" + queryValue.split("\\|")[0] + "'") : "");
                }

            } else {
                //TODO: 当前是否嵌套了子查询
                if (engine.get("child") == null) {
                    //TODO : 二次判断当前查询是否使用了特殊标记
                    if(engine.get("exType") == null){
                        query.append(" '").append(engine.get("queryValue")).append("' ");
                    }else{
                        query.append(engine.get("queryValue"));
                    }
                } else {
                    query.append(engine.get("queryValue"));
                }
            }
        }
        return query;
    }

    /**
     * 指定子表
     *
     * @param joinTable 子表的表名
     * @return
     */
    private sqlEngine join(String joinTable, String joinAlias) {
        engine.put("joinTable", joinTable);
        engine.put("joinTableAlias", joinAlias);
        return this;
    }

    /**
     * 指定主子表关系
     *
     * @param joinType 关系类型枚举
     * @return
     */
    private sqlEngine joinType(joinType joinType) {
        engine.put("joinType", joinType.getJoinType());
        return this;
    }

    /**
     * 分组条件构建
     *
     * @param groupTable
     * @param groupColumns
     * @return
     */
    private sqlEngine groupBy(String groupTable, String groupColumns) {
        StringBuffer groupBy = new StringBuffer(",");

        if (groupColumns.length() > 0) {
            for (String col : groupColumns.split(",")) {
                groupBy.append(groupTable).append(".").append(col).append(",");
            }
        }

        groupBy = groupBy.deleteCharAt(0).deleteCharAt(groupBy.length() - 1);
        if (engine.get("groupBy") == null) {
            engine.put("groupBy", groupBy);
        } else {
            engine.put("groupBy", engine.get("groupBy") + "" + groupBy);
        }

        engine.put("groupBy", groupBy);
        return this;
    }


    /**
     * 生成Having需要的Key键
     *
     * @param groupType
     * @param queryType
     * @param groupTable
     * @param groupColumn
     * @return
     */
    private String getHavingKey(groupType groupType, queryType queryType, String groupTable, String groupColumn) {
        StringBuffer key = new StringBuffer(groupTable).append(".").append(groupColumn);
        if (!groupType.getgroupType().equals("DEF")) {
            key = key.insert(0, groupType.getgroupType() + "(").append(" )");
        }
        return key.toString();
    }

    /**
     * 指定需要查询的数据字段，以表为单位进行设定,若columns为空则视为查询当前表全部
     *
     * @param appointTable
     * @param exAppointType  标记特殊查询方式
     * @param appointColumns 字段以逗号隔开，如需指定别名可按照   原列名#列别名 进行设置
     * @return
     */
    public sqlEngine appointColumn(String appointTable, groupType exAppointType, String appointColumns) {
        StringBuffer appoint = new StringBuffer(",");
        String exType = exAppointType.getgroupType();
        int start = 4;//默认为手机格式开始
        int end = 4;//默认为手机格式结束

        appointTable = StringEscapeUtils.escapeSql(appointTable);
        boolean isExAppoint = exType.equals("DEF");
        //TODO: 判断当前是否需要执行嵌套去重
        boolean isDistinct = exType.indexOf("DIA") > -1;
        if(isDistinct){
            exType = exType.replace("DIA","");
        }
        //TODO: 判断当前是否需要执行隐私字段处理
        boolean isPrivacy = exType.indexOf("PRIVACY") > -1;
        if (isPrivacy) {
            // 1为身份证验证格式
            start = exType.contains("TEL") ? 4 : 6;
            end = exType.contains("TEL") ? 4 : 8;
            exType = exType.replace(start == 4 ? "PRIVACY_TEL" : "PRIVACY_ID", "");
        }
        //TODO: 对传入数据进行处理优化
        if (appointColumns.isEmpty()) {
            appoint.append(appointTable.split(",")[0]).append(".*");
            appoint = appoint.deleteCharAt(0);
        } else {
            for (String col : appointColumns.split(",")) {
                //TODO : 进行数据转义防止注入
                col = StringEscapeUtils.escapeSql(col);
                if (col.contains("@")) {
                    //TODO ：传入固定字段值进行解析返回
                    appoint.append("'").append(checkJson(col.replaceAll("@", ""))).append("'");
                } else {
                    if (col.contains("#")) {
                        if (isPrivacy) {
                            appoint.append(" REPLACE ( ")
                                    .append(appointTable).append(".").append(checkJson(col.split("#")[0]))
                                    .append(", SUBSTR( ")
                                    .append(appointTable).append(".").append(checkJson(col.split("#")[0]))
                                    .append(" FROM "+ start +" FOR "+ end + " ), '****' ) AS `")
                                    .append(col.split("#")[1]).append("` ,");
                        } else {
                            //TODO : 2020/6/5 新增去重字段判定
                            appoint.append(isExAppoint ? "" : exType + (isDistinct ? "(DISTINCT(" : "(")).append(appointTable).append(".").append(checkJson(col.split("#")[0])).append(isExAppoint ? " as `" : isDistinct ? ") ) as `" : ") as `").append(col.split("#")[1]).append("` ,");
                        }
                    } else {
                        if (isPrivacy) {
                            appoint.append(" REPLACE ( ")
                                    .append(appointTable).append(".").append(checkJson(col.split("#")[0]))
                                    .append(", SUBSTR( ")
                                    .append(appointTable).append(".").append(checkJson(col.split("#")[0]))
                                    .append(" FROM "+ start +" FOR "+ end + " ), '****' ) AS `")
                                    .append(col.split("#")[0]).append("` ,");
                        } else {
                            appoint.append(isExAppoint ? "" : exType + (isDistinct ? "(DISTINCT(" : "(")).append(appointTable).append(".").append(checkJson(col)).append(isExAppoint ? "," : isDistinct ? "))," : "),");
                        }
                    }
                }
            }
            appoint = appoint.deleteCharAt(0).deleteCharAt(appoint.length() - 1);
        }

        if (engine.get("appointColumn") == null) {
            engine.put("appointColumn", appoint);

        } else {
            engine.put("appointColumn", engine.get("appointColumn").toString() + "," + appoint);

        }
        //输出字段
        if (engine.get("outFiled") == null) {
            engine.put("outFiled", appointColumns.split(",").length < 1 ? (appointTable + ".*") : appoint);
        }else{
            engine.put("outFiled", engine.get("outFiled").toString() + "," + (appointColumns.split(",").length < 1 ? (appointTable + ".*") : appoint));
        }
        return this;
    }

    private String checkJson(String s) {
        // 有标识符.则视为json检索
        if (s.indexOf(".") > -1) {
            String[] json = s.split("\\.");
            s = json[0] + "->>'$." + json[1] + "'";
        }
        return s;
    }

    /**
     * 分组条件构造器
     *
     * @param groupTable
     * @param groupColumns
     * @return
     */
    public sqlEngine groupBuild(String groupTable, String groupColumns) {
        this.isGroup = true;
        return this.groupBy(groupTable, groupColumns);
    }

    /**
     * 分组查询字段构造器
     *
     * @param groupType
     * @param groupTable
     * @param groupColumns
     * @return
     */
    public sqlEngine groupColumn(groupType groupType, String groupTable, String groupColumns) {
        StringBuffer groupColumn = new StringBuffer(",");

        if (groupColumns.length() > 0) {
            for (String col : groupColumns.split(",")) {
                String alias = "";
                if (col.contains("#")) {
                    alias = col.split("#")[1];
                }
                String key = alias.equals("") ? col : col.split("#")[0];
                //TODO：判断是否需要执行函数处理
                if (groupType.getgroupType().equals("DEF")) {
                    groupColumn.append(groupTable).append(".").append(key);
                } else {
                    groupColumn.append(" ").append(groupType.getgroupType()).append("(").append(groupTable).append(".").append(key).append(")");
                }
                //TODO: 增加别名
                if (!alias.equals("")) {
                    groupColumn.append(" as `").append(alias).append("`");
                }
                groupColumn.append(" ,");
            }
            groupColumn = groupColumn.deleteCharAt(0).deleteCharAt(groupColumn.length() - 1);
        }


        if (engine.get("groupColumn") == null) {
            engine.put("groupColumn", groupColumn);
        } else {
            engine.put("groupColumn", engine.get("groupColumn") + "," + groupColumn);
        }

//        engine.put("groupColumn", engine.get("groupColumn") + ","+ groupColumn);
        return this;
    }


    /**
     * Having条件构造器
     *
     * @param groupType
     * @param queryType
     * @param groupTable
     * @param groupColumn
     * @param conditionType
     * @param value
     * @return
     */
    public sqlEngine groupHaving(groupType groupType, queryType queryType, String groupTable, String groupColumn, conditionType conditionType, String value) {
        this.isGroup = true;
        return this.queryType(queryType).queryKey(getHavingKey(groupType, queryType, groupTable, groupColumn)).queryConditionType(conditionType).queryValue(value).queryFin(true);
    }


    /**
     * 嵌套Having条件构造器
     *
     * @param groupType
     * @param queryType
     * @param groupTable
     * @param groupColumn
     * @param conditionType
     * @param child
     * @return
     */
    public sqlEngine groupHavingChild(groupType groupType, queryType queryType, String groupTable, String groupColumn, conditionType conditionType, sqlEngine child) {
        engine.put("child", "child");
        this.isGroup = true;
        return this.queryType(queryType).queryKey(getHavingKey(groupType, queryType, groupTable, groupColumn)).queryConditionType(conditionType).queryValue("(" + child.engine.get("select") + ")").queryFin(true);
    }

    /**
     * 查询条件构造器
     *
     * @param queryType
     * @param key
     * @param conditionType
     * @param exQueryType
     * @param value  如果exCaseType不为null或DEF，则value入参格式为table||key!
     * @return
     */
    public sqlEngine queryBuild(queryType queryType, String table, String key, conditionType conditionType, groupType exQueryType, String value) {
        this.isGroup = false;
        String exType = exQueryType == null ? "DEF" : exQueryType.getgroupType();
        if (!exType.equals("DEF")) {
            key = "@" + key;

            //追加JSON判定

            String exTable = value.split("\\|\\|")[0];
            String exKey = value.split("\\|\\|")[1];

            if(StringKit.isSpecialChar(exTable) ||  StringKit.isSpecialChar(exKey)){
                value = "Error in data format";
                this.error();
            }else{
                if (exType.equals("FILED")){
                    value = exTable + "." + exKey;
                } else {
                    value = exType + "(" + exTable + "." + exKey + ")";
                }
            }
            //TODO : 特殊标记
            engine.put("exType", "exType");
        }
        return this.queryType(queryType).queryTable(table).queryKey(key).queryConditionType(conditionType).queryValue(value).queryFin(true);
    }

    /**
     * 嵌套子查询条件构造器
     *
     * @param queryType
     * @param table
     * @param key
     * @param conditionType
     * @param child
     * @return
     */
    public sqlEngine queryChild(queryType queryType, String table, String key, conditionType conditionType, sqlEngine child) {
        this.isGroup = false;
        engine.put("child", "child");
        return this.queryType(queryType).queryTable(table).queryKey(key).queryConditionType(conditionType).queryValue("(" + child.engine.get("select") + ")").queryFin(true);
    }

    /**
     * 嵌套子查询条件构造器(连表)
     */
    public sqlEngine joinWhereChild(queryType queryType, String table, String key, conditionType conditionType, sqlEngine child) {
        this.isGroup = false;
        engine.put("child", "child");
        return this.queryType(queryType).queryTable(table).queryKey(key).queryConditionType(conditionType).queryValue("(" + child.engine.get("select") + ")").queryFin(false);
    }
    /**
     * 指定主表
     *
     * @param tableName 主表的表名
     * @return
     */
    public sqlEngine execute(String tableName, String alias) {
        alias = alias == null || alias.trim().equals("") ? tableName : alias;
        engine.put("tableName", tableName);
        engine.put("alias", alias == null ? tableName : alias);
        engine.put("execute", tableName + " as " + alias);
        return this;
    }

    /**
     * 指定子查询作为主表
     *
     * @param table
     * @param alias
     * @return
     */
    public sqlEngine executeChild(sqlEngine table, String alias) {
        engine.put("execute", "(" + table.engine.get("select") + ") as " + alias);
        // TODO : 子查询内循环需求入参对象值
        engine.put("executeParam", table.engine.get("executeParam"));
        return this;
    }

    /**
     * 主子表筛选条件（非必要,请根据实际业务进行使用）
     *
     * @param queryType
     * @param table
     * @param key
     * @param conditionType
     * @param value
     * @return
     */
    public sqlEngine joinWhere(queryType queryType, String table, String key, conditionType conditionType, String value) {
        return this.queryType(queryType).queryTable(table).queryKey(key).queryConditionType(conditionType).queryValue(value).queryFin(false);
    }


    /**
     * 主子表关系构造器
     *
     * @param joinTable
     * @param joinType
     * @return
     */
    public sqlEngine joinBuild(String joinTable, String joinAlias, joinType joinType) {
        return this.join(joinTable, joinAlias).joinType(joinType);
    }

    /**
     * 主子表关系构造器 （复杂连表处理）
     *
     * @param joinTable
     * @param joinAlias
     * @param joinType
     * @return
     */
    public sqlEngine joinChildBuild(sqlEngine joinTable, String joinAlias, joinType joinType) {
        return this.join("(" + joinTable.engine.get("select") + ")", joinAlias).joinType(joinType);
    }

    /**
     * 主子表关系字段构造器「默认指定主表」
     *
     * @param joinTable 所连接的主表表名
     * @param joinFrom  主表外键字段
     * @param joinTo    字表连接字段
     * @return
     */
    public sqlEngine joinColunm(String joinTable, String joinFrom, String joinTo) {
        engine.put("joinColumn", joinTable + "." + joinFrom + " = " + engine.get("joinTableAlias") + "." + joinTo);
        return this;
    }

    /**
     * 主子表关系字段构造器「手动指定外联表」
     *
     * @param joinTable 所连接的主表表名
     * @param joinFrom  主表外键字段
     * @param joinTo    字表连接字段
     * @return
     */
    public sqlEngine joinColunm(String joinTable, String joinFrom,String joinTableAlias, String joinTo) {
        engine.put("joinColumn", joinTable + "." + joinFrom + " = " + joinTableAlias + "." + joinTo);
        return this;
    }

    /**
     * 完成一次主子表关联关系认证并生成对应语句
     *
     * @return
     */
    public sqlEngine joinFin() {
        StringBuffer jo = new StringBuffer();
        jo.append(engine.get("join") == null ? "" : engine.get("join"))
                .append(engine.get("joinType") == null ? joinType.L.getJoinType() : engine.get("joinType"))
                .append(engine.get("joinTable"))
                .append(engine.get("joinType") != " , " ? " " : " as ")
                .append(engine.get("joinTableAlias"))
                .append(" ");
        //TODO: 对主子表关联条件进行匹配
        if (engine.get("joinQuery") == null) {
            //判断是否为普通连接
            if(engine.get("joinType") != " , "){
                jo.append("on")
                        .append(" ")
                        .append(engine.get("joinColumn"))
                        .append(" ");
            }
        } else {
            jo.append(" where ").append(engine.get("joinQuery"));
        }
        engine.put("join", jo.toString());
        //TODO: 重置主子表筛选条件属性
        engine.remove("joinQuery");
        engine.remove("joinType");
        return this;
    }


    /**
     * 数据分页构造器
     *
     * @param pageNo   当前第几页动态参数
     * @param pageSize 每页查询/展示X条动态参数
     * @return
     */
    public sqlEngine dataPaging(String pageNo, String pageSize) {
        StringBuffer sb = new StringBuffer();
        sb.append(" limit ");
        String no = pageNo.split("#")[0];
        String size = pageSize.split("#")[0];
        //分离数据，区分是否为动态参数
        if(!no.contains("@")){
            sb.append("params_").append(StringEscapeUtils.escapeSql(no.replaceAll("\\@","")));
        }else{
            sb.append(StringEscapeUtils.escapeSql(no.replaceAll("\\@","")));
        }
        sb.append(",");
        if(!size.contains("@")){
            sb.append("params_").append(StringEscapeUtils.escapeSql(size.replaceAll("\\@","")));
        }else{
            sb.append(StringEscapeUtils.escapeSql(size.replaceAll("\\@","")));
        }
        engine.put("dataPaging", sb.toString());
        //TODO : 动态参数另存为
        //首要条件：关键参数内不含固定参数标记@
        if(!size.contains("@") && !no.contains("@")) {
            String params = pageNo + "," + pageSize;
            engine.put("executeParam", engine.get("executeParam") == null ? params : (engine.get("executeParam") + ", " + params));
        }
        return this;
    }

    /**
     * 数据排序构造器
     *
     * @param key
     * @param sortType
     * @return
     */
    public sqlEngine dataSort(String table,String key, sortType sortType) {
        StringBuffer sort = new StringBuffer(" order by ");
        String sortTypeStr = sortType.getSortType();

        key = table + "." + key;
        if(!key.equals(".")){
            switch (sortTypeStr) {
                case "ASC":
                    sort.append(" " + key + " asc,");
                    break;
                case "UASC":
                    sort.append("  convert( " + key + " using gbk)  asc,");
                    break;
                case "UDESC":
                    sort.append("  convert( " + key + " using gbk)  desc,");
                    break;
                default:
                    sort.append(" " + key + " desc,");
                    break;
            }
        }else{
            //不需要分组
            sort.append(" NULL,");
        }
        engine.put("dataSort", engine.get("dataSort") == null ? sort : (engine.getString("dataSort") + sort));
        return this;
    }


    /**
     *  声明当前查询语句已经结束并执行生产
     * @param data
     * @return
     */
    public sqlEngine selectFin(String data) {
        StringBuffer sel = new StringBuffer();
        String dataSort = engine.get("dataSort") == null ? " " : engine.get("dataSort").toString();

        boolean caseFin = engine.get("caseFin") == null;
        boolean appointFin = engine.get("appointColumn") == null;
        boolean groupFin = engine.get("groupColumn") == null;

        sel.append(" select ").append(caseFin ? " " : engine.get("caseFin"));

        //当三个查询均为null时，默认为查询全部
        if (caseFin && appointFin && groupFin){
            sel.append(" * ");
        }else{
            //TODO: 首先判断查询字段是否存在，如不存在则判断case语句是否存在，若存在则不做任何操作反则查询全部，若字段存在，则在case语句句柄末尾添加逗号防止sql生成出错
            if(this.isGroup){
                sel.append(groupFin ? " " : (caseFin ? " " : " , ") + engine.get("groupColumn"));
            }
            sel.append(appointFin ? " " : (caseFin ? " " : " , ") + engine.get("appointColumn"));
        }

        //TODO : 将from子句抽离，提供计算总条数方法使用
        StringBuffer fromSql = new StringBuffer();
        fromSql.append(" from ")
                .append(engine.getString("QueryRoleSql"))
                .append(engine.get("execute"))
                .append(" ")
                .append(engine.get("join") == null ? "" : engine.get("join"))
                .append(engine.get("query") == null ? "" : (" where " + engine.get("query")))
                .append(engine.get("groupBy") == null ? "" : (" group by " + engine.get("groupBy")))
                .append(engine.get("groupQuery") == null ? "" : (" having " + engine.get("groupQuery")))
                .append(dataSort.substring(0, dataSort.length() - 1));

        sel.append(fromSql);
        engine.put("fromSql", this.assignment(data, sel.toString()));

        sel.append(engine.get("dataPaging") == null ? "" : engine.get("dataPaging"));

        String parseSql = sel.toString();

        engine.put("select", this.assignment(data, parseSql));
        //TODO : 清空并重置当前引擎装载的参数
        this.isGroup = false;
        engine.remove("execute");
        engine.remove("child");
        engine.remove("join");
        engine.remove("query");
        engine.remove("dataSort");
        engine.remove("dataPaging");
        engine.remove("tableName");
        engine.remove("alias");
        return this;
    }

    /**
     * 根据已有条件检索查询查询目录总条数信息
     *
     * @return
     */
    public sqlEngine selectTotal() {
        StringBuffer totalSql = new StringBuffer();

        totalSql.append(" select count(1) from ( ").append(engine.get("fromSql")).append(" ) total_sel");

        engine.put("totalSql", totalSql);

        engine.remove("fromSql");
        return this;
    }


    //TODO : 获取sql引擎处理结果    ↓↓↓↓↓↓↓↓↓
    public BaseData parseSql() {
        return engine;
    }


    /**
     * 数据更新及新增公用提取方法
     *
     * @param key
     * @param value
     * @param isAdd 是否为新增
     * @return
     */
    private sqlEngine executeData(String key, String value, boolean isAdd) {
        String executeKey = isAdd ? "addKeys" : "updKeys";
        value = StringEscapeUtils.escapeSql(value);

        StringBuffer Keys = new StringBuffer();
        StringBuffer values = new StringBuffer();

        Keys.append(engine.get(executeKey) == null ? "" : engine.get(executeKey));
        if (isAdd) {
            values.append(engine.get("addvalues") == null ? "" : engine.get("addvalues"));
        }

        if (key.contains("@")) {
            key = key.replaceAll("@", "");
            if (!StringKit.isSpecialChar(key)) {
                // TODO: 固定入参值
                Keys.append(",").append(key);
                values.append(",");
                if (isAdd) {
                    //TODO ： 仅insertBase开放使用
                    if (value.equals("NULL")) {
                        values.append(value);
                    } else {
                        values.append("'").append(value).append("'");
                    }
                } else {
                    if (value.equals("NULL")) {
                        Keys.append(value);
                    } else {
                        Keys.append(" = '").append(value).append("'");
                    }
                }
            } else {
                this.error();
            }
        } else {
            if (!StringKit.isSpecialChar(key)) {
                Keys.append(",").append(key);
                if (isAdd) {
                    values.append("'params_").append(value.split("#")[0]).append("'");
                } else {
                    Keys.append(" = 'params_").append(value.split("#")[0]).append("'");
                }
                //TODO : 动态参数另存为
                engine.put("executeParam", engine.get("executeParam") == null ? value : (engine.get("executeParam") + ", " + value));
            } else {
                this.error();
            }
        }
        //2019/11/18 -> 新增修正
        engine.put(executeKey, Keys);
        if (isAdd) {
            engine.put("addvalues", values);
        }
        return this;
    }

    //TODO : 更新相关    ↓↓↓↓↓↓↓↓↓
    public sqlEngine updateData(String updKey, String updValue) {
        return this.executeData(updKey, updValue, false);
    }

    /**
     * 更新语句生成
     *
     * @param data
     * @return
     */
    public sqlEngine updateFin(String data) {
        StringBuffer updateSql = new StringBuffer();

        String keys = engine.get("updKeys").toString();
        keys = keys.substring(0, 1).equals(",") ? keys.substring(1, keys.length()) : keys;


        updateSql.append("update ").append(engine.get("tableName")).append(" ")
                .append(engine.get("alias") == null ? engine.get("tableName") : engine.get("alias"))
                .append(" set ").append(keys)
                .append(engine.get("query") == null ? "" : (" where " + engine.get("query")));

        engine.put("executeSql", this.assignment(data, updateSql.toString()));
        //TODO : 清空并重置当前引擎装载的参数
        engine.remove("tableName");
        engine.remove("alias");
        engine.remove("updKeys");
        engine.remove("query");
        return this;
    }

    //TODO : 新增相关    ↓↓↓↓↓↓↓↓↓

    public sqlEngine addData(String addKey, String addValue) {
        return this.executeData(addKey, addValue, true);
    }

    /**
     * 执行表数据复制操作
     *
     * @param child
     * @return
     */
    public sqlEngine insertCopy(sqlEngine child) {
        engine.put("copyData", child.engine.get("select") + "");
        return this;
    }

    /**
     * 新增语句生成
     *
     * @return
     */
    public sqlEngine insertFin(String data) {
        StringBuffer insertSql = new StringBuffer();

        String keys = engine.get("addKeys").toString();
        keys = keys.substring(0, 1).equals(",") ? keys.substring(1, keys.length()) : keys;

        insertSql.append("insert into ").append(engine.get("tableName"))
                .append(" ( ").append(keys);

        String values = engine.get("addvalues").toString();
        values = values.substring(0, 1).equals(",") ? values.substring(1, values.length()) : values;
        if (engine.get("fetch") == null) {
            if (engine.get("copyData") == null) {
                insertSql.append(" ) values (")
                        .append(values).append(")");
            } else {
                insertSql.append(" ) ").append(engine.get("copyData"));
            }
            engine.put("executeSql", this.assignment(data, insertSql.toString()));
        } else {
            insertSql.append(" ) values ").append(values);
            engine.put("executeSql", insertSql.toString());
        }

        //TODO : 清空并重置当前引擎装载的参数
        engine.remove("tableName");
        engine.remove("alias");
        engine.remove("addKeys");
        engine.remove("addvalues");
        engine.remove("fetch");
        return this;
    }

    /**
     * 批量新增数据
     *
     * @param fetchList
     * @param keys
     * @return
     */
    public sqlEngine insertFetchPush(String fetchList, String keys) {
        StringBuffer addKeys = new StringBuffer();
        StringBuffer addvalues = new StringBuffer();

        JSONArray fetch = JSON.parseArray(fetchList);

        for (String key : keys.split(",")) {
            if (!key.trim().equals("")) {
                if (!StringKit.isSpecialChar(key)) {
                    addKeys.append(",").append(key);
                } else {
                    this.error();
                }
            }
        }
        //匹配fetch的值
        for (int i = 0; i < fetch.size(); i++) {
            JSONObject jo = fetch.getJSONObject(i);
            addvalues.append(",(");
            for (String key : keys.split(",")) {
                if (!key.equals(""))
                    addvalues.append(jo.get(key) instanceof String ?
                            "'" + StringEscapeUtils.escapeSql(jo.get(key).toString()) + "'" : jo.get(key)).append(",");
            }
            addvalues.deleteCharAt(addvalues.length() - 1);
            addvalues.append(")");
        }
        engine.put("addKeys", addKeys.deleteCharAt(0));
        engine.put("addvalues", addvalues.deleteCharAt(0));
        engine.put("fetch", "fetch");
        return this;
    }

    //TODO : 删除相关    ↓↓↓↓↓↓↓↓↓
    public sqlEngine deleteFin(String data) {
        StringBuffer deleteSql = new StringBuffer();
        String dataSort = engine.get("dataSort") == null ? " " : engine.get("dataSort").toString();

        deleteSql.append(" delete ").append(engine.get("alias")).append(" FROM ")
                .append(engine.get("tableName")).append(" as ").append(engine.get("alias"))
                .append(engine.get("query") == null ? "" : (" where " + engine.get("query")))
                .append(dataSort.substring(0, dataSort.length() - 1))
                .append(engine.get("dataPaging") == null ? "" : engine.get("dataPaging"));

        engine.put("executeSql", this.assignment(data, deleteSql.toString()));
        //TODO : 清空并重置当前引擎装载的参数
        engine.remove("tableName");
        engine.remove("alias");
        engine.remove("query");
        engine.remove("dataSort");
        engine.remove("dataPaging");
        return this;
    }

    //TODO :  拓展相关  引擎授权（查询）  仅限在schema逻辑层使用↓↓↓↓↓↓↓↓↓
    public sqlEngine setSelect(String sql) {
        engine.put("select", sql);
        return this;
    }

    //TODO :  拓展相关  引擎授权（查询）,from 语句 需要配合setSelect，仅限在schema逻辑层使用↓↓↓↓↓↓↓↓↓
    public sqlEngine setSelectFormSQL(String sql) {
        engine.put("fromSql", sql);
        return this;
    }

    //TODO :  拓展相关  引擎授权（增删改）  仅限在schema逻辑层使用↓↓↓↓↓↓↓↓↓
    public sqlEngine setExecute(String sql) {
        engine.put("executeSql", sql);
        return this;
    }
}
