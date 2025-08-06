# Rabbitmq消息队列
基于spring-boot-starter-amqp 集成，提供基础工具类，不满足情况下可自行基于RabbitTemplate自行定义使用。
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-rabbitmq-starter</artifactId>
</dependency>
```
## yml配置
```yaml
spring:
	rabbitmq:
  	host:
  	port:
  	username:
  	password:
  	virtual-host:
  	connection-timeout:
  	publisher-confirm-type:
  	publisher-returns:
    listener:
      simple:
        concurrency: 5
        max-concurrency: 15
        acknowledge-mode: manual
        prefetch: 1
```

## 操作工具类
```java

    /**
     * 创建Exchange
     *
     * @param exchangeName
     */
    public void addExchange(ExchangeType exchangeType, String exchangeName)

    /**
     * 删除一个Exchange
     *
     * @param exchangeName
     */
    public boolean deleteExchange(String exchangeName) 

    /**
     * 创建一个指定的Queue
     *
     * @param queueName
     * @return queueName
     */
    public void addQueue(String queueName)

    /**
     * 删除一个queue
     *
     * @param queueName
     * @return queueName
     */
    public boolean deleteQueue(String queueName) 

    /**
     * 按照筛选条件，删除队列
     *
     * @param queueName
     * @param unused    是否被使用
     * @param empty     内容是否为空
     */
    public void deleteQueue(String queueName, boolean unused, boolean empty)

    /**
     * 清空某个队列中的消息，注意，清空的消息并没有被消费
     *
     * @param queueName
     * @return queueName
     */
    public void purgeQueue(String queueName)

    /**
     * 判断指定的队列是否存在
     *
     * @param queueName
     * @return
     */
    public boolean existQueue(String queueName) 

    /**
     * 绑定一个队列到一个匹配型交换器使用一个routingKey
     */
    public void addBinding(RabbitAdminOption option)

    /**
     * 声明绑定
     *
     * @param binding
     */
    public void addBinding(Binding binding)

    /**
     * 解除交换器与队列的绑定
     */
    public void removeBinding(RabbitAdminOption option)

    /**
     * 解除交换器与队列的绑定
     *
     * @param binding
     */
    public void removeBinding(Binding binding)

    /**
     * 创建一个交换器、队列，并绑定队列
     */
    public void andExchangeBindingQueue(RabbitAdminOption option)

    /**
     * 转换Message对象
     *
     * @param messageType
     * @param msg
     * @return
     */
    public Message getMessage(String messageType, Object msg)

    /**
     * 声明交换机
     *
     * @param exchangeType
     * @param exchangeName
     * @return
     */
    private Exchange createExchange(ExchangeType exchangeType, String exchangeName)

    /**
     * 声明绑定关系
     *
     * @return
     */
    private Binding bindingBuilder(RabbitAdminOption option) 

    /**
     * 声明队列
     *
     * @param queueName
     * @return
     */
    private Queue createQueue(String queueName)
```
```java

    /**
     * 发送消息
     */
    public void convertAndSend(String exchange, String routingKey, Object message)

    /**
     * 发送消息-延迟消息
     *
     * @param exchange   交换机
     * @param routingKey 路由key
     * @param message    消息内容
     * @param time       延迟时间秒
     */
    public void convertAndSend(String exchange, String routingKey, Object message, Integer time) 
```

