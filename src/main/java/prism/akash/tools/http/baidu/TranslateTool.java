package prism.akash.tools.http.baidu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import prism.akash.tools.StringKit;
import prism.akash.tools.http.base.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用翻译组件
 */
@Component
public class TranslateTool {

    @Value("${akashConfig.baidu.appid}")
    public String appid;

    @Value("${akashConfig.baidu.secret}")
    public String secret;

    @Autowired
    HttpRequest httpRequest;

    /**
     * 执行获取sign签名
     *
     * @param q
     * @param salt
     * @return
     */
    private String sign(String q, String salt) {
        String sign = appid + q + salt + secret;
        return DigestUtils.md5DigestAsHex(sign.getBytes());
    }

    /**
     * 执行获取翻译结果
     *
     * @param q
     * @return
     */
    public String translate(String q) {
        String salt = StringKit.getUUID();
        Map<String, String> map = new HashMap<>();
        map.put("q", q);
        map.put("from", "zh");
        map.put("to", "en");
        map.put("appid", appid);
        map.put("salt", salt);
        map.put("sign", sign(q, salt));
        String result = httpRequest.sendGet("http://fanyi-api.baidu.com/api/trans/vip/translate", map, new HashMap<>());
        JSONObject json = JSONObject.parseObject(result);
        if (json.get("error_code") != null){
            //错误
            return "";
        } else{
            JSONArray ja = JSONArray.parseArray(json.getString("trans_result"));
            JSONObject jo = ja.getJSONObject(0);
            return jo.getString("dst").trim();
        }
    }
}
