package prism.akash.schema;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prism.akash.api.BaseApi;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.engineEnum.conditionType;
import prism.akash.container.sqlEngine.engineEnum.groupType;
import prism.akash.container.sqlEngine.engineEnum.queryType;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.controller.BaseController;
import prism.akash.tools.StringKit;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.Schema;
import prism.akash.tools.annocation.checked.AccessType;
import prism.akash.tools.date.dateParse;
import prism.akash.tools.file.ExcelImport;
import prism.akash.tools.http.HTTPTool;
import prism.akash.tools.reids.RedisTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统基础逻辑处理类
 *       TODO : 系统·核心逻辑 （独立）
 *       TODO : ※ 为防止注入风险，Schema层代码凡是直接使用了Controller层executeData参数的，均需要对指定参数进行转义处理 ！
 *                                            ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓
 *                               TODO  「 StringEscapeUtils.escapeSql() 」
 * @author HaoNan Yan
 */
@Service
@Transactional(readOnly = true)
@Schema(code = "base", name = "系统基础方法")
public class BaseSchema implements Serializable {

    @Autowired
    protected RedisTool redisTool;

    @Autowired
    protected BaseApi baseApi;

    @Autowired
    protected dateParse dateParse;

    @Autowired
    protected HTTPTool httpTool;

    @Autowired
    protected ExcelImport excelImport;

    /**
     * 一般数据查询
     * TODO ※注意 本方法不会返回格式化后的分页数据！若需要分页数据请调用@selectPage
     *
     * @param executeData 核心参数
     *                    示例
     *                    ↓↓↓↓↓↓
     *                    {
     *                    id:   待执行调用的SQL引擎编号
     *                    executeData：  所需参数 ： 请根据当前引擎实际情况进行传参
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public List<BaseData> select(BaseData executeData) {
        String id = executeData.getString("id");
        String data = executeData.getString("executeData");
        //1.当id为空，直接返回null
        //2.若executeData为空，则将属性值设置为"{}"
        return id.isEmpty() ? null : baseApi.select(id, data.isEmpty() ? "{}" : data);
    }

    /**
     * 分页数据查询
     *
     * @param executeData 核心参数
     *                    示例
     *                    ↓↓↓↓↓↓
     *                    {
     *                    id:   待执行调用的SQL引擎编号
     *                    executeData：  所需参数 ： 请根据当前引擎实际情况进行传参 , 为保证分页数据准确，请务必按照引擎需求字段进行传参
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public Map<String, Object> selectPage(BaseData executeData) {
        String id = executeData.getString("id");
        String data = executeData.getString("executeData");
        //1.当id为空，直接返回null
        //2.若executeData为空，则将属性值设置为"{}"
        return id.isEmpty() ? null : baseApi.selectPage(id, data.isEmpty() ? "{}" : data);
    }

    /**
     * 根据ID查询单条数据
     *
     * @param executeData 核心参数
     *                    示例
     *                    ↓↓↓↓↓↓
     *                    {
     *                    id:   数据表ID
     *                    executeData：  所需参数
     *                    {
     *                    *id :    需要查询的数据ID ※ 必传参数
     *                    fields : 指定需要返回的字段
     *                    }
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public BaseData selectByOne(BaseData executeData) {
        String id = executeData.getString("id");
        String data = executeData.getString("executeData");
        //执行条件：
        //1.executeData不为空
        //2.executeData中存在id参数
        //3.executeData中id参数不为空
        if (id.isEmpty() && data.isEmpty()) {
            return null;
        } else {
            //TODO 判断核心参数是否存在
            Object paramId = JSONObject.parseObject(data).get("id");
            boolean existId = paramId == null && (paramId + "").equals("null");
            return id.isEmpty() || existId ? null : baseApi.selectByOne(id, data);
        }
    }

    /**
     * 执行数据增删改操作
     * TODO ※暂未开放 风险性较高 涉及复杂型数据增删改时，建议使用schema进行操作
     *
     * @param executeData 核心参数
     *                    示例
     *                    ↓↓↓↓↓↓
     *                    {
     *                    id:   待执行调用的SQL引擎编号
     *                    executeData：  所需参数 ： 请根据当前引擎实际情况进行传参
     *                    }
     * @return
     */
//    @Transactional(readOnly = false)
    private int execute(BaseData executeData) {
        String id = executeData.getString("id");
        String data = executeData.getString("executeData");
        //1.当id为空，直接返回null
        //2.若executeData为空，则将属性值设置为"{}"
        return id.isEmpty() ? null : baseApi.execute(id, data.isEmpty() ? "{}" : data);
    }


