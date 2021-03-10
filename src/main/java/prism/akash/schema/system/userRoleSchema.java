package prism.akash.schema.system;

import com.alibaba.fastjson.JSON;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 系统用户权限相关类
 * TODO : 系统·核心逻辑 （独立）
 *
 * @author HaoNan Yan
 */
@Service
@Transactional(readOnly = true)
@Schema(code = "userRole", name = "系统用户权限「授权」")
public class userRoleSchema extends BaseSchema {

    @Autowired
    userSchema userSchema;

    @Autowired
    roleSchema roleSchema;


    /**
     * 新增用户基础信息
     * TODO 本接口不涉及权限绑定
     *
     * @param executeData 待新增的用户基础信息数据
     * @return
     */
    @Access({AccessType.ADD})
    @Transactional(readOnly = false)
    public String addUserInfo(BaseData executeData) {
        //为了保证数据的强一致性，数据表ID将使用getTableIdByCode方法进行指向性获取
        String result = baseApi.insertData(getTableIdByCode("sys_user"), StringKit.parseSchemaExecuteData(executeData));
        if (!result.equals("")) {
            if (!result.equals("-1") && !result.equals("-2")) {
                // 解析初始化权限并进行绑定
                BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
                String roles = data.getString("role");
                int i = 0;
                for (String r : roles.split(",")) {
                    if(!r.isEmpty()){
                        data.put("rid",r);
                        data.put("uid",result);
                        BaseData execute = new BaseData();
                        execute.put("executeData", JSON.toJSONString(data));
                        bindUserRole(execute);
                    }
                    i++;
                }
            }
        }
        return result;
    }

    /**
     * 用户授权操作
     * TODO 通过权限树进行点选
     *
     * @param executeData 授权对象
     *                    {
     *                    *uid: 用户id    TODO 必填字段
     *                    *rid: 权限id    TODO 必填字段
     *                    }
     * @return
     */
    @Access({AccessType.ADD, AccessType.DEL})
    @Transactional(readOnly = false)
    public String bindUserRole(BaseData executeData) {
        String result = "-5";
        //解析executeData
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        //TODO 数据强校验
        //进行授权时,uid及rid不能为null
        if (data.get("uid") != null && data.get("rid") != null) {
            String uid = data.getString("uid");
            String rid = data.getString("rid");
            String current_role = data.getString("system_current_role");
            //0.判断uid及rid是否存在
            data.put("id", uid);
            BaseData user = userSchema.selectUser(pottingData("", data));
            data.put("id", rid);
            BaseData role = roleSchema.getRoleNodeData(pottingData("", data));
            if (user == null) {
                result = "UR1";
            } else if (role == null) {
                result = "UR2";
            } else {
                //1.先获取已有权限，没有则新增，有则删除
                List<BaseData> userData = inspectRole(uid, rid);
                if (userData.size() == 0) {
                    //获取最新的数据下标
                    List<BaseData> allRole = redisTool.getList("system:user_role:id:" + uid, null, null);
                    int newOrder = 0;
                    if (allRole.size() > 0) {
                        newOrder = allRole.get(allRole.size() - 1).getInter("order_number") + 1;
                    }
                    //执行新增
                    result = addUserRole(uid, rid, newOrder, data.getString("is_admin"));
                } else {
                    //执行删除
                    result = deleteUserRole(userData.get(0).getString("id"), uid) + "";
                }
                redisTool.delete("system:menu:root:tree:role:"+current_role);
            }
        }
        return result;
    }

    /**
     * 获取用户可以使用的权限树
     *
     * @param executeData
     *              {
     *                  systemName              系统名称
     *                  system_current_role     当前用户使用的权限「Access会自动设置」
     *              }
     * @return
     */
    @Access({AccessType.SEL})
    public List<BaseData> getRoleMenuTree(BaseData executeData) {
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String current_role = data.getInter("system_current_supervisor") == 1 ? data.getString("system_current_role_pid") : data.getString("system_current_role");
        List<BaseData> mTree = redisTool.getList("system:menu:root:role:"+current_role, null, null);
        if (mTree.isEmpty() || mTree == null) {
            List<BaseData> list = new ArrayList<>();

            BaseData tree = new BaseData();
            tree.put("id", -1);
            tree.put("value", -1);
            tree.put("key", -1);
            tree.put("title", data.get("systemName"));
            tree.put("expand", true);
            tree.put("is_lock", false);
            tree.put("version", 0);
            tree.put("children", data.getInter("system_current_supervisor") == 1 ? roleSchema.getRoleNode(current_role) : roleSchema.getRoleNodeByID(current_role));

            list.add(tree);

            mTree = list;
            redisTool.set("system:menu:root:role:"+current_role, mTree, -1);
        }
        return mTree;
    }


