package paas.framework.monitor;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import paas.framework.tools.JSON;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RefreshScope
public class ActuatorFilter implements Filter {

    @Value("${paas.actuator.ip:null}")
    String ip;

    private List<String> ips = null;

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress;
        try {

            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress.trim();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String url = req.getRequestURI();

        if (url.contains("/actuator/")) {
            if (ip == null || ip.equals("null")) {
                result401(res);
                return;
            }
            if (ips == null) {
                ips = Arrays.asList(ip.split(","));
            }
            String remoteHost = getIpAddr(req);
            for (String v : ips) {
                if (v.contains(remoteHost) || remoteHost.contains(v)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
            result401(res);
            return;
        }
        chain.doFilter(request, response);
    }

    private void result401(HttpServletResponse res) throws IOException {
        res.setStatus(401);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("msg", "无权限访问！");
        res.getWriter().write(JSON.toJSONString(result));
    }
}
