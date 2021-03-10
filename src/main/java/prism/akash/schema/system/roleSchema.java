package prism.akash.schema.system;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.engineEnum.groupType;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.schema.BaseSchema;
import prism.akash.tools.StringKit;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.Schema;
import prism.akash.tools.annocation.checked.AccessType;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统权限相关逻辑类
 *       TODO : 系统·核心逻辑 （独立）
 * @author HaoNan Yan
 */
@Service
@Transactional(readOnly = true)
@Schema(code = "role", name = "系统权限")
public class roleSchema extends BaseSchema {

    @Autowired
    reloadMenuDataSchema reloadMenuDataSchema;

    /**
     * 获取系统默认权限树
     *
     * @param executeData
     *              {
     *                  systemName 系统名称
     *              }
     * @return
     */
    @Access({AccessType.SEL})
    public List<BaseData> getRootRoleTree(BaseData executeData) {
        List<BaseData> mTree = redisTool.getList("system:role:root:tree",null,null);
        if (mTree.isEmpty() || mTree == null) {
            BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
            List<BaseData> list = new ArrayList<>();

            BaseData tree = new BaseData();
            tree.put("id", -1);

            tree.put("title", data.get("systemName"));
            tree.put("expand", true);
            tree.put("is_lock", false);
            tree.put("version", 0);
            tree.put("children", this.getRoleNode("-1"));

            list.add(tree);

            mTree = list;
            redisTool.set("system:role:root:tree", mTree, -1);
        }
        return mTree;
    }


    /**
     * TODO 递归PID
     * 根据指定的节点PID获取权限信息
     *
     * @param pid
     * @return
     */
    protected List<BaseData> getRoleNode(String pid) {
        //查询并获取当前递归节点数据信息
        String selectPid = "select id,name,state,order_number,is_parent,version from sys_role where pid = '" + pid + "'  and state = 0  order by order_number asc";
        return roleNode(selectPid);
    }


    /**
     * TODO 递归ID
     * 根据指定的节点获取权限信息
     *
     * @param id
     * @return
     */
    protected List<BaseData> getRoleNodeByID(String id) {
        //查询并获取当前递归节点数据信息
        String selectid = "select id,name,state,order_number,is_parent,version from sys_role where id = '" + id + "'  and state = 0  order by order_number asc";
        return roleNode(selectid);
    }


    private List<BaseData> roleNode(String sql){
        List<BaseData> roleList = baseApi.selectBase(new sqlEngine().setSelect(sql));
        List<BaseData> list = new ArrayList<>();
        for (BaseData role : roleList) {
            //roleId不为空时，当前节点有效
            if (role.get("id") != null) {
                BaseData rTree = new BaseData();
                rTree.put("id", role.get("id"));
                rTree.put("value", role.get("id"));
                rTree.put("key", role.get("id"));
                rTree.put("title", role.get("name"));
                //默认节点不展开
                rTree.put("expand", false);
                rTree.put("version", role.get("version"));
                rTree.put("is_lock", role.get("state").equals("0") ? false : true);
                //判断当前节点是否为父节点
                //TODO  0-否 / 1-是
                if (role.getInter("is_parent") == 1) {
                    rTree.put("children", getRoleNode(role.getString("id")));
                }
                list.add(rTree);
            }
        }
        return list;
    }

