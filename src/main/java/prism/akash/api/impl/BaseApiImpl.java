package prism.akash.api.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import prism.akash.api.BaseApi;
import prism.akash.container.BaseData;
import prism.akash.container.extend.BaseDataExtends;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.dataInteraction.BaseInteraction;
import prism.akash.tools.StringKit;
import prism.akash.tools.date.dateParse;
import prism.akash.tools.reids.RedisTool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component("baseApiImpl")
public class BaseApiImpl extends BaseDataExtends implements BaseApi {

    @Autowired
    BaseInteraction baseInteraction;
    @Autowired
    dateParse dateParse;
    @Autowired
    RedisTool redisTool;

    private BaseData getEngineData(String id, String executeData){
        return super.invokeDataInteraction(
                new sqlEngine(executeData),
                id, executeData,
                false)
                .parseSql();
    }

    @Override
    public List<BaseData> select(String id, String executeData) {
        BaseData bd = this.getEngineData(id, executeData);
        return bd.get("select") == null ? null : baseInteraction.select(bd);
    }

    @Override
    public Map<String, Object> selectPage(String id, String executeData) {
        Map<String, Object> reObj = new ConcurrentHashMap<>();
        BaseData selectPage = this.getEngineData(id,executeData);
        if (selectPage.get("select") != null) {
            reObj.put("data", baseInteraction.select(selectPage));
        }
        if (selectPage.get("totalSql") != null) {
            Integer total = baseInteraction.selectNums(selectPage);
            reObj.put("total", total == null ? 0 : total);
        }
        return reObj;
    }

    @Override
    public Map<String, Object> selectPageBase(sqlEngine sqlEngine) {
        Map<String, Object> reObj = new ConcurrentHashMap<>();
        BaseData selectPage = sqlEngine.parseSql();
        if (selectPage.get("select") != null) {
            List<BaseData> list = baseInteraction.select(selectPage);
            reObj.put("data", list);
        }
        if (selectPage.get("totalSql") != null) {
            Integer total = baseInteraction.selectNums(selectPage);
            reObj.put("total", total == null ? 0 : total);
        }
        return reObj;
    }

    @Override
    public List<BaseData> selectBase(sqlEngine sqlEngine) {
        BaseData bd = sqlEngine.parseSql();
        return bd.get("select") == null ? null : baseInteraction.select(bd);
    }

    @Override
    @Transactional
    public int execute(String id, String executeData) {
        BaseData bd = this.getEngineData(id, executeData);
        return bd.get("executeSql") == null ? null : baseInteraction.execute(bd);
    }

    @Override
    public BaseData selectByOne(String id, String executeData) {
        LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(executeData);
        String tableCode = getTableCode(id);
        if(!tableCode.isEmpty()){
            BaseData select = new BaseData();
            StringBuffer sb = new StringBuffer();
            String fields = params.get("fields") == null ? "" : params.get("fields")+"";
            //如果有传入字段，则需进行字段对比，确认字段存在
            if(!fields.isEmpty()){
                //获取字段集合
                List<BaseData> fieldList = getFieldList(id);
                for (String f : fields.split(",")){
                    String [] fk = f.split("#");
                    for (BaseData field:fieldList) {
                        if(f.indexOf("#") > -1){
                            //TODO 存在别名
                            if(field.getString("code").equals(fk[0])){
                                sb.append(StringEscapeUtils.escapeSql(fk[0])).append(" as ").append(StringEscapeUtils.escapeSql(fk[1])).append(",");
                                //为了提升系统性能，一旦获取匹配值则跳出当前循环
                                break;
                            }
                        }else{
                            if(field.getString("code").equals(f)){
                                sb.append(f).append(",");
                                //为了提升系统性能，一旦获取匹配值则跳出当前循环
                                break;
                            }
                        }
                    }
                }
            }else{
                sb.append(" * ");
            }
            select.put("select", "select " + sb.deleteCharAt(sb.length() - 1) + " from " + tableCode + " where  id = '" + StringEscapeUtils.escapeSql(params.get("id") + "") + "'");
            List<BaseData> dataList = baseInteraction.select(select);
            return dataList.size() > 0 ? dataList.get(0) : null;
        }else{
            return null;
        }
    }

