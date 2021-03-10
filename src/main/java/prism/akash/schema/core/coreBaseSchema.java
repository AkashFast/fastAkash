package prism.akash.schema.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.schema.BaseSchema;
import prism.akash.tools.StringKit;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.Schema;
import prism.akash.tools.annocation.checked.AccessType;
import prism.akash.tools.druid.InitDataBase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 核心基础数据管理（逻辑）类
 * ※主要用于数据表、数据字段及Schema的同步
 *
 * TODO : 系统·基础核心逻辑 （独立）
 *
 * @author HaoNan Yan
 */
@Service
@Transactional(readOnly = true)
@Schema(code = "coreBase", name = "系统核心数据同步「数据表 / schema逻辑接口」", init = false)
public class coreBaseSchema extends BaseSchema {

    @Autowired
    InitDataBase initDataBase;

    /**
     * 同步当前项目的数据库信息
     * TODO 本方法仅允许超级管理员权限的用户访问及使用
     *
     * @param executeData {
     *                    code : 指定需要初始化的数据表code
     *                    base:  是否同步系统底层数据表
     *                    history: 是否保留历史数据
     *                    }
     * @return
     */
    @Access({AccessType.ADMIN})
    @Transactional(readOnly = false)
    public Map<String, Object> initBaseData(BaseData executeData) {
        //返回值设置
        Map<String, Object> result = new ConcurrentHashMap<>();
        int success = 0;
        List<Object> fail = new ArrayList<>();

        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String tableCode = data.getString("code");
        Boolean base = data.get("base") == null ? false : (Boolean) data.get("base");
        Boolean history = data.get("history") == null ? false : (Boolean) data.get("history");

        List<BaseData> dataBaseList = initDataBase.getDataBase(tableCode, base);
        for (BaseData t : dataBaseList) {
            t.put("history", history);
            if (executeTable(t) > 0) {
                success++;
            } else {
                fail.add(t.get("code"));
            }
        }
        //TODO 不论成功与失败,都会清空缓存数据
        redisTool.delete("core:table:list");
        result.put("success", success);
        result.put("failed", (tableCode.isEmpty() ? dataBaseList.size() : tableCode.split(",").length) - success);
        result.put("failedList", fail);
        return result;
    }

    /**
     * 新增逻辑类
     *
     * @param executeData
     *                  {
     *                      name:
     *                      code:
     *                      note:
     *                      type: 固定类型，只允许传「1」
     *                  }
     * @return
     */
    @Access({AccessType.ADMIN})
    @Transactional(readOnly = false)
    public String addSchemaData(BaseData executeData) {
        String result = "CR1";
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        //为了保证数据的强一致性，数据表ID将使用getTableIdByCode方法进行指向性获取
        if (data.getInter("type") == 1) {
            //确认当前表code是否存在
            //code作为数据表唯一键不允许重复
            if (!existTable(data.getString("code"))) {
                result = baseApi.insertData(getTableIdByCode("cr_tables"), StringKit.parseSchemaExecuteData(executeData));
                if (!result.equals("")) {
                    if (!result.equals("-1") && !result.equals("-2")) {
                        //TODO 新增成功时,重置redis缓存
                        redisTool.delete("core:table:list");
                    }
                }
            }
        }
        return result;
    }

    /**
     * 更新数据表或逻辑类
     *
     * @param executeData 数据表或逻辑类数据对象
     *                    {
     *                    *name :   名称
     *                    *code :   编码
     *                    *note :   注释
     *                    *id   :   数据id
     *                    }
     * @return
     */
    @Access({AccessType.ADMIN})
    @Transactional(readOnly = false)
    public int updateSchemaData(BaseData executeData) {
        int result = 0;
        //为了保证数据的强一致性，数据表ID将使用getTableIdByCode方法进行指向性获取
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String tableId = data.getString("id");
        if (!tableId.isEmpty()) {
            data.put("fields", "id,code,name,version");
            BaseData table = selectByOne(pottingData("cr_tables", data));
            //TODO 确认当前表是否存在
            if (table != null) {
                //重定义executeData数据
                data.put("version", table.getInter("version") + 1);
                BaseData upd = new BaseData();
                upd.put("executeData", JSON.toJSONString(data));
                result = baseApi.updateData(getTableIdByCode("cr_tables"), StringKit.parseSchemaExecuteData(upd));
                if (result > 0) {
                    //清除指定缓存信息
                    redisTool.delete("core:table:list");
                    redisTool.delete("core:field:list:id:" + tableId);
                    redisTool.delete("core:table:code:id:" + tableId);
                }
            }
        }
        return result;
    }

