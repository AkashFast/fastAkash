package prism.akash.controller.init;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;
import prism.akash.schema.BaseSchema;
import prism.akash.schema.core.coreBaseSchema;
import prism.akash.tools.scannerSchema.ScannerSchemaTool;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 系统基础数据初始化
 * TODO 应用于系统启动时,请勿在其他位置手动调用
 *
 * @author HaoNan Yan
 */
@Component
public class initDataTool implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(initDataTool.class);

    //TODO : 基础数据初始化 「关闭后系统启动时将不再自动对「数据库库表、字段」及「@schema逻辑层」进行数据同步」
    @Value("${akashConfig.init.enable}")
    public Boolean init;

    //TODO : 是否同步系统底层数据表 默认为「false」
    @Value("${akashConfig.init.baseInit}")
    public Boolean base;

    //TODO : 是否保存历史字段及表信息 默认为「false」 设置为「true」时，同步数据不会对原有数据进行删除「状态变更」
    @Value("${akashConfig.init.history}")
    public Boolean history;

    //TODO : 在init.enable为「true」时,可以指定需要同步的数据表「多个间以,隔开」,为空视为同步指定数据库所有数据表
    @Value("${akashConfig.init.tables}")
    public Boolean tables;

    //TODO : 获取系统应用名称
    @Value("${spring.application.name}")
    public String applicationName;

    @Autowired
    coreBaseSchema coreBaseSchema;

    @Autowired
    ScannerSchemaTool scannerSchemaTool;

    @Autowired
    BaseSchema baseSchema;

    /**
     * 初始化数据库表
     * *根据配置指定检查
     */
    @PostConstruct
    public void init() {
        //判断是否需要对基础数据进行初始化
        if (init) {
            Long start = new Date().getTime();
            logger.info(applicationName + " init data run start");
            //获取配置数据进行刷新
            logger.info("数据库表执行结果:" + coreBaseSchema.initBaseData(initBase()));

            logger.info("执行时间:" + (new Date().getTime() - start) / 1000 + "s");
            //重置schema数据
        }
    }

    /**
     * 初始化系统逻辑类
     * * 每次启动时检查
     */
    @PostConstruct
    public void initSchema() {
        if (init) {
            List<String> schemaList = scannerSchemaTool.getSystemSchema();
            for (String schema : schemaList) {
                String[] schemaNode = schema.split(",");

                //1.如果当前Schema不存在
                String tid = baseSchema.getTableIdByCode("sc_" + schemaNode[0]);
                if (tid.isEmpty()) {
                    //2.当前逻辑类需要初始化
                    if (schemaNode[2].equals("true")) {
                        //3.执行新增
                        BaseData insert = new BaseData();
                        insert.put("code", "sc_" + schemaNode[0]);
                        insert.put("type", 1);
                        insert.put("name", schemaNode[1]);
                        baseSchema.insertData(baseSchema.pottingData("cr_tables", insert));
                        logger.info("新增SCHEMA:" + schemaNode[0]);
                    }
                } else {
                    //如果当前Schema存在
                    //4.当前逻辑类不需要初始化
                    if (schemaNode[2].equals("false")) {
                        BaseData del = new BaseData();
                        del.put("id", tid);
                        del.put("del_submit", "1");
                        //5.执行删除
                        baseSchema.deleteData(baseSchema.pottingData("cr_tables", del));
                        logger.info("移除SCHEMA:" + schemaNode[0]);
                    }
                }

            }
        }
    }

    /**
     * 内部方法：用于获取系统配置的同步属性值
     *
     * @return
     */
    private BaseData initBase() {
        BaseData init = new BaseData();
        BaseData executeData = new BaseData();
        executeData.put("code", tables);
        executeData.put("history", history);
        executeData.put("base", base);
        init.put("executeData", JSON.toJSONString(executeData));
        return init;
    }
}
