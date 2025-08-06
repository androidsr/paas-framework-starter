package paas.framework.rocketmq;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class RocketMQHelper {
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    /**
     * 发送同步消息
     *
     * @param topic   主题
     * @param message 消息内容
     */
    public void sendMessage(String topic, Object message) {
        sendMessage(topic, message, false);
    }

    public void sendMessage(String topic, Object message, Boolean isTransaction) {
        if (isTransaction) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    rocketMQTemplate.convertAndSend(topic, MessageBuilder.withPayload(message).build());
                }
            });
        } else {
            rocketMQTemplate.convertAndSend(topic, MessageBuilder.withPayload(message).build());
        }
    }

    /**
     * 发送同步消息
     *
     * @param topic   主题
     * @param message 消息内容
     */
    public SendResult syncSend(String topic, Object message) {
        SendResult sendResult = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(message).build());
        return sendResult;
    }

    /**
     * 发送单向消息
     *
     * @param topic   主题
     * @param message 消息内容
     */
    public void sendOneWay(String topic, Object message) {
        rocketMQTemplate.convertAndSend(topic, message);
    }

    /**
     * 发送延迟消息(不关心发送结果，如日志)
     *
     * @param topic      主题
     * @param msg        消息内容
     * @param timeout    发送超时时间
     * @param delayLevel 1到18分别对应1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    public SendResult syncSendDelay(String topic, Object msg, long timeout, int delayLevel) {
        Message message = MessageBuilder.withPayload(msg).build();
        SendResult sendResult = rocketMQTemplate.syncSend(topic, message, timeout, delayLevel);
        return sendResult;
    }

    /**
     * 发送顺序消息
     *
     * @param topic   主题
     * @param msg     消息内容
     * @param hashKey
     * @param timeout 发送超时时间
     * @return
     */
    public SendResult syncSendOrderly(String topic, Object msg, String hashKey, long timeout) {
        Message message = MessageBuilder.withPayload(msg).build();
        SendResult sendResult = rocketMQTemplate.syncSendOrderly(topic, message, hashKey, timeout);
        return sendResult;
    }

    /**
     * 发送顺序消息
     *
     * @param topic   主题
     * @param msg     消息内容
     * @param hashKey
     * @return
     */
    public SendResult syncSendOrderly(String topic, Object msg, String hashKey) {
        Message message = MessageBuilder.withPayload(msg).build();
        SendResult sendResult = rocketMQTemplate.syncSendOrderly(topic, message, hashKey);
        return sendResult;
    }

    /**
     * 发送异步消息
     *
     * @param topic    主题
     * @param message  消息内容
     * @param callback 回调函数
     */
    public void asyncSend(String topic, Object message, SendCallback callback) {
        rocketMQTemplate.asyncSend(topic, MessageBuilder.withPayload(message).build(), callback);
    }

    /**
     * 发送异步延迟消息
     *
     * @param topic      主题
     * @param msg        消息内容
     * @param timeout    发送超时时间
     * @param delayLevel 延迟等级
     * @param callback   回调函数
     */
    public void asyncSendDelay(String topic, Object msg, long timeout, int delayLevel, SendCallback callback) {
        Message message = MessageBuilder.withPayload(msg).build();
        rocketMQTemplate.asyncSend(topic, message, callback, timeout, delayLevel);
    }

    /**
     * 发送事务消息
     *
     * @param topic
     * @param body
     */
    public TransactionSendResult sendMessageInTransaction(String topic, Object body, String transactionId) {
        Message<Object> message = MessageBuilder.withPayload(body).setHeader("key", transactionId).build();
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(topic, message, topic);
        return sendResult;
    }


}