    @Override
    @Transactional
    public int execute(sqlEngine sqlEngine) {
        return baseInteraction.execute(sqlEngine.parseSql());
    }


    /**
     * 内部方法：根据指定数据表ID获取CODE
     * @param id    数据表ID
     * @return
     */
    private String getTableCode(String id){
        //TODO 由前端接取的id数据经过proxy封装后会附带双引号,故需要在添加缓存时去掉，避免后期使用时产生混淆
        String cacheId = id.replaceAll("\"","");
        String code = redisTool.get("core:table:code:id:" + cacheId);
        if (code.isEmpty() || code == null) {
            BaseData select = new BaseData();
            select.put("select", "select code from cr_tables where state = 0 and id = '" + StringEscapeUtils.escapeSql(cacheId) + "'");
            List<BaseData> tables = baseInteraction.select(select);
            code = tables.size() > 0 ? tables.get(0).get("code") + "" : "";
            //持久化当前表code
            redisTool.set("core:table:code:id:" + cacheId, code, -1);
        }
        return code;
    }

    /**
     * 内部方法：根据指定数据表ID获取字段
     * @param id    数据表ID
     * @return
     */
    private List<BaseData> getFieldList(String id){
        //TODO 由前端接取的id数据经过proxy封装后会附带双引号,故需要在添加缓存时去掉，避免后期使用时产生混淆
        String cacheId = id.replaceAll("\"","");
        //从redis缓存内读取相应数据
        List<BaseData> fieldList = redisTool.getList("core:field:list:id:" + cacheId, null, null);
        if (fieldList.size() == 0) {
            BaseData select = new BaseData();
            select.put("select", "select code from cr_field where state = 0 and tid = '" +  StringEscapeUtils.escapeSql(cacheId) + "'");
            fieldList = baseInteraction.select(select);
            //持久化当前表字段
            redisTool.set("core:field:list:id:" + cacheId, fieldList, -1);
        }
        return fieldList.size() > 0 ? fieldList : new ArrayList<>();
    }

    /**
     * 内部方法：根据ID获取指定数据
     * @param id        数据id
     * @param code      数据表CODE
     * @return
     *          -1 : 失败
     *          >-1：成功
     */
    private int getDataVersion(String id,String code){
        BaseData select = new BaseData();
        select.put("select", "select version from " + code + " where id = " + StringEscapeUtils.escapeSql(id));
        List<BaseData> fields = baseInteraction.select(select);
        return fields.size() > 0 ? Integer.parseInt(fields.get(0).get("version")+"") : -1;
    }

    @Override
    @Transactional
    public String  insertData(String id, String executeData) {
        String state = "0";
        LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(executeData);
        String tableCode = getTableCode(id);
        if(!tableCode.isEmpty()){
            //声明sql组装
            StringBuffer insert = new StringBuffer(" insert into ");
            insert.append(tableCode).append(" ( ");

            StringBuffer keys = new StringBuffer();
            StringBuffer values = new StringBuffer();
            //获取字段集合
            List<BaseData> fields = getFieldList(id);
            //新增的UUID
            String uuid = StringKit.getUUID();
            if (fields.size() > 0){
                //主键UUID
                params.put("id", uuid);
                //TODO 核心表没有create_time字段
                if (!tableCode.split("_")[0].equals("cr")){
                    //数据创建时间
                    params.put("create_time", dateParse.formatDate("yyyy-MM-dd HH:mm:ss", new Date()));
                }
                //数据版本
                params.put("version", 0);
                //数据状态(1-正常，0-已删除）
                params.put("state", 0);
                for (String key : params.keySet()) {
                    //数据强校验，以保证传入的数据字段真实有效
                    for (BaseData field:fields) {
                        if(field.getString("code").equals(key)){
                            keys.append("`").append(key).append("`,");
                            if(params.get(key).equals("")){
                                values.append(" null ");
                            }else{
                                String value = params.get(key) + "";
                                values.append("'").append(StringEscapeUtils.escapeSql(value)).append("'");
                            }
                            values.append(",");
                            //为了提升系统性能，一旦获取匹配值则跳出当前循环
                            break;
                        }
                    }
                }
                insert.append(keys.deleteCharAt(keys.length()-1)).append(" ) values ( ");
                insert.append(values.deleteCharAt(values.length()-1)).append(" )");

                //执行新增
                BaseData bd = new BaseData();
                bd.put("executeSql",insert.toString());
                state =  baseInteraction.execute(bd) > 0 ? uuid : "0";
            }else{
                state =  "-1";
            }
        }else{
            state = "-2";
        }
        return state.equals("") ? "0" : state;
    }

