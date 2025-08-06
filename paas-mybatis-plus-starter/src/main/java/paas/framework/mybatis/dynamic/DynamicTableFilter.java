package paas.framework.mybatis.dynamic;

import paas.framework.mybatis.scope.ScopeContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import java.io.IOException;

@Slf4j
@Component
public class DynamicTableFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
        DynamicTableConfig.remove();
        ScopeContextHolder.remove();
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}