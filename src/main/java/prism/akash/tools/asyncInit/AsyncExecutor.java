package prism.akash.tools.asyncInit;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 启用定时任务
 */
@Component
@Configuration
@EnableScheduling
public class AsyncExecutor {

    private final Logger logger = LoggerFactory.getLogger(AsyncExecutor.class);

    @Autowired
    AsyncInitData asyncInitData;

    /**
     * 开启数据同步任务
     * **注意：Spring的cron表达式只需要六个参数(秒 分 时 日 月 周)
     * ** 每日01时执行,拉取上一日增量数据
     */
//    @Scheduled(cron = "50 49 16 * * ?")
    private void configureTasks() throws Exception{
        logger.info("定时任务：核心数据同步Loading……"+new Date());
//        logger.info("定时任务执行结果："+JSON.toJSONString(asyncInitData.executor()));
        logger.info("定时任务结束:"+new Date());
    }

}
