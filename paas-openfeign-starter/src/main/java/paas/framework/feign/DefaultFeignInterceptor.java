package paas.framework.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import paas.framework.web.WebUtils;

@Component
public class DefaultFeignInterceptor implements RequestInterceptor {

    @Resource
    FeignConfigProperties feignConfigProperties;

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            request = WebUtils.get();
        } else {
            request = ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        if (request == null) {
            return;
        }
        for (String key : feignConfigProperties.getHeaderKey()) {
            template.header(key, request.getHeader(key));
        }
        for (String key : feignConfigProperties.getCookieKey()) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                break;
            }
            StringBuffer cookieValue = new StringBuffer();
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    cookieValue.append(key).append("=").append(cookie.getValue()).append(";");
                }
            }
            template.header(HttpHeaders.COOKIE, cookieValue.toString());
        }
    }
}