    /**
     * 获取当前用户拥有的权限信息
     *
     * @param executeData {
     *                    uid: 用户id
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public List<BaseData> getCurrentRole(BaseData executeData) {
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String userId = data.getString("uid");
        //从redis里获取当前用户的权限缓存
        List<BaseData> userData = redisTool.getList("system:user_role:id:" + userId, null, null);
        if (userData.size() == 0) {
            //未获取到缓存数据，请求数据
            String roles = "select ur.is_admin,ur.id,ur.uid,ur.rid,ur.state,ur.order_number,r.name,r.note,r.is_supervisor,r.index_page " +
                    "from sys_userrole ur left join sys_role r on r.id = ur.rid " +
                    "where r.state = 0 and  ur.uid = '" + userId + "' and ur.state = 0 order by ur.order_number asc";
            userData = baseApi.selectBase(new sqlEngine().setSelect(roles));
            if (userData.size() > 0) {
                redisTool.set("system:user_role:id:" + userId, userData, -1);
            }
        }
        return userData == null ? new ArrayList<>() : userData;
    }

    /**
     * 获取当前用户可使用的权限
     *
     * @param executeData {
     *                    uid: 用户id
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public Map<String,List<String>> getRoleChecked(BaseData executeData){
        Map<String,List<String>> map = new ConcurrentHashMap<>();
        List<String> result = new ArrayList<>();
        List<String> resultAdmin = new ArrayList<>();
        List<String> resultNormal = new ArrayList<>();
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String current_uid = data.getString("uid").isEmpty() ?  data.getString("system_current_user") : data.getString("uid");
        if (!current_uid.isEmpty()){
            data.put("uid",current_uid);
            List<BaseData> ur = this.getCurrentRole(pottingData("",data));
            for (BaseData r:ur) {
                if(r.getInter("is_admin") == 1){
                    resultAdmin.add(r.getString("rid"));
                }else{
                    resultNormal.add(r.getString("rid"));
                }
                result.add(r.getString("rid"));
            }
        }
        map.put("checked",result);
        map.put("adminChecked",resultAdmin);
        map.put("normalChecked",resultNormal);
        return map;
    }

    /**
     * 内部方法 : 权限检测
     *
     * @param userId 用户编号
     * @param roleId 指定的权限编号
     * @return
     */
    private List<BaseData> inspectRole(String userId, String roleId) {
        BaseData user = new BaseData();
        user.put("uid", userId);
        //从redis里获取当前用户的权限缓存
        List<BaseData> userData = getCurrentRole(pottingData("", user));
        //TODO 检测当前权限是否已存在
        return userData.stream().filter(ur -> (ur.get("rid") + "").equals(roleId)).collect(Collectors.toList());
    }

    /**
     * 内部方法：新增用户授权
     *
     * @param userId       用户编号
     * @param roleId       指定的权限编号
     * @param order_number 当前授权序列号
     * @return
     */
    @Transactional(readOnly = false)
    protected String addUserRole(String userId, String roleId, int order_number,String is_admin) {
        BaseData executeData = new BaseData();
        executeData.put("uid", userId);
        executeData.put("rid", roleId);
        executeData.put("is_admin", is_admin);
        executeData.put("order_number", order_number);
        String result = insertData(pottingData("sys_userrole", executeData));;
        if (result.length() == 32) {
            //新增成功后,将redis缓存重置
            redisTool.delete("system:user_role:id:" + userId);
        }
        return result;
    }

    /**
     * 内部方法：移除用户授权
     *
     * @param ur_id  授权信息id
     * @param userId 用户id
     * @return
     */
    @Transactional(readOnly = false)
    protected int deleteUserRole(String ur_id, String userId) {
        String delete = "delete from sys_userrole where id = '" + ur_id + "'";
        int result = baseApi.execute(new sqlEngine().setExecute(delete));
        if (result > 0) {
            //删除成功后,将redis缓存重置
            redisTool.delete("system:user_role:id:" + userId);
        }
        return result;
    }
}
