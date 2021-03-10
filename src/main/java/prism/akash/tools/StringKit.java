package prism.akash.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.omg.CORBA.DATA_CONVERSION;
import org.springframework.util.DigestUtils;
import prism.akash.container.BaseData;
import prism.akash.tools.properties.responseCode.responseCodeConfig;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统 字符串 / BaseData对象封装处理
 * TODO : 系统·字符串及BaseData对象处理
 *
 * @author HaoNan Yan
 */
public class StringKit {

    //盐，用于混交md5
    private static final String slat = "&%5123***&&%%$$#@";

    /**
     * 生成md5
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        String base = str + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 判断Key或value中是否含有特殊字符
     * TODO ：主要提供给sqlEngine校验使用,仅允许及开放使用下划线_
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ `~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        return isSpecial(regEx, str);
    }

    /**
     * 判断Column字段是否含有非法字符
     * TODO ：主要提供给sqlStepBuild校验使用,仅允许及开放使用:_#,@
     *
     * @param str true为包含，false为不包含
     * @return
     */
    public static boolean isSpecialColumn(String str) {
        String regEx = "[ `~!$%^&*()+=|{}':;'\\[\\].<>/?~！￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        return isSpecial(regEx, str);
    }

    /**
     * 通用代码提取
     *
     * @param regEx
     * @param str
     * @return
     */
    private static boolean isSpecial(String regEx, String str) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * TODO : 生成32位标准UUID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取ip地址
     * @param request
     * @return
     */
    public static String getRealIP(HttpServletRequest request) {
        if(request==null)
            try {
                return  InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                return "127.1.1.1";
            }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /**
     * 将json字符串转换为有序集合LinkedHashMap
     *
     * @param data 待处理的JSON字符串
     * @return
     */
    public static LinkedHashMap<String, Object> parseLinkedMap(String data) {
        return JSONObject.parseObject(data, new TypeReference<LinkedHashMap<String, Object>>() {
        });
    }

    /**
     * 将对象转储为标准数据
     *
     * @param data 待转储的数据对象
     * @return
     */
    public static String formatSchemaData(Object data) {
        String content = String.valueOf(data);
        //TODO 判断当前数据格式是否为基础格式 String int
        boolean existBase = data instanceof String || data instanceof Integer;
        Map<String, Object> result = new HashMap<>();
        result.put("result", !existBase ?
                (!content.equals("null") && !content.equals("{}")) ? "1" :
                        "-8" :
                content.length() == 32 ? "1" : data);
        result.put("resultData", !existBase ?
                (!content.equals("null") && !content.equals("{}"))
                        ? data : "⚠ 操作失败：待操作数据不存在" :
                responseCodeConfig.formatCode(content));
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 将数据转化成BaseData对象
     * TODO Schema层使用
     *
     * @param data
     * @return
     */
    public static BaseData parseBaseData(String data) {
        BaseData execute = new BaseData();
        if (!data.isEmpty() && data != null) {
            execute = JSON.parseObject(data, BaseData.class);
        }
        return execute;
    }

    /**
     * 转换成executeData
     * @param data
     * @return
     */
    public static BaseData toExecuteData(BaseData data){
        BaseData executeData = new BaseData();
        executeData.put("executeData", JSON.toJSONString(data));
        return executeData;
    }

    /**
     * 拓展Schema层入参数据解析
     * TODO Schema层使用
     * TODO 非基础（base）类Schema层使用Controller提供的数据时需要将proxy处理过的数据对象进行定向解析
     *
     * @param data
     * @return
     */
    public static String parseSchemaExecuteData(BaseData data) {
        if (data != null) {
            if (data.get("executeData") != null) {
                data = parseBaseData(data.getString("executeData"));
            } else {
                data = new BaseData();
            }
        } else {
            data = new BaseData();
        }
        return JSON.toJSONString(data);
    }
}
