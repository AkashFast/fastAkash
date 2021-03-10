package prism.akash.schema.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.engineEnum.conditionType;
import prism.akash.container.sqlEngine.engineEnum.groupType;
import prism.akash.container.sqlEngine.engineEnum.joinType;
import prism.akash.container.sqlEngine.engineEnum.queryType;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.schema.BaseSchema;
import prism.akash.tools.StringKit;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.Schema;
import prism.akash.tools.annocation.checked.AccessType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统权限菜单相关类
 * TODO : 系统·核心逻辑 （独立）
 *
 * @author HaoNan Yan
 */
@Service
@Transactional(readOnly = true)
@Schema(code = "roleMenu", name = "系统权限菜单关系")
public class roleMenuSchema extends BaseSchema {

    @Autowired
    menuSchema menuSchema;

    @Autowired
    roleSchema roleSchema;

    @Autowired
    reloadMenuDataSchema reloadMenuDataSchema;

    /**
     * 菜单授权操作
     * TODO 通过菜单树进行点选
     *
     * @param executeData 授权对象
     *                    {
     *                    *mid: 菜单id    TODO 必填字段
     *                    *rid: 权限id    TODO 必填字段
     *                    }
     * @return
     */
    @Access({AccessType.ADD, AccessType.DEL})
    @Transactional(readOnly = false)
    public String bindRoleMenu(BaseData executeData) {
        String result = "-5";
        //解析executeData
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        //TODO 数据强校验
        //进行授权时,uid及rid不能为null
        if (data.get("mid") != null && data.get("rid") != null) {
            String mid = data.getString("mid");
            String rid = data.getString("rid");
            //0.判断uid及rid是否存在
            data.put("id", mid);
            BaseData menu = menuSchema.getMenuNodeData(pottingData("", data));
            data.put("id", rid);
            BaseData role = roleSchema.getRoleNodeData(pottingData("", data));
            if (menu == null) {
                result = "UR3";
            } else if (role == null) {
                result = "UR2";
            } else {
                //1.先获取已绑定的菜单，没有则新增，有则删除
                List<BaseData> menuData = inspectMenu(mid, rid);
                if (menuData.size() == 0) {
                    //获取最新的数据下标
                    List<BaseData> allRole = redisTool.getList("system:role_menu:id:" + mid, null, null);
                    int newOrder = 0;
                    if (allRole.size() > 0) {
                        newOrder = allRole.get(allRole.size() - 1).getInter("order_number") + 1;
                    }
                    String role_admin = data.getString("add") + data.getString("del") + data.getString("upd") + data.getString("sel") + data.getString("download") + data.getString("upload") + data.getString("exportData");
                    String role_normal = data.getString("add2") + data.getString("del2") + data.getString("upd2") + data.getString("sel2") + data.getString("download2") + data.getString("upload2") + data.getString("exportData2");
                    //执行新增
                    result = addRoleMenu(mid, rid, newOrder, role_admin, role_normal, data.getString("role_type"));
                } else {
                    //执行删除
                    result = deleteUserRole(menuData.get(0).getString("id"), rid) + "";
                }
                //4.将指定权限缓存重置
                reloadMenuDataSchema.reloadLoginData(rid);
            }
        }
        return result;
    }

    /**
     * 获取当前权限可访问的菜单列表
     *
     * @param executeData {
     *                    rid:           权限id
     *                    checkLogin:    是否为鉴权认证使用 「不为空即可」
     *                    }
     * @return
     */
    public List<BaseData> getCurrentMenu(BaseData executeData) {
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String roleId = data.getString("rid");
        //从redis里获取当前权限的菜单缓存数据
        String check = !data.getString("checkLogin").isEmpty() ? ("'" + roleId + "'") : (" (select pid from sys_role sr where sr.id = '" + roleId + "') ");
        //未获取到缓存数据，请求数据
        String menus = " select rm.*,m.name,m.note,m.pid,m.code,m.icon,m.path,m.component,m.is_parent,m.redirect_page," +
                " m.version,m.state" +
                " from sys_rolemenu rm left join sys_menu m on m.id = rm.mid " +
                " where m.state = 0 and rm.state = 0 " +
                " and rm.rid = " + check + " order by rm.order_number asc ";
        List<BaseData> menuData = baseApi.selectBase(new sqlEngine().setSelect(menus));

        //向上级溯源
        if (menuData == null) {
            data.put("id", roleId);
            BaseData role = roleSchema.getRoleNodeData(pottingData("", data));
            if (role != null) {
                if (!role.getString("pid").equals("-1")) {
                    BaseData parent_role = new BaseData();
                    parent_role.put("rid", role.getString("pid"));
                    menuData = getCurrentMenu(pottingData("", parent_role));
                }
            }
        }
        //如果为空则去获取上级
        return menuData == null ? new ArrayList<>() : menuData;
    }