    /**
     * 根据ID获取指定权限节点的信息
     *
     * @param executeData 待获取的数据节点ID
     *                    {
     *                    id: 数据节点id
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public BaseData getRoleNodeData(BaseData executeData) {
        BaseData resultData = new BaseData();
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String result = redisTool.get("system:role:id:"+data.get("id"));
        if (result.isEmpty() || result == null){
            resultData = selectByOne(enCapsulationData("sys_role", executeData));
            //如果获取值为空,则锁定当前数据1分钟,1分钟内禁止对数据库进行访问
            if (resultData == null) {
                redisTool.set("system:role:id:" + data.get("id"), JSON.toJSONString(new BaseData()), 60000);
            } else {
                redisTool.set("system:role:id:" + data.get("id"), JSON.toJSONString(resultData), -1);
            }
        } else {
            resultData = StringKit.parseBaseData(result);
        }
        return resultData;
    }

    /**
     * 获取当前用户权限是否为管理员角色
     *
     * @param executeData
     * @return
     */
    public int getRoleUserData(BaseData executeData) {
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));

        List<BaseData> result = baseApi.selectBase(new sqlEngine().
                setSelect(" select is_admin from  sys_userrole where uid = '"+ data.getString("uid") +"' and  rid = '" + data.getString("rid") + "'"));

        return result.size() > 0 ? result.get(0).getInter("is_admin") : -1;
    }


    /**
     * 内部方法：对单数据节点的增删改缓存操作进行优化提出
     * @param id    数据节点id
     * @return
     */
    private BaseData redisCache(String id) {
        BaseData select = new BaseData();
        select.put("id", id);
        return getRoleNodeData(pottingData("sys_role", select));
    }

    /**
     * 新增权限节点
     *
     * @param executeData 权限节点的数据对象
     * @return
     */
    @Access({AccessType.ADD})
    @Transactional(readOnly = false)
    public String addRoleNode(BaseData executeData) {
        //为了保证数据的强一致性，数据表ID将使用getTableIdByCode方法进行指向性获取
        String result = baseApi.insertData(getTableIdByCode("sys_role"), StringKit.parseSchemaExecuteData(executeData));
        if (!result.equals("")) {
            if (!result.equals("-1") && !result.equals("-2")) {
                redisCache(result);
                //TODO 新增成功时,重置redis缓存
                redisTool.delete("system:role:root:tree");
                deleteRoleKey();
            }
        }
        return result;
    }

    /**
     * 更新权限节点
     *
     * @param executeData 权限节点的待更新数据对象
     * @return
     */
    @Access({AccessType.UPD})
    @Transactional(readOnly = false)
    public int updateRoleNode(BaseData executeData) {
        //为了保证数据的强一致性，数据表ID将使用getTableIdByCode方法进行指向性获取
        int result = baseApi.updateData(getTableIdByCode("sys_role"), StringKit.parseSchemaExecuteData(executeData));
        if (result == 1) {
            BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
            redisCache(data.get("id") + "");
            //TODO 更新成功,重置redis缓存
            redisTool.delete("system:role:root:tree");
            deleteRoleKey();
            redisTool.delete("system:role:id:" + data.get("id"));
            //4.将指定权限缓存重置
            reloadMenuDataSchema.reloadLoginData(data.get("id") + "");
        }
        return result;
    }

    /**
     * 删除权限节点
     *
     * @param executeData 权限节点的待删除的对象
     *                    {id : xxxxxx}
     * @return
     */
    @Access({AccessType.DEL})
    @Transactional(readOnly = false)
    public int deleteRoleNode(BaseData executeData) {
        int result = -99;
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        //查询当前节点下是否拥有子节点
        int size = baseApi.selectBase(new sqlEngine().setSelect(" select id from  sys_role where state = 0 and  pid = '" + data.get("id") + "'")).size();
        if (size == 0) {
            //TODO 使用软删除对数据进行更新操作
            //为了保证数据的强一致性，数据表ID将使用getTableIdByCode方法进行指向性获取
            result = deleteDataSoft(enCapsulationData("sys_role", executeData));
            if (result == 1) {
                redisTool.delete("system:role:id:" + data.get("id"));
                //TODO 删除成功,重置redis缓存
                redisTool.delete("system:role:root:tree");
                deleteRoleKey();
                //4.将指定权限缓存重置
                reloadMenuDataSchema.reloadLoginData(data.get("id") + "");
            }
        }
        return result;
    }

    /**
     * 删除system:menu:root:role所有子级key
     */
    private void deleteRoleKey() {
        String key = "system:menu:root:role:";
        // 获取全部key
        sqlEngine sqlEngine = new sqlEngine().execute("sys_role", "c").appointColumn("c", groupType.DEF, "id").selectFin("");
        List<BaseData> roleKey = baseApi.selectBase(sqlEngine);
        for (BaseData r : roleKey) {
            if (redisTool.getList(key + r.getString("id"),null,null).size() > 0) {
                redisTool.delete(key + r.getString("id"));
            }
        }
        // 超管key
        if (redisTool.getList(key + "-1",null,null).size() > 0) {
            redisTool.delete(key + "-1");
        }
    }

    /**
     * 获取当前可以使用的父节点
     *
     * @param executeData
     * @return
     */
    @Access({AccessType.SEL})
    public List<BaseData> getParentNode(BaseData executeData) {
        String sql = "select *  from sys_role where is_parent = 1 and state = 0 order by order_number asc";
        sqlEngine sel = new sqlEngine().setSelect(sql);
        return baseApi.selectBase(sel);
    }
}
