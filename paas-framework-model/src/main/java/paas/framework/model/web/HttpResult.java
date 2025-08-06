package paas.framework.model.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;

import java.io.Serializable;

public class HttpResult<T> implements NetResult<T>, Serializable {
    /**
     * 响应码
     */
    private int code;
    /**
     * 响应消息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String msg;
    /**
     * 响应数据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public HttpResult() {
    }

    public HttpResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public HttpResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> HttpResult<T> ok() {
        HttpResult r = new HttpResult();
        r.setCode(ResultMessage.SUCCESS.getCode());
        r.setMsg(ResultMessage.SUCCESS.getMessage());
        return r;
    }

    public static <T> HttpResult<T> ok(Object data) {
        HttpResult r = new HttpResult();
        r.setData(data);
        r.setCode(ResultMessage.SUCCESS.getCode());
        r.setMsg(ResultMessage.SUCCESS.getMessage());
        return r;
    }

    public static <T> HttpResult<T> isOk(Boolean flag) {
        if (flag) {
            return ok(true);
        } else {
            return fail(ResultMessage.FAIL);
        }
    }

    public static <T> HttpResult<T> fail(int code, String msg) {
        HttpResult r = new HttpResult();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> HttpResult<T> fail(String msg) {
        HttpResult r = new HttpResult();
        r.setCode(ResultMessage.UNKNOW_USER_ERROR.getCode());
        r.setMsg(msg);
        return r;
    }

    public static <T> HttpResult<T> fail(ResultCode result) {
        HttpResult r = new HttpResult();
        r.setCode(result.getCode());
        r.setMsg(result.getMessage());
        return r;
    }

    public static <T> HttpResult<T> fail(BusException e) {
        HttpResult r = new HttpResult();
        r.setCode(e.getCode());
        r.setMsg(e.getMessage());
        return r;
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }
}
