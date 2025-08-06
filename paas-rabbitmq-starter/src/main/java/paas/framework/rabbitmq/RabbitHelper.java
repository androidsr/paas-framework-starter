package paas.framework.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import paas.framework.model.exception.BusException;
import paas.framework.tools.PaasUtils;

import java.util.UUID;

@Slf4j
@Component
public class RabbitHelper {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     */
    public void convertAndSend(String exchange, String routingKey, Object content) {
        rabbitTemplate.convertAndSend(exchange, routingKey, content);
    }

    /**
     * 发送消息
     */
    public void convertAndSend(String exchange, String routingKey, Object content, MessagePostProcessor messagePostProcessor) {
        rabbitTemplate.convertAndSend(exchange, routingKey, content, messagePostProcessor);
    }

    /**
     * 发送消息
     */
    public void convertAndSend(String exchange, String routingKey, Object content, MessagePostProcessor messagePostProcessor, String messageId) {
        if (PaasUtils.isEmpty(exchange)) {
            BusException.fail(5672, "交换机不能为空");
        }

        if (PaasUtils.isEmpty(routingKey)) {
            BusException.fail(5672, "路由键不能为空");
        }

        if (content == null || PaasUtils.isEmpty(content.toString())) {
            BusException.fail(5672, "发送的内容不能为空");
        }
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(PaasUtils.isEmpty(messageId) ? UUID.randomUUID().toString() : messageId);
        if (messagePostProcessor == null) {
            this.rabbitTemplate.convertAndSend(exchange, routingKey, content, correlationData);
        } else {
            // 发送对应的消息
            this.rabbitTemplate.convertAndSend(exchange, routingKey, content, messagePostProcessor, correlationData);
        }
    }

    /**
     * 发送消息-延迟消息
     *
     * @param exchangeType 交换机
     * @param queue        路由key
     * @param message      消息内容
     * @param time         延迟时间秒
     */
    public void convertAndSend(ExchangeType exchangeType, String queue, Object message, Integer time) {
        rabbitTemplate.convertAndSend(exchangeType.getValue(), queue, message, info -> {
            info.getMessageProperties().setHeader("x-delay", time * 1000);
            return info;
        });
    }
}