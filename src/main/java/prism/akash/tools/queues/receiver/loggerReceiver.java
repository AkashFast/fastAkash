package prism.akash.tools.queues.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;
import prism.akash.schema.core.loggerSchema;

/**
 * 日志队列监听
 */
@Component
@RabbitListener(queues = "${akashConfig.log_queue}")
public class loggerReceiver {

    @Autowired
    loggerSchema loggerSchema;

    @RabbitHandler
    public void process(BaseData executeData) {
        // TODO 开始对队列中的数据进行处理
        try {
          loggerSchema.addLogger(executeData);
        } catch (Exception e) {
            // todo  解决消息队列重复试错的bug 抛出一个致命异常就会抛弃消费这个消息
            throw new MessageConversionException("消息消费失败，移出消息队列，不再试错");
        }
    }
}