    /**
     * 新增数据
     *
     * @param executeData 核心参数
     *                    示例
     *                    ↓↓↓↓↓↓
     *                    {
     *                    id:   数据表ID
     *                    executeData：  所需参数 ： 请根据当前引擎实际情况进行传参
     *                    }
     * @return uuid : 成功
     * -1   : 参数字段不匹配
     * -2   : 数据表不存在
     * ""   : 失败
     */
    @Access({AccessType.ADD})
    @Transactional(readOnly = false)
    public String insertData(BaseData executeData) {
        String id = executeData.getString("id");
        String data = executeData.getString("executeData");
        //1.当id为空，直接返回null
        //2.若executeData为空，则将属性值设置为"{}"
        return id.isEmpty() ? "0" : baseApi.insertData(id, data.isEmpty() ? "{}" : data);
    }


    /**
     * 更新数据
     *
     * @param executeData 核心参数
     *                    示例
     *                    ↓↓↓↓↓↓
     *                    {
     *                    id:   数据表ID
     *                    executeData：  所需参数 ： 请根据当前引擎实际情况进行传参
     *                    {
     *                    *id :     待更新数据ID
     *                    *version: 待更新数据version
     *                    }
     *                    }
     * @return 1   : 成功
     * -1   : 参数字段不匹配
     * -2   : 数据表不存在
     * -3   : 数据版本不匹配
     * -8   ：数据不存在
     * -9   ：入参数据有误（缺少版本号 updVersion）
     * 0   ：失败（数据锁定:is_lock状态）
     */
    @Access({AccessType.UPD})
    @Transactional(readOnly = false)
    public int updateData(BaseData executeData) {
        String id = executeData.getString("id");
        String data = executeData.getString("executeData");
        //执行条件：
        //1.executeData不为空
        //2.executeData中存在id参数
        //3.executeData中id参数不为空
        //3.executeData中version参数不为空
        if (id.isEmpty() && data.isEmpty()) {
            return 0;
        } else {
            return id.isEmpty() ? 0 : baseApi.updateData(id, data);
        }
    }

    /**
     * 数据软删除
     *
     * @param executeData 核心参数
     *                    示例
     *                    ↓↓↓↓↓↓
     *                    {
     *                    id:   数据表ID
     *                    executeData：  所需参数 ： 请根据当前引擎实际情况进行传参
     *                    {
     *                    *id :    待软删除数据的ID
     *                    }
     *                    }
     * @return 1   : 成功
     * -1   : 参数字段不匹配
     * -2   : 数据表不存在
     * -3   : 数据版本不匹配
     * -8   ：数据不存在
     * -9   ：入参数据有误（缺少版本号 updVersion）
     *  0   ：失败（数据锁定:is_lock状态）
     */
    @Access({AccessType.DEL})
    @Transactional(readOnly = false)
    public int deleteDataSoft(BaseData executeData) {
        String id = executeData.getString("id");
        String data = executeData.getString("executeData");
        if (id.isEmpty() && data.isEmpty()) {
            return 0;
        } else {
            Object paramId = JSONObject.parseObject(data).get("id");
            boolean existId = paramId == null && (paramId + "").equals("null");
            //判断需要执行软删除的数据ID是否存在
            if (!existId) {
                //根据ID查询当前数据的数据版本
                BaseData one = new BaseData();
                one.put("id", id);
                BaseData execute = new BaseData();
                execute.put("id", paramId);
                execute.put("filed", "version");
                one.put("executeData", JSON.toJSONString(execute));
                BaseData selectParam = selectByOne(one);
                if (selectParam != null) {
                    BaseData executeUpd = new BaseData();
                    executeUpd.put("id", paramId);
                    executeUpd.put("state", 1);
                    executeUpd.put("version", Integer.parseInt(selectParam.get("version") + "") + 1);
                    //执行更新（软删除）
                    return id.isEmpty() ? 0 : baseApi.updateData(id, JSON.toJSONString(executeUpd));
                } else {
                    return -8;
                }
            } else {
                return -1;
            }
        }
    }