    @Override
    public int updateData(String id, String executeData) {
        //数据更新状态
        int state = 0;
        LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(executeData);
        String tableCode = getTableCode(id);
        if(!tableCode.isEmpty()){
            //获取当前数据版本
            int version = getDataVersion(JSON.toJSONString(params.get("id")), tableCode);
            int updVersion = params.get("version") == null ? -1 : Integer.parseInt(params.get("version") + "");
            //判断当前数据是否允许更新
            if(version == -1){
                state = -9;
            }else if(updVersion == -1){
                state = -8;
            }else if(updVersion > version){
                //TODO 当待更新版本号大于系统版本号时，允许更新
                //获取字段集合
                List<BaseData> fields = getFieldList(id);
                if(fields.size() > 0){
                    StringBuffer update = new StringBuffer(" update  ");
                    update.append(tableCode).append(" set ");
                    for (String key : params.keySet()) {
                        //数据强校验，以保证传入的数据字段真实有效
                        for (BaseData field:fields) {
                            String code = field.getString("code");
                            if (code.equals(key)) {
                                if (code.equals("id") || code.equals("version") || code.equals("create_time") || code.equals("last_time")) {
                                    //TODO 不允许手动更新数据主键ID/数据版本version
                                } else {
                                    update.append("`").append(StringEscapeUtils.escapeSql(key)).append("` = ");
                                    if(params.get(key).equals("")){
                                        update.append(" null ");
                                    }else{
                                        update.append("'").append(StringEscapeUtils.escapeSql(params.get(key) + "")).append("'");
                                    }
                                    update.append(" , ");
                                }
                                //为了提升系统性能，一旦获取匹配值则跳出当前循环
                                break;
                            }
                        }
                    }
                    update.append(" version = ").append(updVersion);
                    if (!tableCode.split("_")[0].equals("cr")) {
                        //数据最后访问时间
                        update.append(" ,last_time = '").append(dateParse.formatDate("yyyy-MM-dd HH:mm:ss", new Date())).append("'");
                    }
                    update.append(" where id = '").append(StringEscapeUtils.escapeSql(params.get("id") + "")).append("'");
                    //TODO sys系统源数据在更新时需要追加is_lock条件
                    if (tableCode.split("_")[0].equals("sys")) {
                        update.append(" and is_lock = 0 ");
                    }
                    //执行新增
                    BaseData bd = new BaseData();
                    bd.put("executeSql", update.toString());
                    state = baseInteraction.execute(bd);
                }else{
                    state = -1;
                }
            }else{
                state = -3;
            }
        }else{
            state = -2;
        }
        return state;
    }

    @Override
    public int deleteData(String id, String executeData) {
        //删除状态
        int state = 0;
        LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(executeData);
        String tableCode = getTableCode(id);
        if(!tableCode.isEmpty()){
            if(params.get("id") != null){
                if(!(params.get("id")+"").isEmpty()){
                    BaseData bd = new BaseData();
                    //暴力删除仅允许删除非活跃数据（state状态为禁用，经过soft软删除的数据）
                    String delete = "delete from " + tableCode + " where  id = " + JSON.toJSONString(StringEscapeUtils.escapeSql(params.get("id") + ""));
                    //确认删除标识，不为空时可删除任意状态的数据
                    if (params.get("del_submit") == null){
                        delete += " and state = 1 ";
                    }
                    //TODO system系统源数据在删除时需要追加is_lock条件
                    if (tableCode.split("_")[0].equals("sys")){
                        delete  +=  " and is_lock = 0 ";
                    }
                    bd.put("executeSql", delete);
                    state = baseInteraction.execute(bd);
                }
            }else{
                state = -1;
            }
        }else{
            state = -2;
        }
        return state;
    }

}
