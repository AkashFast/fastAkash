package prism.akash.schema.core;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.engineEnum.*;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.schema.BaseSchema;
import prism.akash.tools.StringKit;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.Schema;
import prism.akash.tools.annocation.checked.AccessType;

import java.util.Map;

/**
 * 系统日志相关类
 * -- 本类方法主要提供系统日志的收集
 * TODO : 系统·核心逻辑 （独立）
 *
 * @author HaoNan Yan
 */
@Service
@Transactional(readOnly = true)
@Schema(code = "logger", name = "核心：系统日志")
public class loggerSchema extends BaseSchema {

    /**
     * 新增日志
     *
     * @param executeData 日志信息
     * @return
     */
    @Transactional(readOnly = false)
    public String addLogger(BaseData executeData) {
        //为了保证数据的强一致性，数据表ID将使用getTableIdByCode方法进行指向性获取
        return baseApi.insertData(getTableIdByCode("cr_logger"), JSON.toJSONString(executeData));
    }


    private sqlEngine buildEngine(BaseData executeData) {
        //1.获取并解析关键参数信息
        BaseData data = StringKit.parseBaseData(executeData.getString("executeData"));

        sqlEngine st = new sqlEngine().execute("cr_logger", "t")
                .joinBuild("sys_user","u", joinType.L)
                .joinColunm("u","id","t","executorId")
                .joinFin()
                .appointColumn("t",groupType.DEF,"id,log_ip,reson,sourceTag,sourceData,methodName,updateTime,exe_data")
                .appointColumn("u",groupType.DEF,"name,email");

        //TODO 参数匹配
        //请注意,若使用此类型进行数据操作时，请严格按照sqlEngine传参格式在key值前加入固定参数标识@，如@key

        Integer state = data.getInter("state");
        if (state != -1) {
            st.queryBuild(queryType.and, "t", "@state", conditionType.EQ, groupType.DEF, state + "");
        }

        Integer reson = data.getInter("reson");
        if (reson != -1) {
            st.queryBuild(queryType.and, "t", "@reson", conditionType.EQ, groupType.DEF, reson + "");
        }

        Integer sourceTag = data.getInter("sourceTag");
        if (sourceTag != -1) {
            st.queryBuild(queryType.and, "t", "@sourceTag", conditionType.EQ, groupType.DEF, sourceTag + "");
        }

        String name = data.getString("name");
        if (!name.isEmpty()) {
            st.queryBuild(queryType.and, "u", "@name", conditionType.LIKE, groupType.DEF, "%" + name + "%");
        }

        String ip = data.getString("ip");
        if (!ip.isEmpty()) {
            st.queryBuild(queryType.and, "t", "@log_ip", conditionType.EQ, groupType.DEF, ip);
        }

        String sDate = data.getString("sDate");
        String eDate = data.getString("eDate");

        if (!sDate.isEmpty() && !eDate.isEmpty()){
            st.queryBuild(queryType.and, "t", "@updateTime", conditionType.GTEQ, groupType.DEF, sDate);
            st.queryBuild(queryType.and, "t", "@updateTime", conditionType.LTEQ, groupType.DEF, eDate);
        }

        st.dataSort("t","updateTime", sortType.DESC);

        return st;
    }


    /**
     * 查询系统内已有的日志信息
     *
     * @param executeData
     * @return
     */
    @Access({AccessType.SEL})
    public Map<String, Object> selectLogger(BaseData executeData){
        return baseApi.selectPageBase(pageEngine(buildEngine(executeData),executeData,true));
    }

    /**
     * 根据查询列表设置的条件导出用户信息
     *
     * @param executeData
     * @return
     */
    @Access({AccessType.EXPORT})
    public ModelAndView exportLoggerData(BaseData executeData) {
        //1.设置xlxs的基本信息
        BaseData excelParams = new BaseData();
        excelParams.put("cellName", "reson,name,email,log_ip,sourceTag");
        excelParams.put("cellParseName", "操作类型,操作者姓名,企业邮箱,访问地址,执行结果");

        //2.对特定字段转换
        BaseData parseParams = new BaseData();
        BaseData parseReson = new BaseData();
        parseReson.put("0","系统登录");
        parseReson.put("1","接口访问");
        parseReson.put("11","新增");
        parseReson.put("22","更新");
        parseReson.put("33","删除");
        parseReson.put("2","文件上传");
        parseReson.put("3","数据导出");

        BaseData parseTag = new BaseData();
        parseTag.put("0","失败");
        parseTag.put("1","成功");
        parseTag.put("2","未登录/非法请求");
        parseTag.put("3","越权访问");


        parseParams.put("sourceTag",parseTag);
        parseParams.put("reson",parseReson);

        excelParams.put("parseCellValue", parseParams);
        excelParams.put("fileName", "系统日志信息");
        return excelImport.importExcel(baseApi.selectBase(buildEngine(executeData).selectFin("")), excelParams);
    }
}
