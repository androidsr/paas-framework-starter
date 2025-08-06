# Kafka消息队列
针对spring boot 方式集成kafka，集成消息投递工具类进行了封装，支持普通消息，有序消息，事务消息，默认基于redis策略延迟消息，并针对异常重试进行了简单处理，可自定义存储持久化方式。
针对更专业稳定的事物消息，延迟消息，消息异常重试，建议使用Rocketmq。
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-kafka-starter</artifactId>
</dependency>
```
## yml配置
```yaml
spring:
  kafka:
    bootstrap-servers: 127.0.0.1:9092
    producer:
      ##重试次数
      retries: 5
      ##批次大小
      batch-size: 16384
      ##缓冲区大小
      buffer-memory: 33554432
      acks: 1
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: default-group
      enable-auto-commit: true
      auto-commit-interval: 30000
      max-poll-records: 50
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      listener:
        type: batch 
        concurrency: 1 
```
## 框架自定义配置
重试扫描采用本地线程轮询方式，进行了分布式锁处理。存在空跑线程的情况。延迟时间存在一定的偏差。
```yaml
paas:
  kafka:
    consumer:
      default-retry-enable: true
			scanner: true    ##启动消费异常扫描
      concurrency: 1   ##kafka并发控制
      interval: 30     ##扫描间隔时间秒
      max-retry: 3     ##重试默认最大次数
      fail-handler:    ##消费最终失败处理器实现KafkaConsumerFailHandler接口
      adapter:         ##自定义失败存储处理器consumerRetryAdapter（内置redis方案）
    producer:
      scanner: true        ##启动生产异常扫描
      interval: 30         ##扫描间隔时间秒
      max-retry: 5         ##重试默认最大次数
      fail-handler:        ##生产最终失败处理器实现KafkaProducerFailHandler接口
      adapter:             ##自定义失败存储处理器produceRetryAdapter（内置redis方案）    
```
## 操作工具类
MQ消费时必需进行幂等性处理，合理利用分布式锁保证消息的重复消费。
数据提交格式采用JSON字符串传输。
消息失败时自带重试机制。重试机制数据存储默认采用REDIS。重要接口也可实现接口自定义存储机制。
生产消息支持：普通消息，有序消息，事务消息等多种组合。
```java

/**
 * 发送消息
 * @param topic
 * @param message
 * @return
 */
public ListenableFuture<SendResult<String, Object>> sendMessage(String topic, String message) 

/**
 * 重试消息发送
 *
 * @param topic     主题
 * @param partition 分片
 * @param message   消息
 */
public ListenableFuture<SendResult<String, Object>> sendMessage(String topic, String key, String message) 

/**
 * 重试消息发送
 *
 * @param topic     主题
 * @param partition 分片
 * @param message   消息
 */
public ListenableFuture<SendResult<String, Object>> sendOrderMessage(String topic, Integer partition, String message) 


/**
 * 事物提交后发送消息
 *
 * @param topic   主题
 * @param message 消息体
 * @return
 */
public ListenableFuture<SendResult<String, Object>> sendCommitMessage(String topic, String message) 

/**
 * 事物提交后发送消息
 *
 * @param topic   主题
 * @param key     指定key
 * @param message 消息体
 * @return
 */
public ListenableFuture<SendResult<String, Object>> sendCommitMessage(String topic, String key, String message) 

/**
 * 事物提交后发送消息
 *
 * @param topic     主题
 * @param partition 分片
 * @param message   消息体
 * @return
 */
public ListenableFuture<SendResult<String, Object>> sendCommitOrderMessage(String topic, Integer partition, String message) 

public ListenableFuture<SendResult<String, Object>> sendRetryMessage(String topic, Integer partition,
                                                                     Long timestamp, Object key, Object value, Map<String, byte[]> header) 
 
```
## 示例代码
```java
@Autowired
KafkaHelper kafkaHelper;
```
投递普通消息，事务提交后发送消息；只保证在事务提交后发送消息，内置消息投递失败重试策略。由kafka自身处理。
```java
kafkaHelper.sendMessage(KafkaTopicConstants.KAFKA_TOPIC_ARTICLE, JSON.toJSONString(dto));

kafkaHelper.sendCommitMessage(KafkaTopicConstants.KAFKA_TOPIC_ARTICLE, JSON.toJSONString(message));
```
```java
@KafkaListener(topics = {KafkaTopicConstants.KAFKA_TOPIC_AREA_COLLECT}, groupId = KafkaTopicConstants.KAFKA_GROUP_AREA_COLLECT)
public void consumeAreaCollect(ConsumerRecord<String, String> record) {
    CollectExtraDTO data = JSON.parseObject(record.value(), CollectExtraDTO.class);
    collectionStorageFacade.extra(data);
}

```
