package prism.akash.tools.oauth;


import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuthToken
 * TODO : 会话管理工具类  主要应用于LoginController
 *
 * @author HaoNan Yan
 */
public class OAuthToken {

    //TODO : SSL加密链接访问控制
    @Value("${akashConfig.oauth.secureSSL}")
    private static boolean secureSSL;

    //TODO : 是否启用跨域
    @Value("${akashConfig.oauth.cross.enable}")
    private static boolean cross;

    //TODO : 指定跨域域名
    @Value("${akashConfig.oauth.cross.domain}")
    private static String domain;


    //TODO : 指定跨域域名
    @Value("${akashConfig.oauth.cross.httpOnly}")
    private static boolean httpOnly;

    public HttpServletRequest request;
    public HttpServletResponse response;

    /**
     * 初始化OAuthToken
     *
     * @param req 客户端请求
     * @param rep 服务端响应
     */
    public OAuthToken(HttpServletRequest req, HttpServletResponse rep) {
        this.request = req;
        this.response = rep;
    }

    /**
     * 获取指定Cookie值
     *
     * @param cookieName
     * @return
     */
    public String getCookie(String cookieName) {
        Map<String, Cookie> cookieMap = getCookieMap();
        if (cookieMap.containsKey(cookieName)) {
            Cookie cookie = cookieMap.get(cookieName);
            return cookie.getValue();
        } else {
            return "";
        }
    }

    /**
     * 获取指定Cookie值的剩余有效时间
     *
     * @param cookieName
     * @return
     */
    public int getMaxAge(String cookieName) {
        Map<String, Cookie> cookieMap = getCookieMap();
        if (cookieMap.containsKey(cookieName)) {
            Cookie cookie = cookieMap.get(cookieName);
            return cookie.getMaxAge();
        } else {
            return 0;
        }
    }


    /**
     * 设置指定Cookie值
     *
     * @param accessToken
     * @param value
     * @param maxAge
     */
    public void setCookie(String accessToken, String value, Integer maxAge) {
        _setCookie(accessToken, value, maxAge);
    }


    private void _setCookie(String accessToken, String value, Integer maxAge) {
        if (response == null)
            return;
        Cookie cookie = new Cookie(accessToken, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        if (cross && !domain.isEmpty()) {
            cookie.setDomain("." + domain);
        }
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secureSSL);
        if (response != null)
            response.addCookie(cookie);
    }

    private Map<String, Cookie> getCookieMap() {
        Map<String, Cookie> cookieMap = new ConcurrentHashMap<>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

}
