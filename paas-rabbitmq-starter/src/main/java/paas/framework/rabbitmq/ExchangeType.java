package paas.framework.rabbitmq;

public enum ExchangeType {
    /**
     * 直连交换机
     */
    DIRECT("DIRECT"),
    /**
     * 广播交换机
     */
    FANOUT("FANOUT"),
    /**
     * 模糊通配符交换机
     */
    TOPIC("TOPIC"),
    /**
     * 头交换机
     */
    HEADERS("HEADERS");
    public final String value;

    ExchangeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
