package prism.akash.tools.queues;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;

@Component
public class QueueSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * TODO: 将指定信息内容推送至消息队列
     *
     * @param executeData 消息主体对象
     * @return
     * @pparm queuesName         待推送队列的名称
     */
    public void send(String queuesName, BaseData executeData) {
        amqpTemplate.convertAndSend(queuesName, executeData);
    }

}
