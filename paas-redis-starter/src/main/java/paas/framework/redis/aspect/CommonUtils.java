package paas.framework.redis.aspect;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtils {

    /**
     * 设置header参数key
     *
     * @param exp 缓存key表达式
     */
    public static String setHeaderType(String exp) {
        HttpServletRequest request;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return "";
        }
        request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return getHeaderKey(request, exp);
    }

    /**
     * 获取header参数
     *
     * @param request
     * @param exp     缓存key表达式
     */
    public static String getHeaderKey(HttpServletRequest request, String exp) {
        String[] paramNames = exp.split("\\.");
        if (paramNames.length != 2) {
            return "";
        }
        String headerValue = request.getHeader(paramNames[1]);
        return headerValue == null ? "" : headerValue;
    }

}
