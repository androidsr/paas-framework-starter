package paas.framework.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName: RabbitAdminOption
 * @author: sirui
 * @date: 2021/11/16 15:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RabbitAdminOption {
    /**
     * 交换机类型
     */
    private ExchangeType exchangeType;
    /**
     * 交换机名称
     */
    private String exchangeName;
    /**
     * 队列名称
     */
    private String queueName;
    /**
     * 路由Key
     */
    private String routingKey;

    /**
     * 头交换机headers
     */
    private Header header;
    /**
     * 设置是否持久化，默认是 false。durable 设置为 true 表示持久化，反之是非持久化。
     */
    Boolean durable;

    /**
     * 设置是否排他，默认也是 false。为 true 则设置队列为排他。
     */
    Boolean exclusive;

    /**
     * 设置是否自动删除，为 true 则设置队列为自动删除（当没有生产者或者消费者使用此队列，该队列会自动删除）
     */
    Boolean autoDelete;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private HeaderIsWhere isWhere;
        private String key;
        private Map<String, Object> data;
    }


    public enum HeaderIsWhere {
        /**
         * 匹配单个 key
         */
        Where("Where"),
        /**
         * 同时匹配多个 key
         */
        WhereAny("WhereAny"),
        /**
         * 匹配多个 key 中的一个或多个
         */
        WhereAll("WhereAll");

        public final String value;

        HeaderIsWhere(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

}
