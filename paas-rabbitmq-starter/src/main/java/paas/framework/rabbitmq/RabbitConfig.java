package paas.framework.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
public class RabbitConfig {
    @Autowired
    RabbitmqProperties rabbitmqProperties;

    /**
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqProperties.getHost());
        connectionFactory.setPort(rabbitmqProperties.getPort());
        connectionFactory.setUsername(rabbitmqProperties.getUsername());
        connectionFactory.setPassword(rabbitmqProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitmqProperties.getVirtualHost());
        connectionFactory.setPublisherReturns(rabbitmqProperties.getPublisherReturns());
        return connectionFactory;
    }

    /**
     * 重新实例化 RabbitAdmin 操作类
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    /**
     * 重新实例化 RabbitTemplate 操作类
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setMandatory(rabbitmqProperties.getMandatory());

        return rabbitTemplate;
    }
}