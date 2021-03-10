package prism.akash.schema.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.engineEnum.conditionType;
import prism.akash.container.sqlEngine.engineEnum.groupType;
import prism.akash.container.sqlEngine.engineEnum.queryType;
import prism.akash.container.sqlEngine.engineEnum.sortType;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.schema.BaseSchema;
import prism.akash.tools.StringKit;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.Schema;
import prism.akash.tools.annocation.checked.AccessType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(readOnly = true)
@Schema(code = "rolePatchData", name = "系统权限「角色」数据分配管理")
public class rolePatchDataSchema extends BaseSchema {

    /**
     * 将当前角色绑定在指定菜单下的自定义数据清除「含缓存」
     *
     * @param executeData
     * @return
     */
    @Access({AccessType.DEL, AccessType.ADD})
    @Transactional(readOnly = false)
    public int bindPatchData(BaseData executeData) {
        int result = -8;
        //1.获取并解析关键参数信息
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String rid = data.getString("rid");
        if (!rid.isEmpty()) {
            if (StringKit.isSpecialChar(rid) || StringKit.isSpecialChar(rid)) {
                return 0;
            } else {
                // 先执行删除
                String deleteSql = "delete from sys_role_patch_data where rid = '" + StringEscapeUtils.escapeSql(rid) + "'";
                sqlEngine del = new sqlEngine().setExecute(deleteSql);
                result = baseApi.execute(del) >= 0 ? 1 : 0;
                // 清空缓存
                redisTool.delete("system:role:patch:" + rid);

                JSONArray fetch = data.get("data") == null ? new JSONArray() : (JSONArray) data.get("data");
                if (fetch.size() > 0) {
                    JSONArray addFetch = new JSONArray();

                    for (int i = 0; i < fetch.size(); i++) {
                        JSONObject jo = fetch.getJSONObject(i);
                        jo.put("id", StringKit.getUUID());
                        jo.put("version", 0);
                        jo.put("state", 0);
                        jo.put("rid", rid);
                        jo.put("create_time", dateParse.formatDate("yyyy-MM-dd HH:mm:ss", new Date()));
                        addFetch.add(jo);
                    }

                    sqlEngine add = new sqlEngine()
                            .execute("sys_role_patch_data", "")
                            .insertFetchPush(JSON.toJSONString(addFetch),
                                    "id,version,state,create_time,rid,schema_name,method_name,table_id,patch_filed,patch_type,repeat_val,engine_params")
                            .insertFin("");
                    result = baseApi.execute(add) > 0 ? 1 : 0;
                    if (result == 1) {
                        sqlEngine sel = new sqlEngine()
                                .execute("sys_role_patch_data", "t")
                                .queryBuild(queryType.and, "t", "@rid", conditionType.EQ, groupType.DEF, rid)
                                .selectFin("");
                        List<BaseData> list = baseApi.selectBase(sel);
                        if (list.size() > 0) {
                            redisTool.set("system:role:patch:" + rid, list, -1);
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     * 查询当前权限下全部自定义数据的配置信息
     *
     * @param executeData
     * @return
     */
    @Access({AccessType.SEL})
    public Map<String, Object> selectPatchData(BaseData executeData) {
        //1.获取并解析关键参数信息
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));

        sqlEngine st = new sqlEngine().execute("sys_role_patch_data", "t");
        //TODO 参数匹配
        //请注意,若使用此类型进行数据操作时，请严格按照sqlEngine传参格式在key值前加入固定参数标识@，如@key
        String rid = data.getString("rid");
        if (!rid.isEmpty()) {
            st.queryBuild(queryType.and, "t", "@rid", conditionType.EQ, groupType.DEF, rid);
            st.dataSort("t", "create_time", sortType.DESC);
            st.selectFin("");
            return baseApi.selectPageBase(st);
        } else {
            return new HashMap<>();
        }
    }
}
