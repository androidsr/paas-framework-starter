# Rocketmq消息队列
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-rocketmq-starter</artifactId>
</dependency>
```
## yml配置
```yaml
rocketmq:
  name-server: 127.0.0.1:9876
  producer.group: producer-default-group
```
## 操作工具类
```java

/**
 * 发送同步消息
 *
 * @param topic   主题
 * @param message 消息内容
 */
public void sendMessage(String topic, Object message)

/**
 * 发送同步消息
 *
 * @param topic   主题
 * @param message 消息内容
 */
public SendResult syncSend(String topic, Object message)

/**
 * 发送单向消息
 *
 * @param topic   主题
 * @param message 消息内容
 */
public void sendOneWay(String topic, Object message)

/**
 * 发送延迟消息
 *
 * @param topic      主题
 * @param msg        消息内容
 * @param timeout    发送超时时间
 * @param delayLevel 1到18分别对应1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
 */
public SendResult syncSendDelay(String topic, Object msg, long timeout, int delayLevel)

/**
 * 发送顺序消息
 *
 * @param topic   主题
 * @param msg     消息内容
 * @param hashKey
 * @param timeout 发送超时时间
 * @return
 */
public SendResult syncSendOrderly(String topic, Object msg, String hashKey, long timeout)

/**
 * 发送顺序消息
 *
 * @param topic   主题
 * @param msg     消息内容
 * @param hashKey
 * @return
 */
public SendResult syncSendOrderly(String topic, Object msg, String hashKey)

/**
 * 发送异步消息
 *
 * @param topic    主题
 * @param msg      消息内容
 * @param callback 回调函数
 */
public void asyncSend(String topic, Object msg, SendCallback callback)

/**
 * 发送异步延迟消息
 *
 * @param topic      主题
 * @param msg        消息内容
 * @param timeout    发送超时时间
 * @param delayLevel 延迟等级
 * @param callback   回调函数
 */
public void asyncSendDelay(String topic, Object msg, long timeout, int delayLevel, SendCallback callback) 

/**
 * 发送同步消息-事物提交后
 *
 * @param topic   主题
 * @param message 消息内容
 * @return
 */
public SendResult sendMessageAfter(String topic, Object message)
```
## 示例代码
```java

@RocketMQMessageListener(consumerGroup = "消费分组", topic = "消费主题")
public class Consumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String s) {
        System.out.println("收到了消息：" + s);
    }
}

```
