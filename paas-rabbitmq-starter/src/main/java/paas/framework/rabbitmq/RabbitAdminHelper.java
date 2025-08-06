package paas.framework.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitAdminHelper {
    @Autowired
    RabbitAdmin rabbitAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 创建交换器
     *
     * @param exchangeType 交换器类型
     * @param exchangeName 交换器名称
     * @param durable      设置是否持久化
     * @param autoDelete   设置是否自动删除，为 true 则设置队列为自动删除，
     */
    public void addExchange(ExchangeType exchangeType, String exchangeName, Boolean durable, Boolean autoDelete) {
        Exchange exchange = createExchange(exchangeType, exchangeName, durable, autoDelete);
        rabbitAdmin.declareExchange(exchange);
    }

    /**
     * 删除一个Exchange
     *
     * @param exchangeName
     */
    public boolean deleteExchange(String exchangeName) {
        return rabbitAdmin.deleteExchange(exchangeName);
    }

    /**
     * 创建一个指定的Queue
     *
     * @param queueName
     * @return queueName
     */
    public void addQueue(String queueName, Boolean durable, Boolean exclusive, Boolean autoDelete) {
        Queue queue = createQueue(queueName, durable, exclusive, autoDelete);
        rabbitAdmin.declareQueue(queue);
    }

    /**
     * 删除一个queue
     *
     * @param queueName
     * @return queueName
     */
    public boolean deleteQueue(String queueName) {
        return rabbitAdmin.deleteQueue(queueName);
    }

    /**
     * 按照筛选条件，删除队列
     *
     * @param queueName
     * @param unused    是否被使用
     * @param empty     内容是否为空
     */
    public void deleteQueue(String queueName, boolean unused, boolean empty) {
        rabbitAdmin.deleteQueue(queueName, unused, empty);
    }

    /**
     * 清空某个队列中的消息，注意，清空的消息并没有被消费
     *
     * @param queueName
     * @return queueName
     */
    public void purgeQueue(String queueName) {
        rabbitAdmin.purgeQueue(queueName, false);
    }

    /**
     * 判断指定的队列是否存在
     *
     * @param queueName
     * @return
     */
    public boolean existQueue(String queueName) {
        return rabbitAdmin.getQueueProperties(queueName) == null ? false : true;
    }

    /**
     * 绑定一个队列到一个匹配型交换器使用一个routingKey
     */
    public void addBinding(RabbitAdminOption option) {
        Binding binding = bindingBuilder(option);
        rabbitAdmin.declareBinding(binding);
    }

    /**
     * 声明绑定
     *
     * @param binding
     */
    public void addBinding(Binding binding) {
        rabbitAdmin.declareBinding(binding);
    }

    /**
     * 解除交换器与队列的绑定
     */
    public void removeBinding(RabbitAdminOption option) {
        Binding binding = bindingBuilder(option);
        removeBinding(binding);
    }

    /**
     * 解除交换器与队列的绑定
     *
     * @param binding
     */
    public void removeBinding(Binding binding) {
        rabbitAdmin.removeBinding(binding);
    }

    /**
     * 创建一个交换器、队列，并绑定队列
     */
    public void andExchangeBindingQueue(RabbitAdminOption option) {
        //声明交换器
        addExchange(option.getExchangeType(), option.getExchangeName(), option.durable, option.autoDelete);
        //声明队列
        addQueue(option.getQueueName(), option.durable, option.exclusive, option.autoDelete);
        //声明绑定关系
        addBinding(option);
    }

    /**
     * 转换Message对象
     *
     * @param messageType
     * @param msg
     * @return
     */
    public Message getMessage(String messageType, Object msg) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(messageType);
        Message message = new Message(msg.toString().getBytes(), messageProperties);
        return message;
    }

    /**
     * 声明交换机
     *
     * @param exchangeType
     * @param exchangeName
     * @return
     */
    private Exchange createExchange(ExchangeType exchangeType, String exchangeName, Boolean durable, Boolean autoDelete) {
        if (ExchangeType.DIRECT.equals(exchangeType)) {
            return new DirectExchange(exchangeName, durable, autoDelete);
        }
        if (ExchangeType.TOPIC.equals(exchangeType)) {
            return new TopicExchange(exchangeName, durable, autoDelete);
        }
        if (ExchangeType.HEADERS.equals(exchangeType)) {
            return new HeadersExchange(exchangeName, durable, autoDelete);
        }
        if (ExchangeType.FANOUT.equals(exchangeType)) {
            return new FanoutExchange(exchangeName, durable, autoDelete);
        }
        return null;
    }

    /**
     * 声明绑定关系
     *
     * @return
     */
    private Binding bindingBuilder(RabbitAdminOption option) {
        if (ExchangeType.DIRECT.equals(option.getExchangeType())) {
            return BindingBuilder.bind(new Queue(option.getQueueName())).to(new DirectExchange(option.getExchangeName())).with(option.getRoutingKey());
        }
        if (ExchangeType.FANOUT.equals(option.getExchangeType())) {
            return BindingBuilder.bind(new Queue(option.getQueueName())).to(new FanoutExchange(option.getExchangeName()));
        }
        if (ExchangeType.TOPIC.equals(option.getExchangeType())) {
            return BindingBuilder.bind(new Queue(option.getQueueName())).
                    to(new TopicExchange(option.getExchangeName())).with(option.getRoutingKey());
        }
        if (ExchangeType.HEADERS.equals(option.getExchangeType())) {
            if (option.getHeader() != null) {
                if (option.getHeader().getIsWhere().equals(RabbitAdminOption.HeaderIsWhere.WhereAny)) {
                    return BindingBuilder.bind(new Queue(option.getQueueName())).
                            to(new HeadersExchange(option.getExchangeName())).whereAny(option.getHeader().getData()).match();

                } else if (option.getHeader().getIsWhere().equals(RabbitAdminOption.HeaderIsWhere.WhereAll)) {
                    return BindingBuilder.bind(new Queue(option.getQueueName())).
                            to(new HeadersExchange(option.getExchangeName())).whereAll(option.getHeader().getData()).match();

                } else {
                    return BindingBuilder.bind(new Queue(option.getQueueName())).
                            to(new HeadersExchange(option.getExchangeName())).where(option.getHeader().getKey()).exists();

                }
            }
        }
        return null;
    }

    /**
     * 创建队列
     *
     * @param queueName  队列名称
     * @param durable    设置是否持久化，默认是 false。durable 设置为 true 表示持久化，反之是非持久化。
     * @param exclusive  设置是否排他，默认也是 false。为 true 则设置队列为排他。
     * @param autoDelete 设置是否自动删除，为 true 则设置队列为自动删除（当没有生产者或者消费者使用此队列，该队列会自动删除）
     * @return
     */
    private Queue createQueue(String queueName, Boolean durable, Boolean exclusive, Boolean autoDelete) {
        return new Queue(queueName, durable, exclusive, autoDelete);
    }

}