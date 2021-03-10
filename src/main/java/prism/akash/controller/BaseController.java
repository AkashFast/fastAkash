package prism.akash.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import prism.akash.container.BaseData;
import prism.akash.controller.proxy.BaseProxy;
import prism.akash.tools.StringKit;
import prism.akash.tools.oauth.AccessTool;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用接口
 * TODO : 系统·通用接口
 *
 * @author HaoNan Yan
 */
@RestController
public class BaseController extends BaseProxy implements Serializable {

    @Autowired
    AccessTool accessTool;

    /**
     * 系统业务 ·  统一入口
     *
     *
     * @param schemaName    需要调用的业务逻辑名称，默认使用base
     * @param methodName    需要请求的方法名称
     * @param id            数据表id /  sql数据引擎id    TODO  ※ schemaName非base时，允许为空
     * @param data          封装参数  方法所需参数集合    TODO  ※ 允许为空，为空时建议传入 -> {}
     *                      {
     *                         *id  :
     *                         *executeData :
     *                         {
     *                             paramA :   ……参数A
     *                             paramB :   ……参数B
     *                                      ……
     *                         }
     *                      }
     * TODO   基础逻辑方法 「base」 :
     *                      1.  select          查询数据                      TODO  ※ 需要使用sql引擎id
     *                      2.  selectPage      查询数据「含分页」             TODO  ※ 需要使用sql引擎id
     *                      3.  selectByOne     根据数据id查询指定数据信息
     *                      4.  insertData      新增数据
     *                      5.  updateData      更新数据
     *                      6.  deleteDataSoft  数据软删除
     *                      7.  deleteData      数据暴力删除
     *                      TODO  3~7 demo示例可在AkashApplicationTests查看
     *
     * @return 请求结果
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/executeUnify",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String executeUnify(
            @RequestParam(value = "schemaName", required = false, defaultValue = "base") String schemaName,
            @RequestParam(value = "methodName") String methodName,
            @RequestParam(value = "id", required = false, defaultValue = "") String id,
            @RequestParam(value = "data", required = false, defaultValue = "{}") String data) {
        if (accessTool.accessParamCheck(schemaName, methodName, id)) {
            //⚠ 检测到注入攻击
            Map<String, Object> result = new ConcurrentHashMap<>();
            result.put("result", "0");
            result.put("resultData", "⚠ 操作失败,请联系管理员");
            return JSON.toJSONString(result);
        } else {
            //数据正常,放行
            BaseData execute = StringKit.parseBaseData(data);
            return StringKit.formatSchemaData(invokeMethod(schemaName, methodName, id, execute));
        }
    }

    /**
     * 系统业务·灵活导出
     *
     * @param schemaName
     * @param methodName
     * @param id
     * @param data
     * @return
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/exportFile",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public ModelAndView exportFile(
            @RequestParam(value = "schemaName", required = false, defaultValue = "base") String schemaName,
            @RequestParam(value = "methodName") String methodName,
            @RequestParam(value = "id", required = false, defaultValue = "") String id,
            @RequestParam(value = "data", required = false, defaultValue = "{}") String data) {
        if (accessTool.accessParamCheck(schemaName, methodName, id)) {
            return null;
        } else {
            //数据正常,放行
            BaseData execute = StringKit.parseBaseData(data);
            return (ModelAndView) invokeMethod(schemaName, methodName, id, execute);
        }
    }
}
