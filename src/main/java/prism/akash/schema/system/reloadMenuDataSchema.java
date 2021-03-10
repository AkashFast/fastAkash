package prism.akash.schema.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prism.akash.container.BaseData;
import prism.akash.schema.BaseSchema;
import prism.akash.schema.login.accessLoginSchema;
import prism.akash.tools.annocation.Schema;

import java.util.List;


/**
 * 系统·重新加载权限数据
 * TODO : 系统·核心逻辑 （仅对Schema层有效，不对外部开放）
 *
 * @author HaoNan Yan
 */
@Service
@Transactional(readOnly = true)
@Schema(code = "reloadMenuData", name = "系统·权限重载" ,init = false)
public class reloadMenuDataSchema extends BaseSchema {

    @Autowired
    accessLoginSchema accessLoginSchema;

    @Autowired
    roleMenuSchema roleMenuSchema;

    /**
     * 权限重载
     *
     * @param roleId  权限ID
     * @return 重载结果 true/false
     */
    public boolean reloadLoginData(String roleId) {
        //1.清除缓存
        redisTool.delete("login:role_data:id:" + roleId);
        //2.重载缓存
        BaseData reload = new BaseData();
        reload.put("rid", roleId);
        reload.put("checkLogin","1");
        //3.根据权限id获取菜单信息
        List<BaseData> menuList = roleMenuSchema.getCurrentMenu(pottingData("", reload));
        return accessLoginSchema.getLoginAccess(menuList,roleId).size() > 0;
    }
}
