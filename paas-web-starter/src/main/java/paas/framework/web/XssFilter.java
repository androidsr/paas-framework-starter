package paas.framework.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * XSS过虑器配置
 */
@WebFilter(filterName = "xssFilter", urlPatterns = "/*", asyncSupported = true)
@ConditionalOnProperty(name = "paas.xss-filter.enabled")
public class XssFilter implements Filter {

    @Autowired
    XssProperties xssProperties;

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;
        String uri = req.getRequestURI();
        for (String exclude:xssProperties.getExcludes()){
            if (uri.contains(exclude)){
                arg2.doFilter(arg0, response);
                return;
            }
        }
        XssHttpServletRequestWrapper reqW = new XssHttpServletRequestWrapper(req);
        arg2.doFilter(reqW, response);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig1) throws ServletException {
    }
}