    /**
     * 删除数据 - 暴力删除
     * TODO 本接口仅允许删除经过deleteDataSoft软删除处理的数据 （数据的state状态为1 - 禁用状态）
     *
     * @param executeData 核心参数
     *                    示例
     *                    ↓↓↓↓↓↓
     *                    {
     *                    id:   数据表ID
     *                    executeData：  所需参数 ： 请根据当前引擎实际情况进行传参
     *                    {
     *                    *id :    待删除数据ID
     *                    del_submit : 暴力删除标识,不为null时可以删除任意类型数据
     *                    }
     *                    }
     * @return 0 - 失败（数据锁定:is_lock状态）
     * 1 - 成功
     * -1 - 参数不匹配（没有id)
     * -2 - 不存在表
     */
    @Access({AccessType.DEL})
    @Transactional(readOnly = false)
    public int deleteData(BaseData executeData) {
        String id = executeData.getString("id");
        String data = executeData.getString("executeData");
        //1.当id为空，直接返回null
        //2.若executeData为空，则将属性值设置为"{}"
        return id.isEmpty() ? 0 : baseApi.deleteData(id, data.isEmpty() ? "{}" : data);
    }


    /**
     * 内部方法：用于获取core_engine的缓存数据
     *
     * @return
     */
    protected List<BaseData> getEngineList() {
        //通过redis获取缓存数据
        List<BaseData> tableList = redisTool.getList("core:engine:list", null, null);
        if (tableList.size() == 0) {
            tableList = baseApi.selectBase(new sqlEngine().setSelect(" select id,code,name,engineType from cr_engine where state = 0 "));
            if(tableList.size() > 0){
                redisTool.set("core:engine:list", tableList, -1);
            }else{
                tableList = new ArrayList<>();
            }
        }
        return tableList;
    }


    /**
     * 内部方法：用于获取core_tables的缓存数据
     *
     * @return
     */
    protected List<BaseData> getTableList() {
        //通过redis获取缓存数据
        List<BaseData> tableList = redisTool.getList("core:table:list", null, null);
        if (tableList.size() == 0) {
            tableList = baseApi.selectBase(new sqlEngine().setSelect(" select id,code,name,version from cr_tables where state = 0 "));
            if(tableList.size() > 0){
                redisTool.set("core:table:list", tableList, -1);
            }else{
                tableList = new ArrayList<>();
            }
        }
        return tableList;
    }

    /**
     * 根据table的code值获取tableId
     * TODO 仅提供于Schema层使用
     *
     * @param tableCode 数据表唯一码
     * @return
     */
    public String getTableIdByCode(String tableCode) {
        //通过redis获取缓存数据
        BaseData tableData = getTableDataByCode(tableCode);
        return tableData == null ? "" : tableData.getString("id");
    }

    /**
     * 根据table的code值获取数据表对象
     * TODO 仅提供于Schema层使用
     *
     * @param tableCode 数据表唯一码
     * @return
     */
    protected BaseData getTableDataByCode(String tableCode) {
        //通过redis获取缓存数据
        List<BaseData> tableList = getTableList();
        //通过lambda表达式获取指定数据
        if (tableList != null && tableList.size() > 0) {
            List<BaseData> result = tableList.stream().filter(t -> t.get("code").equals(tableCode)).collect(Collectors.toList());
            return result.size() > 0 ? result.get(0) : null;
        } else {
            return null;
        }
    }

