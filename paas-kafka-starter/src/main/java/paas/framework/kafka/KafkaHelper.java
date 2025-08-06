package paas.framework.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class KafkaHelper implements ProducerListener {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    TransactionCommitHandler transactionCommitHandler;

    /**
     * 发送消息
     * @param topic
     * @param message
     * @return
     */
    public CompletableFuture<SendResult<String, Object>> sendMessage(String topic, String message) {
        ProducerRecord record = new ProducerRecord(topic, message);
        return kafkaTemplate.send(record);
    }

    /**
     * 重试消息发送
     *
     * @param topic     主题
     * @param partition 分片
     * @param message   消息
     */
    public CompletableFuture<SendResult<String, Object>> sendMessage(String topic, String key, String message) {
        ProducerRecord record = new ProducerRecord(topic,key, message);
        return kafkaTemplate.send(record);
    }

    /**
     * 重试消息发送
     *
     * @param topic     主题
     * @param partition 分片
     * @param message   消息
     */
    public CompletableFuture<SendResult<String, Object>> sendOrderMessage(String topic, Integer partition, String message) {
        ProducerRecord record = new ProducerRecord(topic, partition, System.currentTimeMillis(), null, message);
        return kafkaTemplate.send(record);
    }


    /**
     * 事物提交后发送消息
     *
     * @param topic   主题
     * @param message 消息体
     * @return
     */
    public CompletableFuture<SendResult<String, Object>> sendCommitMessage(String topic, String message) {
        AtomicReference<CompletableFuture<SendResult<String, Object>>> result = new AtomicReference<>();
        transactionCommitHandler.handle(() -> {
            try {
                result.set(sendMessage(topic,  message));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("kafka 发送消息-主题：{}, 内容：{}, 异常信息：{}", topic, message, e.getMessage());
            }
        });
        return result.get();
    }

    /**
     * 事物提交后发送消息
     *
     * @param topic   主题
     * @param key     指定key
     * @param message 消息体
     * @return
     */
    public CompletableFuture<SendResult<String, Object>> sendCommitMessage(String topic, String key, String message) {
        AtomicReference<CompletableFuture<SendResult<String, Object>>> result = new AtomicReference<>();
        transactionCommitHandler.handle(() -> {
            try {
                result.set(sendMessage(topic, key, message));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("kafka 发送消息-主题：{}, 内容：{}, 异常信息：{}", topic, message, e.getMessage());
            }
        });
        return result.get();
    }

    /**
     * 事物提交后发送消息
     *
     * @param topic     主题
     * @param partition 分片
     * @param message   消息体
     * @return
     */
    public CompletableFuture<SendResult<String, Object>> sendCommitOrderMessage(String topic, Integer partition, String message) {
        AtomicReference<CompletableFuture<SendResult<String, Object>>> result = new AtomicReference<>();
        transactionCommitHandler.handle(() -> {
            try {
                result.set(sendOrderMessage(topic, partition, message));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("kafka 发送消息-主题：{}, 内容：{}, 异常信息：{}", topic, message, e.getMessage());
            }
        });
        return result.get();
    }

    public CompletableFuture<SendResult<String, Object>> sendRetryMessage(String topic, Integer partition,
                                                                         Long timestamp, Object key, Object value, Map<String, byte[]> header) {
        Headers headers = new RecordHeaders();
        header.forEach((k, v) -> headers.add(new RecordHeader(k, v)));
        ProducerRecord record = new ProducerRecord(topic, partition,
                timestamp, key, value, headers);
        return kafkaTemplate.send(record);
    }

}
