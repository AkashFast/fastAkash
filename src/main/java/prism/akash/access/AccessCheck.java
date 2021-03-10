package prism.akash.access;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import prism.akash.container.BaseData;
import prism.akash.schema.system.roleSchema;
import prism.akash.tools.StringKit;
import prism.akash.tools.oauth.OAuthToken;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AccessCheck {

    //TODO : 获取系统默认的强制鉴权设置
    @Value("${akashConfig.access.enable}")
    public boolean accessEnable;

    @Autowired
    roleSchema roleSchema;

    @Pointcut("execution(public * prism.akash.controller.BaseController.*(..))")
    public void recordController() {
    }

    @Pointcut("execution(public * prism.akash.controller.file.FileController.*(..))")
    public void fileController() {
    }

    @Around("recordController() || fileController()")
    public Object loginProcess(ProceedingJoinPoint point) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        OAuthToken oAuthToken = new OAuthToken(request, null);
        String tokenKey = oAuthToken.getCookie("mt");

        Object[] args = point.getArgs();
        String pointName = point.getSignature().getName();

        // TODO 判断当前方法是否为文件上传
        // upFile data下标为2   executeUnify data下标为3
        int index = pointName.equals("upLoad") || pointName.equals("getFile") ? 2 : 3;
        //获取指定data
        JSONObject paramData = JSON.parseObject(args[index].toString());
        if (!tokenKey.isEmpty() || !accessEnable) {
            Object obj = request.getSession().getAttribute(tokenKey);
            // TODO 2020/11/24 新增非强制鉴权「测试」模式下默认使用超级管理员身份进行请求访问
            if (obj != null || !accessEnable) {
                BaseData user = (BaseData) obj;
                //将用户id写入对象
                paramData.put("system_current_user", accessEnable ? user.getString("id") : "c052d6525a444cada1a0809c7f5f9a57");
                //获取用户的真实IP地址
                paramData.put("system_current_ip", StringKit.getRealIP(request));
                //当前登陆角色是否为超管
                paramData.put("system_current_supervisor", accessEnable ? user.getInter("is_supervisor") : 1);
                //当前登陆角色是否为管理员
                paramData.put("system_current_admin", accessEnable ? user.getInter("is_admin") : 1);
                //将当前登入的权限信息写入
                paramData.put("system_current_role", accessEnable ? user.getString("rid") : "074e19ca81b944629773fff101ea759e");
                //区分管理者及普通用户
                paramData.put("system_current_role_type", accessEnable ? (user.getString("mtp").isEmpty() ? "-1" : user.getString("mtp")) : "1");
                paramData.put("system_current_role_pid", accessEnable ? user.getString("pid") : "-1");
                args[index] = JSON.toJSONString(paramData);
            }
        } else {
            //获取用户的真实IP地址
            paramData.put("system_current_ip", StringKit.getRealIP(request));
        }
        args[index] = JSON.toJSONString(paramData);
        return  point.proceed(args);
    }
}
