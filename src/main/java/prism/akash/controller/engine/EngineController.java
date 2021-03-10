package prism.akash.controller.engine;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import prism.akash.container.converter.sqlConverter;

import java.io.Serializable;

/**
 * 数据引擎相关接口
 * TODO : 系统·数据引擎
 *
 * @author HaoNan Yan
 */
public class EngineController implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(EngineController.class);

    @Autowired
    sqlConverter sqlConverter;

    /**
     * 创建数据检索引擎
     * TODO 当前版本仅支持数据查询
     *
     * @param s  executeData  引擎流程数据
     * @param n  name         引擎名称
     * @param c  code         引擎Code（唯一码）
     * @param ne note         引擎备注信息
     * @return
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @ResponseBody
    @RequestMapping(value = "/engineCreate",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public String engineCreate(@Param("s") String s,
                                @Param("n") String n,
                                @Param("c") String c,
                                @Param("ne") String ne) {
        return JSON.toJSONString(sqlConverter.createBuild(n, c, ne, s));
    }
}
