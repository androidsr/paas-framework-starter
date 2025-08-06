package paas.framework.web;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.SneakyThrows;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import paas.framework.tools.PaasUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @ClassName: WebUtils
 * @author: sirui
 * @date: 2021/11/23 15:50
 */
public class WebUtils {
    private static TransmittableThreadLocal<HttpServletRequest> requestThreadLocal = new TransmittableThreadLocal<>();

    public static HttpServletRequest get() {
        HttpServletRequest request = requestThreadLocal.get();
        if (ObjectUtils.isEmpty(request)) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                return ((ServletRequestAttributes) requestAttributes).getRequest();
            }
        }
        return request;
    }

    public static void set(HttpServletRequest request) {
        requestThreadLocal.set(request);
    }

    public static void remove() {
        requestThreadLocal.remove();
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return requestThreadLocal.get();
    }

    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    public static String getHeaderValue(String key) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String value = request.getHeader(key);
        return value;
    }

    public static String getCookieValue(String key) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (!PaasUtils.equals(name, key)) {
                    continue;
                }
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void addHeader(String key, String value) {
        HttpServletResponse response = getResponse();
        if (response == null) {
            return;
        }
        response.addHeader(key, value);
    }

    @SneakyThrows
    public static String getFileCnName(String name) {
        HttpServletRequest request = getRequest();
        name = URLEncoder.encode(name, "UTF-8");
        if (request != null) {
            String userAgent = request.getHeader("USER-AGENT");
            if (PaasUtils.nullToBlank(userAgent).contains("MSIE")) {
                name = URLEncoder.encode(name, "UTF8");
            } else if (PaasUtils.nullToBlank(userAgent).contains("Mozilla")) {
                name = new String(name.getBytes(), "ISO-8859-1");
            } else {
                name = URLEncoder.encode(name, "UTF8");
            }
        }
        return name;
    }
}
