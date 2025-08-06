package paas.framework.model.exception;

import paas.framework.model.enums.ResultMessage;
import paas.framework.model.web.HttpResult;
import paas.framework.model.web.ResultCode;

/**
 * @ClassName: Asserts
 * @author: sirui
 * @date: 2021/11/20 16:43
 */
public class Asserts {

    public static void isTrueError(boolean expression, int code, String message) throws BusException {
        if (expression) {
            throw new BusException(code, message);
        }
    }


    public static void isTrueError(boolean expression, String message) throws BusException {
        if (expression) {
            throw new BusException(message);
        }
    }

    public static void isTrueError(boolean expression, ResultCode resultCode) throws BusException {
        if (expression) {
            throw new BusException(resultCode);
        }
    }

    public static void isFalseError(boolean expression, String message) throws BusException {
        if (!expression) {
            throw new BusException(message);
        }
    }

    public static void isFalseError(boolean expression, int code, String message) throws BusException {
        if (!expression) {
            throw new BusException(code, message);
        }
    }

    public static void isFalseError(boolean expression, ResultCode resultCode) throws BusException {
        if (!expression) {
            throw new BusException(resultCode);
        }
    }

    public static <T> T feignResultVerify(HttpResult<T> result) throws BusException {
        if (result == null) {
            throw new BusException(ResultMessage.FEIGN_NET_CHECK);
        } else if (result.getCode() != ResultMessage.SUCCESS.getCode()) {
            throw new BusException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

    public static <T> T feignResultDataVerify(HttpResult<T> result) throws BusException {
        if (result == null) {
            throw new BusException(ResultMessage.FEIGN_NET_CHECK);
        } else if (result.getCode() != ResultMessage.SUCCESS.getCode()) {
            throw new BusException(result.getCode(), result.getMsg());
        } else if (result.getData() == null) {
            throw new BusException(ResultMessage.FEIGN_NET_DATA_NULL);
        }

        return result.getData();
    }

    public static boolean feignIsSuccess(HttpResult result) throws BusException {
        return result != null && result.getCode() == ResultMessage.SUCCESS.getCode();
    }

    public static boolean feignIsDataSuccess(HttpResult result) throws BusException {
        return result != null && result.getCode() == ResultMessage.SUCCESS.getCode() && result.getData() != null;
    }

}
