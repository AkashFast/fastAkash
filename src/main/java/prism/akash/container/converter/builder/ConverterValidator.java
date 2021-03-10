package prism.akash.container.converter.builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import prism.akash.tools.StringKit;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO : 数据逻辑引擎信息有效性
 */
public class ConverterValidator implements Serializable {

    private static final long serialVersionUID = 1L;

    //待验证数据对象
    private String executeListData;

    /**
     * 初始化构造
     */
    public ConverterValidator(String executeData){
        executeListData = executeData;
    }

    /**
     * 启动校验
     * @return
     */
    public String  verification(){
        Map error = null;
        JSONArray coverArray = JSONArray.parseArray(executeListData);
        boolean errs = false;
        for (int i = 0; i < coverArray.size(); i++) {
            error = checkError(coverArray.getJSONObject(i).toJSONString());
            Object err = error.get("err");
            if (err!= null){
                errs = (boolean)err;
                if (errs){
                    break;
                }
            }
        }
        return errs ? error.get("result") + "" : null;
    }

    /**
     * 单项校验器
     * @param data
     * @return
     */
    private Map checkError(String data){
        Map KeyError = null;
        LinkedHashMap<String, Object> params = StringKit.parseLinkedMap(data);
        //TODO : 确认传入字段存在
        for (String key : params.keySet()) {
            if (key.equals("childList")) {
                // TODO : 子查询内循环
                JSONArray ja = JSONArray.parseArray(params.get(key).toString());
                for (int j = 0; j < ja.size(); j++) {
                    JSONObject jo = ja.getJSONObject(j);
                    checkError(jo.toJSONString());
                }
            } else {
                // TODO : 执行递归校验
                KeyError = loopData(params.get("executeTag") + "->" + key,params.get(key).toString());
            }
        }
        return KeyError;
    }

    /**
     * 递归轮询校验
     * @param result    上级节点Key值
     * @param data      执行校验的数据
     * @return
     */
    private Map loopData(String result, String data){
        Map res = new ConcurrentHashMap();
        boolean err = false;
        if(data!=null){
            if (data.contains("{")){
                LinkedHashMap<String, Object> mapData = StringKit.parseLinkedMap(data);
                for (String key : mapData.keySet()) {
                    //TODO : 判断是否存在子级节点
                    String value = mapData.get(key).toString();
                    if(value.contains("{")){
                        loopData(key,value);
                    }else{
                        // TODO : 除指定值外,其余配参值必须通过校验器的验证
                        if(!key.contains("value")){
                            if(StringKit.isSpecialColumn(value)){
                                result =  result + "->" + key;
                                err = true;
                                break;
                            }
                        }
                    }

                }
            }
        }
        res.put("result",result);
        res.put("err",err);
        return res;
    }
}
