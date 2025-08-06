package paas.framework.model.exception;

import paas.framework.model.enums.ResultMessage;
import paas.framework.model.web.ResultCode;
import lombok.Data;

/**
 * @ClassName: BusinessException
 * @author: sirui
 * @date: 2021/11/20 16:37
 */
@Data
public class BusException extends RuntimeException {
    private Integer code;
    private String message;

    public BusException(String message) {
        super(message);
        this.code = ResultMessage.FAIL.getCode();
        this.message = message;
    }

    public BusException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    public BusException(Integer code, Throwable e) {
        super(e.getMessage());
        this.code = code;
        this.message = e.getMessage();
    }

    public static void fail(Integer code, String message) {
        throw new BusException(code, message);
    }

    public static void fail(ResultCode code, String message) {
        throw new BusException(code, message);
    }

    public static void fail(ResultCode resultCode) {
        throw new BusException(resultCode);
    }
    public static void fail(String message) {
        throw new BusException(message);
    }
}