    /**
     * 获取当前权限可访问的菜单节点
     *
     * @param executeData {
     *                    rid: 权限id
     *                    getName: 用于判断获取id还是name集合
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public List<String> getMenuChecked(BaseData executeData) {
        List<String> result = new ArrayList<>();
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String roleId = data.getString("rid");
        if (!roleId.isEmpty()) {
            sqlEngine st = new sqlEngine().execute("sys_rolemenu", "t")
                    .joinBuild("sys_menu", "m", joinType.L)
                    .joinColunm("t", "mid", "id")
                    .joinFin();

            //TODO 参数匹配
            //请注意,若使用此类型进行数据操作时，请严格按照sqlEngine传参格式在key值前加入固定参数标识@，如@key
            st.queryBuild(queryType.and, "t", "@rid", conditionType.EQ, groupType.DEF, roleId)
                    .queryBuild(queryType.and, "m", "@pid", conditionType.NEQ, groupType.DEF, "-1")
                    .appointColumn("t", groupType.DEF, "mid")
                    .appointColumn("m", groupType.DEF, "code").selectFin("");

            List<BaseData> list = baseApi.selectBase(st);
            for (BaseData l : list) {
                result.add(data.getString("getName").isEmpty() ? l.getString("mid") : l.getString("code"));
            }
        }
        return result;
    }

    /**
     * 获取当前权限可访问的数据节点
     *
     * @param executeData {
     *                    rid: 权限id
     *                    mid: 菜单id
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public BaseData getCurrentRoleMenuNode(BaseData executeData) {
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        sqlEngine sel = new sqlEngine()
                .execute("sys_rolemenu", "s")
                .queryBuild(queryType.and, "s", "@rid", conditionType.EQ, groupType.DEF, data.getString("rid"))
                .queryBuild(queryType.and, "s", "@mid", conditionType.EQ, groupType.DEF, data.getString("mid"))
                .selectFin("");
        List<BaseData> node = baseApi.selectBase(sel);
        if (node.size() > 0){
            return node.get(0);
        } else {
            BaseData nData = new BaseData();
            nData.put("page_role","1111111");
            nData.put("page_normal_role","0001000");
            return nData;
        }
    }

    /**
     * 获取当前权限可访问的菜单树
     *
     * @param executeData {
     *                    rid: 权限id
     *                    }
     * @return
     */
    @Access({AccessType.SEL})
    public List<BaseData> getCurrentRoleMenuTree(BaseData executeData) {
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));
        String roleId = data.getString("rid");
        if (!roleId.isEmpty()) {
            //1.获取当前权限下的菜单缓存
            List<BaseData> currentRole = getCurrentMenu(executeData);
            //2.获取可访问的菜单树

            List<BaseData> list = new ArrayList<>();

            BaseData tree = new BaseData();
            tree.put("id", -1);

            tree.put("title", data.get("systemName"));
            tree.put("expand", true);
            tree.put("is_lock", false);
            tree.put("version", 0);
            tree.put("children", menuSchema.getMenuNode("-1", currentRole.size() > 0 ? currentRole : null));

            list.add(tree);

            return list;
        } else {
            //默认是全部菜单权限
            //当前为溯源失败「既全部父级节点均未绑定菜单」
            return menuSchema.getRootMenuTree(executeData);
        }
    }


    /**
     * 内部方法 : 菜单权限检测
     *
     * @param menuId 菜单编号
     * @param roleId 指定的权限编号
     * @return
     */
    private List<BaseData> inspectMenu(String menuId, String roleId) {
        sqlEngine st = new sqlEngine().execute("sys_rolemenu", "t")
                .queryBuild(queryType.and, "t", "@rid", conditionType.EQ, groupType.DEF, roleId)
                .queryBuild(queryType.and, "t", "@mid", conditionType.EQ, groupType.DEF, menuId)
                .selectFin("");
        //TODO 检测当前权限是否已存在
        return baseApi.selectBase(st);
    }

    /**
     * 内部方法：新增菜单授权
     *
     * @param menuId       菜单编号
     * @param roleId       指定的权限编号
     * @param order_number 当前授权序列号
     * @return
     */
    @Transactional(readOnly = false)
    protected String addRoleMenu(String menuId, String roleId, int order_number,String role_admin,String role_normal,String role_type) {
        BaseData executeData = new BaseData();
        executeData.put("mid", menuId);
        executeData.put("rid", roleId);
        executeData.put("page_role", role_admin);
        executeData.put("page_normal_role", role_normal);
        executeData.put("role_type", Integer.parseInt(role_type));
        executeData.put("order_number", order_number);
        String result = insertData(pottingData("sys_rolemenu", executeData));
        if (result.length() == 32) {
            //新增成功后,将redis缓存重置
            redisTool.delete("system:role_menu:id:" + roleId);
        }
        return result;
    }


    /**
     * 内部方法：移除菜单授权
     *
     * @param um_id  授权信息id
     * @param roleId 权限id
     * @return
     */
    @Transactional(readOnly = false)
    protected int deleteUserRole(String um_id, String roleId) {
        String delete = "delete from sys_rolemenu where id = '" + um_id + "'";
        int result = baseApi.execute(new sqlEngine().setExecute(delete));
        if (result > 0) {
            //删除成功后,将redis缓存重置
            redisTool.delete("system:role_menu:id:" + roleId);
        }
        return result;
    }

}
