package prism.akash.controller.proxy;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import prism.akash.container.BaseData;
import prism.akash.schema.file.fileSchema;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.checked.AccessType;
import prism.akash.tools.cache.CacheClass;
import prism.akash.tools.context.SpringContextUtil;
import prism.akash.tools.properties.log.LogConfig;
import prism.akash.tools.reids.RedisTool;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 系统核心及拓展逻辑Schema代理类
 * TODO : 系统·核心逻辑 / 拓展业务逻辑代理
 *
 * @author HaoNan Yan
 */
@Component
public class BaseProxy implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(BaseProxy.class);

    //TODO : 获取系统默认的强制鉴权设置
    @Value("${akashConfig.access.enable}")
    public boolean accessEnable;

    @Autowired
    RedisTool redisTool;

    @Autowired
    fileSchema fileSchema;

    @Autowired
    LogConfig logConfig;

    /**
     * 执行方法「BaseController」
     *
     * @param schemaName  需要反射代理的Schema（系统固有逻辑）对象
     * @param methodName  需要代理执行调用的方法名称
     * @param id          数据表id /  sql数据引擎id
     * @param executeData 代理入参数据对象
     *                    {
     *                    *session_id: 当前用户可用授权 TODO Access鉴权类会根据当前会话自动补全本数据
     *                    }
     * @return
     */
    public Object invokeMethod(String schemaName, String methodName, String id, BaseData executeData) {
        //通过反射代理的class对象
        Class clazz;
        //执行invoke后返回的数据对象
        Object reObject = null;
        //需要invoke执行调用的方法
        Method m1;
        //通过反射的schemaClassObject
        Object obj = proxySchema(schemaName);
        clazz = obj.getClass();
        BaseData reBase = new BaseData();
        try {
            //执行方法
            m1 = clazz.getDeclaredMethod(methodName, BaseData.class);
            //TODO 对请求进行鉴权
            reBase = checkAccess(schemaName, id, getMethodPermission(schemaName, methodName, m1),
                    executeData.getString("system_current_role_type"),
                    executeData.getString("system_current_role"));
            //如果accessEnable为true则执行鉴权，为false时则默认跳过鉴权步骤
            boolean accessResult = accessEnable ? (boolean) reBase.get("result") : true;
            //只有鉴权通过才允许执行访问
            //TODO 请根据需求
            if (accessResult || (reBase.getString("type").equals("ADMIN") && executeData.getInter("system_current_supervisor") == 1)) {
                //封装执行参数
                BaseData execute = new BaseData();
                //TODO 使用sql引擎时，不需要对ID进行JSON化处理
                boolean isSqlEngine = schemaName.equals("base") && (methodName.equals("select") || methodName.equals("selectPage"));
                execute.put("id", isSqlEngine ? id : JSON.toJSONString(id));
                execute.put("executeData", JSON.toJSONString(rolePatchDataAccess(schemaName, methodName, id, executeData, executeData.getString("system_current_role"))));
                //执行操作
                reObject = m1.invoke(obj, execute);
                //追加日志
                logConfig.add_log(reBase, schemaName, methodName, id, executeData);
            } else {
                //返回无权限标识
                reObject = "NAC";
                //追加无权限日志
                logConfig.add_log(reBase, schemaName, methodName, id, executeData);
            }
        } catch (NoSuchMethodException e) {
            logConfig.record_log("1", schemaName + " - " + methodName + " - " + id, 0, "⚠ 当前逻辑类中不存在此方法", executeData);
        } catch (IllegalAccessException e) {
            logConfig.record_log("1", schemaName + " - " + methodName, 3, "⚠ 越权访问", executeData);
        } catch (InvocationTargetException e) {
            logConfig.record_log("1", schemaName + " - " + methodName, 0, "⚠ 方法内部错误：" + e.getMessage() + " / " + e.getCause().getMessage(), executeData);
        }
        return reBase.getString("").equals("EXPORT") ? (ModelAndView) reObject : reObject;
    }

    /**
     * 执行方法「上传文件」
     *
     * @param file        文件地址
     * @param executeData 用户状态信息
     * @return
     */
    public String invokeUpLoad(MultipartFile[] file, BaseData executeData) {
        String reObject = null;
        //通过反射代理的class对象
        Class clazz = fileSchema.getClass();
        try {
//            BaseData reBase = checkAccess("file", null, getMethodPermission("file",
//                    "upLoadFile",
//                    clazz.getDeclaredMethod("upLoadFile", MultipartFile[].class)),
//                    executeData.getString("system_current_role_type"),
//                    executeData.getString("system_current_role"));
            //如果accessEnable为true则执行鉴权，为false时则默认跳过鉴权步骤
//            boolean accessResult = accessEnable ? (boolean) reBase.get("result") : true;
            boolean accessResult = true;
            //只有鉴权通过才允许执行访问
            //TODO 2020 11 30 追加条件：当鉴权type为admin并且用户超管属性为1时可访问
//            if (accessResult) {
                reObject = fileSchema.upLoadFile(file);
//                //追加日志
//                executeData.put("fileName", reObject);
//                logConfig.add_log(reBase, "file", "upLoadFile", "", executeData);
//            } else {
//                //返回无权限标识
//                reObject = "NAC";
//                //追加无权限日志
//                logConfig.add_log(reBase, "file", "upLoadFile", "", executeData);
//            }
        } catch (Exception e) {
            logConfig.record_log("1", "file - upLoadFile", 0, "⚠ 方法内部错误：" + e.getMessage() + " / " + (e.getCause() != null ? e.getCause().getMessage() : ""), executeData);
        }
        return reObject;
    }


    /**
     * 执行方法「下载文件（非数据导出）」
     *
     * @param id          文件系统存储id
     * @param executeData 用户状态信息
     * @return
     */
    public void invokeDownLoad(HttpServletResponse response, String id, BaseData executeData) {
        //通过反射代理的class对象
        Class clazz = fileSchema.getClass();
        try {
//            BaseData reBase = checkAccess("file", null, getMethodPermission("file",
//                    "getFile",
//                    clazz.getMethod("getFile", HttpServletResponse.class, String.class)),
//                    executeData.getString("system_current_role_type"),
//                    executeData.getString("system_current_role"));
            //如果accessEnable为true则执行鉴权，为false时则默认跳过鉴权步骤
//            boolean accessResult = accessEnable ? (boolean) reBase.get("result") : true;
            boolean accessResult = true;
            //⚠ 只有鉴权通过才允许执行访问
//            //TODO 请根据需求
//            if (accessResult) {
                fileSchema.getFile(response, id);
//                //追加日志
//                logConfig.add_log(reBase, "file", "getFile", "", executeData);
//            } else {
//                //追加无权限日志
//                logConfig.add_log(reBase, "file", "getFile", "", executeData);
//            }
        } catch (Exception e) {
            logConfig.record_log("1", "file - getFile", 0, "⚠ 方法内部错误：" + e.getMessage() + " / " + (e.getCause() != null ? e.getCause().getMessage() : ""), executeData);
        }
    }

    /**
     * 自定义数据匹配
     *
     * @param schemaName
     * @param methodName
     * @param id
     * @param executeData
     * @param rid
     * @return
     */
    private BaseData rolePatchDataAccess(String schemaName,
                                         String methodName,
                                         String id,
                                         BaseData executeData,
                                         String rid) {
        List<BaseData> patchData = redisTool.getList("system:role:patch:" + rid, null, null);
        if (patchData.size() > 0) {
            for (BaseData pat : patchData) {
                if (pat.getString("schema_name").equals(schemaName)
                        && pat.getString("method_name").equals(methodName)
                        && pat.getString("table_id").equals(id)) {
                    // TODO 完成匹配后,将参数进行替换重置
                    executeData.put(pat.getString("patch_filed"), pat.getString("repeat_val"));
                }
            }
        }
        return executeData;
    }

    /**
     * 内部方法：执行鉴权操作
     *
     * @param schemaName       需要反射代理的Schema（系统固有逻辑）对象
     * @param id               数据表id /  sql数据引擎id
     * @param methodPermission 通过getMethodPermission()获取的指定方法可用权限集合
     * @param rType            通过AccessCheck设置的session_uType
     * @param rid              通过AccessCheck设置的session_rid
     * @return 成功 / 失败
     */
    private BaseData checkAccess(String schemaName,
                                 String id,
                                 List<BaseData> methodPermission,
                                 String rType,
                                 String rid) {
        BaseData reData = new BaseData();
        //设置默认的返回值
        boolean result = false;
        String type = "";
        if (methodPermission.size() > 0) {
            result = methodPermission.stream().filter(m -> m.getString("accessType").equals("LOGIN") || m.getString("accessType").equals("H5")).collect(Collectors.toList()).size() > 0;
            //判断当前方法是否为登陆相关方法，如果是，则不再执行后续鉴权操作
            if (!result) {
                //获取当前登录权限可访问数据列表
                List<BaseData> roleData = redisTool.getList("login:role_data:id:" + rid, null, null);
                //1.判断当前使用的是否为基础逻辑（base）
                if (!schemaName.equals("base")) {
                    roleData = roleData.stream().filter(r -> r.getString("code").equals("sc_" + schemaName)).collect(Collectors.toList());
                } else {
                    //判断当前表id是否已授权
                    roleData = roleData.stream().filter(r -> r.getString("tid").equals(id)).collect(Collectors.toList());
                }
                if (roleData.size() > 0) {
                    //细分授权验证
                    for (BaseData role : roleData) {
                        if (role.get("page_normal_role") != null && role.get("page_role") != null) {
                            //根据用户类型区分授权（1为管理员角色）, 为-1时则不具备任何权限
                            String pageRole = rType.equals("-1") ? "000000" : role.getString(rType.equals("1") ? "page_role" : "page_normal_role");
                            if (!pageRole.isEmpty()) {
                                for (BaseData mp : methodPermission) {
                                    String accessType = mp.getString("accessType");
                                    //判断当前是否已授权
                                    switch (accessType) {
                                        case "SEL":
                                            result = authorizationResults(pageRole, 3);
                                            if (result) {
                                                type = "SEL";
                                                break;
                                            }
                                        case "ADD":
                                            result = authorizationResults(pageRole, 0);
                                            if (result) {
                                                type = "ADD";
                                                break;
                                            }
                                        case "DEL":
                                            result = authorizationResults(pageRole, 1);
                                            if (result) {
                                                type = "DEL";
                                                break;
                                            }
                                        case "UPD":
                                            result = authorizationResults(pageRole, 2);
                                            if (result) {
                                                type = "UPD";
                                                break;
                                            }
                                        case "DOWN":
                                            result = authorizationResults(pageRole, 4);
                                            if (result) {
                                                type = "DOWN";
                                                break;
                                            }
                                        case "UPLOAD":
                                            result = authorizationResults(pageRole, 5);
                                            if (result) {
                                                type = "UPLOAD";
                                                break;
                                            }
                                        case "EXPORT":
                                            result = authorizationResults(pageRole, 6);
                                            if (result) {
                                                type = "EXPORT";
                                                break;
                                            }
                                        case "ADMIN":
                                            type = "ADMIN";
                                            break;
                                        default:
                                            break;
                                    }
                                    if (result) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        reData.put("type", type);
        reData.put("result", result);
        return reData;
    }

    /**
     * 内部方法：用于获取授权结果
     *
     * @param pageRole
     * @param tag
     * @return
     */
    private boolean authorizationResults(String pageRole, int tag) {
        // TODO 避免「数据导出」无授权报错
        return pageRole.length() > 6 ? String.valueOf(pageRole.charAt(tag)).equals("1") : false;
    }

    /**
     * 内部方法：获取指定方法的调用权限
     *
     * @param schemaName 使用逻辑类简称，默认为base
     * @param methodName 调用方法名称
     * @param method     方法对象
     * @return
     */
    private List<BaseData> getMethodPermission(String schemaName, String methodName, Method method) {
        String redisKey = "system:method:access:" + schemaName + ":" + methodName;
        List<BaseData> methodPermission = redisTool.getList(redisKey, null, null);
        if (methodPermission.size() == 0) {
            Access access = AnnotationUtils.findAnnotation(method, Access.class);
            Object accessList = AnnotationUtils.getValue(access, "value");
            AccessType[] accesses = (AccessType[]) accessList;
            if (access != null){
                for (AccessType ac : accesses) {
                    BaseData accessType = new BaseData();
                    accessType.put("accessType", ac.getAccessType());
                    methodPermission.add(accessType);
                }
            }
            //如果没有取到任何@Access值，则视为当前方法不允许通过接口访问
            //以防新增方法无法正常扫描，方法鉴权有效期设置为10分钟
            if (methodPermission.size() == 0) {
                redisTool.set(redisKey, new ArrayList<>(), 600000);
            } else {
                //以防新增方法无法正常扫描，方法鉴权有效期设置为10小时
                redisTool.set(redisKey, methodPermission, (60 * 1000 * 60 * 10));
            }
        }
        return methodPermission;
    }


    /**
     * 获取代理的Schema对象
     *
     * @param schemaName schema名称 ： 如未填写默认使用base
     * @return
     */
    private Object proxySchema(String schemaName) {
        Object sObj = null;
        //如果SchemaName为空,则默认使用baseSchema
        schemaName = schemaName.isEmpty() || schemaName == null ? "base" : schemaName;
        String schema = schemaName + "Schema";
        //从cache中获取Schema对象
        Object schemaObj = CacheClass.getCache("schema:proxy:" + schemaName);
        if (schemaObj != null) {
            //将cache中获取的数据转换成实际存在的schema对象
            sObj = schemaObj;
        } else {
            //若cache中未获取到schema对象则进行初始化
            Class<?> cls = null;
            try {
                cls = Class.forName("prism.akash.schema.BaseSchema");
                sObj = SpringContextUtil.getBean(schema, cls);
                //持久化当前schema对象
                CacheClass.setCache("schema:proxy:" + schemaName, sObj, -1);
            } catch (ClassNotFoundException e) {
                logger.error("BaseConfig:proxySchema:ClassNotFoundException -> " + schema + " is not Found");
            }
        }
        return sObj;
    }

}
