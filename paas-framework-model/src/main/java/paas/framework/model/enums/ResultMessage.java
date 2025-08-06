package paas.framework.model.enums;


import paas.framework.model.web.ResultCode;

public enum ResultMessage implements ResultCode {
    SUCCESS(200, "操作成功"),
    FAIL(5000, "操作失败"),
    UNKNOW_ERROR(5055, "服务异常,请稍后再试!"),
    VALIDATE_ERROR(5001, "参数验证异常"),
    NOT_FOUND(5002, "资源不存在"),
    REPETITION(5003, "数据标识已存在"),
    SUB_SOURCE_RELEVANCY(5004, "关联资源不为空"),
    INVOKE_FAILURE(5003, "访问失败"),
    ACCESS_DENIED_ERROR(5005, "访问受限"),
    PARAMETER_ERROR(5006, "请求参数有误"),
    VAILD_USER_ERROR(5223, "无效的用户信息"),
    VAILD_USER_LOGIN_ERROR(5224, "用户未登录"),
    UNAUTHORIZED_ERROR(901, "登录过期,请重新登录"),
    SYSTEM_CONFIG_ERROR(5022, "动态分表参数无效"),
    FEIGN_FAILURE(5054, "内部接口调用,请稍后再试!"),
    FEIGN_NET_ERROR(7701, "FEIGN接口失败"),
    FEIGN_NET_CHECK(7702, "内部接口调用结束验证未通过"),
    FEIGN_NET_DATA_NULL(7703, "内部接口响应数据为空"),
    LOCK_ERROR(9001, "分布式锁-业务处理异常"),
    LOCK_WAIT_TIMEOUT(9002, "分布式锁获取等待超时"),
    EXTERNAL_NET_ERROR(6701, "外部系统调用失败"),
    CACHE_ERROR(8701, "缓存处理异常"),
    UNKNOW_USER_ERROR(5555, "默认异常响应码");

    private final int code;
    private final String message;

    private ResultMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
