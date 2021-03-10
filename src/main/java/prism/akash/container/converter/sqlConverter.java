package prism.akash.container.converter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import prism.akash.api.BaseApi;
import prism.akash.container.BaseData;
import prism.akash.container.extend.BaseDataExtends;
import prism.akash.container.sqlEngine.engineEnum.*;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.tools.StringKit;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * TODO : 逻辑引擎编辑转换器
 */
@Component
public class sqlConverter extends BaseDataExtends implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    BaseApi baseApi;


    /**
     * 创建一个新的逻辑引擎
     * @param name               引擎名称
     * @param code               引擎唯一标识CODE
     * @param note               引擎备注信息
     * @param executeData        核心逻辑数据
     * @return
     */
    public ConverterData createBuild(String name, String code, String note, String executeData) {
        //初始化引擎创建工具
        ConverterData init = new ConverterData();
        // TODO : # 创建引擎时，code值及配参值必须通过数据校验！（强制-数据安全）
//        init.setErrorMsg(new ConverterValidator(executeData).verification());
        if(!StringKit.isSpecialChar(code) && init.getErrorMsg() == null){
            //创建实例化引擎
            initConverter(init, name, code, note);

            if (init.getExist()) {
                int newVersion = init.getVersion() + 1;
                // TODO : 更新当前引擎的版本号
                sqlEngine updateEngine = new sqlEngine();
                updateEngine.execute("cr_engine", "c")
                        .updateData("@version", newVersion + "")
                        .queryBuild(queryType.and, "c", "@id", conditionType.EQ, groupType.DEF, init.getEngineId())
                        .updateFin("");
                baseApi.execute(updateEngine);

                // TODO : 更新当前执行的引擎的版本号
                sqlEngine update = new sqlEngine();
                update.execute("cr_engineexecute", "c")
                        .updateData("@state", "0")
                        .updateData("@version", newVersion + "")
                        .queryBuild(queryType.and, "c", "@eid", conditionType.EQ, groupType.DEF, init.getEngineId())
                        .queryBuild(queryType.and, "c", "@version", conditionType.EQ, groupType.DEF, init.getVersion() + "")
                        .updateFin("");
                baseApi.execute(update);
            }

            //解析核心逻辑数据
            JSONArray coverArray = JSONArray.parseArray(executeData);
            for (int i = 0; i < coverArray.size(); i++) {
                execute(init, false, coverArray.getJSONObject(i).toJSONString());
            }

            paramBinding(init);
        }
        return init;
    }

    /**
     * 入参字段信息绑定
     * @param init     核心引擎数据对象
     * @return
     */
    private ConverterData paramBinding(ConverterData init){
        // TODO ：获取当前引擎的执行结果
        BaseData execute = super.invokeDataInteraction(new sqlEngine(), init.getEngineId(), "", false).parseSql();
        if (execute != null) {
            init.setExecute(execute);
            // TODO : 对指定的入参字段进行抽离另存
            if (execute.get("executeParam") != null){
                // TODO : 重新进行数据绑定
                String ver = getVersion("cr_engineparam",init.getEngineId());
                this.bindFiled(execute.get("executeParam") + "","cr_engineparam",init.getEngineId(),ver);

            }
            if(execute.get("outFiled") != null){
                String ver = getVersion("cr_engineout",init.getEngineId());
                //提出stringBuffer用来管理
                //TODO: 20/6/3 提出 进行统一化管理
                StringBuffer sb = new StringBuffer();
                for (String ac : (execute.get("outFiled")+"").split(",")){
                    //若存在*,即视为全部
                    if(ac.indexOf("*") > -1){
                        //根据表获取（不建议使用数据别名）对应字段
                        List<BaseData> filed = baseApi.selectBase(new sqlEngine()
                                .execute("cr_field", "f")
                                .appointColumn("f", groupType.DEF, "name,code")
                                .queryBuild(queryType.and, "f", "@tid", conditionType.EQ, groupType.DEF, ac.split("\\.")[0])
                                .queryBuild(queryType.and, "f", "@state", conditionType.EQ, groupType.DEF, "0")
                                .selectFin(""));
                        execute.remove("outFiled");
                        for (BaseData f : filed){
                            sb.append(",");
                            sb.append(f.get("code") + "#" + f.get("name"));
                        }
                        sb = sb.deleteCharAt(0);
                        execute.put("outFiled",sb);
                    }else{
                        //不存在*
                        // TODO : 输出字段绑定
                        sb.append(",").append(ac);
                    }
                }
                this.bindFiled(sb.toString(),"cr_engineout",init.getEngineId(),ver);
            }
        }
        return init;
    }

    /**
     * 字段绑定方法（提取）
     * @param filed
     * @param table
     * @param eid
     * @param ver  统一版本号
     */
    private void bindFiled(String filed,String table,String eid,String ver){
        //执行新增前先更新历史数据
        this.updateState(table,eid);
        for (String ac : filed.split(",")){
            if (!ac.equals("")){
                String [] codeAndName = ac.indexOf(" as ") > -1 ? ac.split(" as ") : ac.split("#");
                sqlEngine addParam = new sqlEngine().execute(table, "")
                        .addData("@id", StringKit.getUUID())
                        .addData("@name", codeAndName.length > 1 ? codeAndName[1].trim() :"")
                        .addData("@code", codeAndName[0].trim())
                        .addData("@engineId", eid)
                        .addData("@version", ver)
                        .addData("@state", "0")
                        .insertFin("");
                baseApi.execute(addParam);
            }
        }
    }

    /**
     * 获取当前数据版本信息（如存在则将历史版本状态更新为禁用）
     * @param table
     * @param eid
     * @return
     */
    private String getVersion(String table,String eid){
        //获取版本信息
        List<BaseData> list =  baseApi.selectBase(new sqlEngine()
                .execute(table, "e")
                .appointColumn("e",groupType.DEF,"version")
                .queryBuild(queryType.and, "e", "@engineId", conditionType.EQ, null,eid)
                .queryBuild(queryType.and, "e", "@state", conditionType.EQ, null, "0")
                .dataSort("e", "version", sortType.DESC)
                .dataPaging("@0","@1")
                .selectFin(""));
        return list.size() > 0 ? (Integer.parseInt(list.get(0).get("version") + "") + 1) + "" : "0";
    }

    //更新数据
    private int updateState(String table,String eid){
        //更新状态
        return baseApi.execute(new sqlEngine().execute(table, "c")
                .updateData("@state",  "1")
                .queryBuild(queryType.and, "c", "@engineId", conditionType.EQ, groupType.DEF, eid)
                .updateFin(""));
    }

    /**
     * 检查当前引擎Code值是否已被使用
     *
     * @param code
     * @return
     */
    private BaseData checkCodeExist(String code) {
        List<BaseData> exist = baseApi.selectBase(new sqlEngine()
                .execute("cr_engine", "c")
                .appointColumn("c", groupType.DEF, "id")
                .queryBuild(queryType.and, "c", "@code", conditionType.EQ, groupType.DEF, code)
                .queryBuild(queryType.and, "c", "@engineType", conditionType.EQ, groupType.DEF, "0")
                .queryBuild(queryType.and, "c", "@state", conditionType.EQ, groupType.DEF, "0").selectFin(""));
        return exist.size() > 0 ? exist.get(0) : null;
    }

    /**
     * 初始化逻辑执行引擎
     * @param name      引擎名称
     * @param code      引擎唯一标识CODE
     * @param note      引擎备注信息
     * @return
     */
    private String initConverter(ConverterData initData, String name, String code, String note) {
        //实例初始化
        initData.setSort(0);
        initData.setExist(false);
        initData.setVersion(0);

        BaseData converter = checkCodeExist(code);
        if (converter == null) {
            initData.setEngineId(StringKit.getUUID());
            // TODO : 逻辑引擎不存在则执行新建
            sqlEngine addEngine = new sqlEngine().execute("cr_engine", "")
                    .addData("@id", initData.getEngineId())
                    .addData("@name", name)
                    .addData("@code", code)
                    .addData("@note", note)
                    .addData("@engineType", "0")
                    .addData("@state", "0")    //TODO 正式版本该状态创建时为2 （待审核）
                    .addData("@executeVail", "0")
                    .addData("@version", initData.getVersion() + "")
                    .insertFin("");
            baseApi.execute(addEngine);
        } else {
            // TODO : 逻辑引擎存在则变更指令标识为true
            initData.setExist(true);
            initData.setEngineId(converter.getString("id"));
        }
        return initData.getEngineId();
    }

    /**
     * 核心逻辑节点载入
     * @param isChild    当前节点是否使用子查询模式
     * @param data       当前节点预处理JSON值
     * @return
     */
    private String execute(ConverterData initData, boolean isChild, String data) {
        //如果当前engineId已生成（引擎已创建成功）
        if (initData.getEngineId() != null) {
            String id = StringKit.getUUID();
            boolean child = false;
            sqlEngine addExecute = new sqlEngine().execute("cr_engineexecute", "")
                    .addData("@id", id)
                    .addData("@eid", isChild ? initData.getChildId() : initData.getEngineId())
                    .addData("@state", "0")
                    .addData("@version", initData.getVersion() + "")
                    .addData("@sorts", isChild ? initData.getChildSort() + "" : initData.getSort() + "");
            LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(data);
            //TODO : 确认传入字段存在
            for (String key : params.keySet()) {
                if (key.equals("childList")) {
                    initData.setChildId(id);
                    child = true;
                    // TODO : 子查询内循环
                    JSONArray ja = JSONArray.parseArray(params.get(key).toString());
                    for (int j = 0; j < ja.size(); j++) {
                        JSONObject jo = ja.getJSONObject(j);
                        execute(initData,true,jo.toJSONString());
                    }
                } else {
                    addExecute.addData("@" + key, params.get(key).toString());
                }
            }
            addExecute.addData("@isChild", child ? "1" : "0");
            addExecute.insertFin("");
            baseApi.execute(addExecute);
            if(isChild){
                initData.setChildSort(initData.getChildSort()+1);
            }else{
                initData.setSort(initData.getSort()+1);
            }
            return id;
        }else{
            return null;
        }
    }
}