    /**
     * 删除数据表
     * @param executeData
     *                  {
     *                      id:要删除的数据表id
     *                  }
     * @return
     */
    @Access({AccessType.ADMIN})
    @Transactional(readOnly = false)
    public int deleteTable(BaseData executeData) {
        int result = -2;
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String tableId = data.getString("id");
        if (!tableId.isEmpty()) {
            data.put("fields", "id,code,name,version");
            BaseData table = selectByOne(pottingData("cr_tables", data));
            //TODO 确认当前表是否存在
            if (table != null) {
                String code = table.getString("code");
                if (!code.isEmpty()) {
                    //sys及core类型表不允许删除
                    if (code.indexOf("sys_") > -1 || code.indexOf("cr_") > -1) {
                        result = -3;
                    } else {
                        //执行数据软删除
                        result = deleteDataSoft(pottingData("cr_tables", data));
                        if (result > 0) {
                            //清除指定缓存信息
                            redisTool.delete("core:table:list");
                            redisTool.delete("core:field:list:id:" + tableId);
                            redisTool.delete("core:table:code:id:" + tableId);
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     * 内部方法：判断当前code是否存在
     *
     * @param code
     * @return
     */
    private boolean existTable(String code) {
        List<BaseData> tables = getTableList();
        return tables.stream().filter(t -> t.getString("code").equals(code)).collect(Collectors.toList()).size() > 0;
    }


    /**
     * 解析数据对象
     *
     * @param tableData {
     *                  code:    表code
     *                  name:    表名称
     *                  colimns: 表内字段信息
     *
     *                  }
     * @return
     */
    private int executeTable(BaseData tableData) {
        int result = 0;

        String tableId = StringKit.getUUID();
        int version = 0;
        //获取核心数据
        String code = tableData.getString("code");
        String name = tableData.getString("name");
        String colimns = JSON.toJSONString(tableData.get("colimns"));

        //如果name为空则同步为code值
        name = name.isEmpty() ? code : name;
        //TODO 核心值不允许为空
        if (!code.isEmpty() && !name.isEmpty() && !colimns.isEmpty()) {
            BaseData table = getTableDataByCode(code);
            if (table != null) {
                //更新
                tableId = table.getString("id");
                version = table.getInter("version");

                result = baseApi.execute(new sqlEngine().setExecute(" update cr_tables set type = 0 ,code = '" + code + "',name = '" + name + "',version = '" + (version + 1) + "' where id = '" + tableId + "'"));
                if (result > 0) {
                    //更新成功,删除指定缓存
                    redisTool.delete("core:field:list:id:" + tableId);
                    redisTool.delete("core:table:code:id:" + tableId);
                }
            } else {
                //新增
                result = baseApi.execute(new sqlEngine().setExecute(" insert into cr_tables (id,code,name,state,type,version) VALUES ('" + tableId + "','" + code + "','" + name + "',0,0,0)"));
            }

            if (result == 1) {
                boolean history = (boolean) tableData.get("history");
                if (history) {
                    //在history为true时同步字段，会优先将之前字段的数据状态设置为禁用
                    //TODO 误操作保护
                    baseApi.execute(new sqlEngine().setExecute(" update cr_field set state = 1 where tid = '" + tableId + "'"));
                } else {
                    //清除数据历史
                    baseApi.execute(new sqlEngine().setExecute(" delete from cr_field where tid = '" + tableId + "'"));
                }
                //解析字段类型数据
                LinkedHashMap<String, Object> params = JSONObject.parseObject(colimns, new TypeReference<LinkedHashMap<String, Object>>() {
                });

                List<BaseData> fetch = new ArrayList<>();
                String keys = "id,code,name,tid,type,size,sorts,state,version";
                int sorts = 1;
                for (String key : params.keySet()) {
                    String[] dataAttribute = params.get(key).toString().split("\\|\\|");
                    BaseData fe = new BaseData();
                    String fid = StringKit.getUUID();
                    fe.put("id", fid);
                    fe.put("code", key);
                    fe.put("name", dataAttribute[0]);
                    fe.put("type", dataAttribute.length > 0 ? dataAttribute[1] : "");
                    fe.put("size", dataAttribute.length > 1 ? dataAttribute[2] : "0.0");
                    fe.put("tid", tableId);
                    fe.put("state", 0);
                    fe.put("version", version);
                    fe.put("sorts", sorts);
                    fetch.add(fe);
                    sorts++;
                }
                //执行批量新增
                result = baseApi.execute(new sqlEngine().execute("cr_field", "")
                        .insertFetchPush(JSON.toJSONString(fetch), keys.substring(0, keys.length())).insertFin(""));
            }
        }
        return result;
    }

}