    /**
     * 根据table的Code名称对待执行executeData对象进行重定义封装
     * TODO 仅提供于Schema层使用
     * TODO 请注意，本方法仅适用于executeData为如下格式时使用
     *      {
     *          id:
     *          executeData:{
     *
     *          }
     *      }
     * TODO 解析并提取controller层通过proxy封装的数据进行二次定义封装
     *
     * 常见使用于以下关联方法
     * ↓↓↓↓↓
     * 数据软删除 deleteDataSoft
     * 数据指定ID查询 selectByOne
     *
     * @param tableName
     * @param executeData
     * @return
     */
    public BaseData enCapsulationData(String tableName, BaseData executeData) {
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        return pottingData(tableName, data);
    }


    /**
     * 根据table的Code名称对待执行executeData对象进行重定义封装
     * TODO 仅提供于Schema层使用
     * TODO 请注意，本方法仅适用于executeData为如下格式时使用
     * executeData:{}
     *
     * 常见使用于以下关联方法
     * ↓↓↓↓↓
     * 数据新增 insertData
     * 数据更新 updateData
     * 用户自定义方法 methodXX……
     *
     * @param tableName
     * @param executeData
     * @return
     */
    public BaseData pottingData(String tableName, BaseData executeData) {
        BaseData execute = new BaseData();
        execute.put("id", tableName.isEmpty() || tableName == null ? "" : getTableIdByCode(tableName));
        execute.put("executeData", JSON.toJSONString(executeData));
        return execute;
    }


    /**
     * 启用分页引擎
     * * TODO 常用于导出与查询条件一致时
     * * 示例: 「baseApi.selectPageBase(pageEngine(buildEngine(executeData),executeData,true));」
     *
     * @param sqlEngine     带参sql引擎
     * @param executeData   查询参数数据集
     * @param needTotal     是否需要直接返回总条数
     * @return
     */
    public sqlEngine pageEngine(sqlEngine sqlEngine, BaseData executeData, boolean needTotal) {
        //1.获取并解析关键参数信息
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        Integer pageNo = data.getInter("pageNo");
        Integer pageSize = data.getInter("pageSize");

        pageSize = pageSize == -1 ? 10 : pageSize;
        pageNo = pageNo == -1 ? 0 : pageNo > 1 ? (pageNo - 1) * pageSize : 0;

        sqlEngine.dataPaging("@" + pageNo + "", "@" + pageSize);
        sqlEngine.selectFin("");
        if (needTotal) {
            sqlEngine.selectTotal();
        }
        return sqlEngine;
    }

    /**
     * 「内部方法」 - 数据关联绑定
     *
     * @param info_id 关联的数据ID
     * @param type    关联类型「0-问题分类，1-问题，2-试卷，3-非后台用户」
     * @return
     */
    @Transactional(readOnly = false)
    public String bind_info(String info_id, int type, BaseData executeData) {
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        BaseData info = new BaseData();
        info.put("bind_id", info_id);
        // 如果没有指定数据权限的信息则直接绑定当前用户的数据权限
        info.put("rid", data.get("rid") == null ? data.getString("system_current_role") : data.getString("rid"));
        info.put("type", type);
        return baseApi.insertData(getTableIdByCode("bs_bind_info"), JSON.toJSONString(info));
    }

    /**
     * 「内部方法」 - 根据数据权限获取指定数据
     *
     * @param type 关联类型「0-问题分类，1-问题，2-试卷，3-非后台用户」
     * @param rid  当前用户权限
     * @return
     */
    public sqlEngine buildEngine_bindInfo(int type, String rid) {
        sqlEngine info = new sqlEngine()
                .execute("bs_bind_info", "b")
                .queryBuild(queryType.and, "b", "@type", conditionType.EQ, groupType.DEF, type + "")
                .queryBuild(queryType.and, "b", "@rid", conditionType.QUERY_ROLE, groupType.DEF, rid)
                .appointColumn("b", groupType.DEF, "bind_id,rid#bind_rid")
                .selectFin("");
        return info;
    }

    /**
     * 内部方法：获取当前用户RID
     * @param data
     * @return
     */
    public String getRid(BaseData data){
        return data.getString("rid").isEmpty() ?
                data.getInter("system_current_supervisor") == 1 ? "-1" :
                        data.getInter("system_current_admin") == 1 ? data.getString("system_current_role") : data.getString("system_current_user") : data.getString("rid");
    }

}
