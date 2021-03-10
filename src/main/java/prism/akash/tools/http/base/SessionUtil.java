package prism.akash.tools.http.base;

import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;

import javax.servlet.http.HttpServletRequest;

/**
 * SessionUtil
 * TODO : 系统 · Session会话管理
 *
 * @author HaoNan Yan
 */
@Component
public class SessionUtil {

    private static final String CURRENT_USER = "loginUser";

    public static void setUser(HttpServletRequest request, BaseData user) {
        request.getSession().setAttribute(CURRENT_USER, user);
    }

    public static BaseData getUser(HttpServletRequest request) {
        return (BaseData) request.getSession().getAttribute(CURRENT_USER);
    }

}
