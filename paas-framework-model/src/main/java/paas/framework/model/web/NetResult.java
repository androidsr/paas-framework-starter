package paas.framework.model.web;

public interface NetResult<T> {
    /**
     * 响应码
     */
    int getCode();

    /**
     * 响应消息
     */
    String getMsg();

    /**
     * 响应数据
     */
    T getData();
}
