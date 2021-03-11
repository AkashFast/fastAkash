package prism.akash.controller.login;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prism.akash.container.BaseData;
import prism.akash.controller.proxy.BaseProxy;
import prism.akash.schema.system.roleSchema;
import prism.akash.schema.system.userSchema;
import prism.akash.tools.StringKit;
import prism.akash.tools.http.base.HttpRequest;
import prism.akash.tools.http.base.SessionUtil;
import prism.akash.tools.oauth.OAuthToken;
import prism.akash.tools.properties.log.LogConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 登录集成类
 * TODO : 系统·统一登录
 *
 * @author HaoNan Yan
 */
@RestController
public class E2LoginController implements Serializable {

    @Autowired
    HttpRequest httpRequest;

    @Autowired
    userSchema userSchema;

    @Autowired
    roleSchema roleSchema;

    @Autowired
    LogConfig logConfig;

    //TODO : 登录成功后的待跳转「中转」根目录
    @Value("${coreConfig.loginSuccessUrl}")
    public String loginSuccessUrl;

    private final Logger logger = LoggerFactory.getLogger(E2LoginController.class);

    /**
     * 用户登陆
     *
     * @param request
     * @param response
     * @param email
     */
    @CrossOrigin(origins = "*", maxAge = 36000)
    @RequestMapping(value = "/login")
    public Boolean login(HttpServletRequest request, HttpServletResponse response, String email) {
        Boolean login = false;
        OAuthToken oAuthToken = new OAuthToken(request, response);
        BaseData data = new BaseData();
        data.put("email",email);

        //获取统一登录的返回值「email」作为登录标识
        BaseData execute = new BaseData();
        execute.put("executeData", JSON.toJSONString(data));
        execute.put("id", "");
        //获取符合条件的用户信息
        BaseData user = userSchema.selectUserLogin(execute);
        // TODO △ 这里请根据实际业务场景进行修改
        String uri = "/#/checkRole";
        // TODO △ 这里请根据实际业务场景进行修改
        if (user != null) {
            // 授权成功
            sessionToken(oAuthToken, request, user);
            login = true;
        }
        return login;
    }

    /**
     * 指定用户登陆权限
     * @param rid       权限id
     * @param request
     */
    @CrossOrigin(origins = "*", maxAge = 36000)
    @RequestMapping(value = "/checkRole")
    public void checkRole(String rid,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        OAuthToken oAuthToken = new OAuthToken(request, response);
        String tokenKey = oAuthToken.getCookie("mt");
        // △ 将权限数据写入Session
        if (!tokenKey.isEmpty() && !rid.isEmpty()) {
            Object obj = request.getSession().getAttribute(tokenKey);
            if (obj != null) {
                BaseData user = (BaseData) obj;
                BaseData data = new BaseData();
                data.put("id", rid);
                data.put("uid",user.getString("id"));
                data.put("rid",rid);

                BaseData execute = new BaseData();
                execute.put("id","");
                execute.put("executeData", JSON.toJSONString(data));
                BaseData role = roleSchema.getRoleNodeData(execute);

                user.put("rid",rid.equals("checkRole") ? "" : role.getString("id"));
                user.put("pid",rid.equals("checkRole") ? "" : role.getString("pid"));
                //判断当前用户是否为超管
                user.put("is_supervisor", rid.equals("checkRole") ? 0 : role.getInter("is_supervisor"));
                user.put("uid", user.getString("id"));
                String mtp = "-1";
                if(!rid.equals("checkRole")) {
                    mtp = roleSchema.getRoleUserData(execute) + "";
                    // 获取当前用户当前角色是否为管理员
                    user.put("is_admin", rid.equals("checkRole") ? 0 : Integer.parseInt(mtp));
                    oAuthToken.setCookie("mt-p", mtp + "", -1);
                }else{
                    oAuthToken.setCookie("mt-p", "-1", 1);
                }
                // 用户类型
                user.put("mtp", mtp);
                request.getSession().setAttribute(tokenKey, user);
            }
        }
    }

    // 对当前会话Token进行管理
    private void sessionToken(OAuthToken oAuthToken,
                              HttpServletRequest request,
                              BaseData user) {
        //TODO 更新用户数据 载入数据日志
        BaseData log = new BaseData();
        log.put("system_current_user", user.getString("id"));
        log.put("system_current_ip", StringKit.getRealIP(request));
        logConfig.record_log("0", "Login", 1, "", log);
        // Session会话数据保存
        SessionUtil.setUser(request, user);
        // 获取TOKEN令牌
        String token = StringKit.getUUID();
        oAuthToken.setCookie("mt", token, -1);
        try {
            oAuthToken.setCookie("mt-uname", URLEncoder.encode(user.getString("name"), "UTF-8"), -1);
        } catch (UnsupportedEncodingException e) {
           logger.error("sessionToken:cookie + 中文转码错误");
        }
        // 将令牌打入以用户id为主键的Session
        request.getSession().setAttribute(token, user);
    }

    /**
     * 用户退出
     *
     * @param request
     * @param response
     */
    @CrossOrigin(origins = "*", maxAge = 36000)
    @RequestMapping(value = "/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 清空当前用户数据
        OAuthToken oAuthToken = new OAuthToken(request, null);
        request.getSession().setAttribute(oAuthToken.getCookie("mt"), null);
    }
}
