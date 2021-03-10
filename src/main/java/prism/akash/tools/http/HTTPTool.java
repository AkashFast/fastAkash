package prism.akash.tools.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import prism.akash.tools.http.base.HttpRequest;
import prism.akash.tools.http.mac.ApiMac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTPTool
 * TODO : 系统 · HTTP请求统一入口
 *
 * @author HaoNan Yan
 */
@Component
public class HTTPTool {

    @Autowired
    HttpRequest httpRequest;

    //TODO : 获取appId
    @Value("${coreConfig.appId}")
    private String appId;

    //TODO : 获取appSecret
    @Value("${coreConfig.appSecret}")
    private String appSecret;

    //TODO : 获取请求地址
    @Value("${coreConfig.host}")
    private String host;

    /**
     * 生成验签
     *
     * @param data 数据
     * @return 验签
     */
    private String verifySign(String data, String ztAppSecret) {
        ApiMac apiMac = new ApiMac(ztAppSecret);
        return apiMac.sign(data);
    }


    /**
     * 构建查询条件字符串
     *
     * @param param 查询条件
     * @return 查询条件字符串
     */
    private String getQuery(Map<String, String> param) {
        List<String> aryParam = new ArrayList<>(param.size());
        for (Map.Entry<String, String> entry : param.entrySet()) {
            aryParam.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
        }
        return String.join("&", aryParam);
    }

    /**
     * 创建外部系统的访问请求
     *
     * @param uri      接口地址
     * @param paramMap 接口参数
     * @return
     */
    public String sendGet(String uri, Map<String, String> paramMap) {
        String sign;
        if (paramMap == null || paramMap.size() == 0) {
            sign = verifySign(uri, appSecret);
        } else {
            String queryStr = getQuery(paramMap);
            sign = verifySign(uri + "?" + queryStr, appSecret);
        }
        // Header内容
        Map<String, String> header = new HashMap<>();
        header.put("appId", appId);
        header.put("sign", sign);
        return httpRequest.sendGet(host + uri, paramMap, header);
    }

}
