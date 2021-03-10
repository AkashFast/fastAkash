package prism.akash.tools.properties.log;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;
import prism.akash.tools.date.dateParse;
import prism.akash.tools.queues.QueueSender;

import java.util.Date;

/**
 * 日志采集·基本项配置
 *
 * @author HaoNan.Yan
 */
@Component
public class LogConfig {

    //TODO : 获取日志服务使用的队列名称
    @Value("${akashConfig.log_queue}")
    public String log_queue;

    @Autowired
    QueueSender queueSender;

    @Autowired
    dateParse dateParse;

    /**
     * 发起日志记录
     *
     * @param accessData  权限认证证书
     * @param schemaName  逻辑类名称
     * @param methodName  方法名称
     * @param id          指定引擎/表id
     * @param executeData 请求参数对象
     */
    public void add_log(BaseData accessData, String schemaName, String methodName, String id, BaseData executeData) {
        String type = accessData.getString("type");
        record_log(getOperation(type,
                schemaName,
                methodName),
                schemaName + " - " + methodName + " - " + id,
                type.equals("") ? 2 : 1,
                type.equals("") ?
                        !executeData.getString("system_current_ip").isEmpty() ?
                                "授权失效:当前访问权限被上级管理员撤回" :
                                "非法访问 => 安全守卫拦截「会话时效已过期或访问攻击」" : "方法执行成功",
                executeData);
    }

    /**
     * 操作类型标准值转化设置
     *
     * @param accessType
     * @param schemaName
     * @param methodName
     * @return
     */
    private String getOperation(String accessType, String schemaName, String methodName) {
        String result = "NOLOG";
        if (accessType.equals("ADD")) {
            result = "11";
        } else if (accessType.equals("UPD")) {
            result = "22";
        } else if (accessType.equals("DEL")) {
            result = "33";
        } else if (accessType.equals("EXPORT")) {
            result = "3";
        } else if (accessType.equals("UPLOAD")) {
            result = "2";
        } else if (accessType.equals("DOWN")) {
            result = "9";
        } else if (accessType.equals("SEL")) {
            // 如果需要对指定的查询类方法进行记录，请参照API手册规范进行配置↓↓↓
            // if (schemaName.equals("") && methodName.equals("")){ result = "999"; }
            // 对系统全部查询类方法进行记录 ↓↓↓↓
            // result = "999";
        }
        return result;
    }


    /**
     * 新增系统日志信息「队列推送」
     *
     * @param operation   操作类型
     * @param methodName  调用的逻辑类及方法名称
     * @param sourceTag   执行的结果标识
     * @param errData     错误信息
     * @param executeData 用户的登陆及请求信息
     * @return
     */
    public void record_log(String operation, String methodName, int sourceTag, String errData, BaseData executeData) {
        if(!operation.equals("NOLOG")){
            BaseData log = new BaseData();
            log.put("updateTime", dateParse.formatDate("yyyy-MM-dd HH:mm:ss", new Date()));
            log.put("executorId", executeData.getString("system_current_user"));
            log.put("log_ip", executeData.getString("system_current_ip"));
            log.put("sourceTag", sourceTag);
            log.put("sourceData", errData);
            log.put("methodName", methodName);
            log.put("reson", operation);
            log.put("state", 0);
            log.put("exe_data", removeAttachContent(executeData));
            queueSender.send(log_queue, log);
        }
    }

    /**
     * 清除与登陆信息相关的参数值
     *
     * @param executeData
     * @return
     */
    private String removeAttachContent(BaseData executeData) {
        executeData.remove("system_current_user");
        executeData.remove("system_current_ip");
        executeData.remove("system_current_supervisor");
        executeData.remove("system_current_admin");
        executeData.remove("system_current_role");
        executeData.remove("system_current_role_type");
        executeData.remove("system_current_role_pid");
        return JSON.toJSONString(executeData);
    }
}
