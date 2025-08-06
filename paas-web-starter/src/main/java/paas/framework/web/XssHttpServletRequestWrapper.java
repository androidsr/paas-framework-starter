package paas.framework.web;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Sets;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import paas.framework.model.exception.BusException;
import paas.framework.tools.JSON;
import paas.framework.tools.PaasUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Pattern[] XSS_PATTERNS = {Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE), Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), Pattern.compile("</script>", Pattern.CASE_INSENSITIVE), Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE), Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE), Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)};

    private static final String SQL_INJECT_REGEX = "\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";
    private static final Set<String> SKIP_FIELDS = Sets.newHashSet("password", "token");

    private byte[] body; // cleaned body (for JSON request only)
    private boolean isFileUpload;

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.isFileUpload = isMultipart(request);
    }

    private boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("multipart/form-data") || new StandardServletMultipartResolver().isMultipart(request);
    }

    private boolean isJsonRequest() {
        String contentType = super.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

    private void initBodyIfNecessary() {
        if (body != null || !isJsonRequest()) return;

        try {
            String originalBody = readInputStream(super.getInputStream());
            String cleanedBody = cleanRequestBody(originalBody);
            this.body = cleanedBody.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("XssWrapper读取请求体失败: {}", e.getMessage());
            this.body = new byte[0];
        }
    }

    private String readInputStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private String cleanRequestBody(String bodyStr) {
        if (PaasUtils.isEmpty(bodyStr)) return bodyStr;

        try {
            if (bodyStr.trim().startsWith("{")) {
                Map<String, Object> map = JSON.parseMapObject(bodyStr, String.class, Object.class);
                cleanMap(map);
                return JSON.toJSONString(map);
            } else if (bodyStr.trim().startsWith("[")) {
                List<Object> list = JSON.parseArray(bodyStr);
                cleanList(list);
                return JSON.toJSONString(list);
            } else {
                return cleanSQLInject(cleanXSS(bodyStr));
            }
        } catch (Exception e) {
            log.warn("清洗JSON失败，跳过: {}", e.getMessage());
            return bodyStr;
        }
    }

    private void cleanMap(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            if (SKIP_FIELDS.contains(key)) continue;

            if (val instanceof String) {
                String strVal = (String) val;
                map.put(key, cleanSQLInject(cleanXSS(strVal)));
            } else if (val instanceof Map<?, ?>) {
                Map<String, Object> subMap = (Map<String, Object>) val;
                cleanMap((Map<String, Object>) subMap);
            } else if (val instanceof List<?>) {
                List<Object> subList = (List<Object>) val;
                cleanList((List<Object>) subList);
            }
        }
    }

    private void cleanList(List<Object> list) {
        for (int i = 0; i < list.size(); i++) {
            Object val = list.get(i);
            if (val instanceof String) {
                String strVal = (String) val;
                list.set(i, cleanSQLInject(cleanXSS(strVal)));
            } else if (val instanceof Map<?, ?>) {
                Map<String, Object> subMap = (Map<String, Object>) val;
                cleanMap((Map<String, Object>) subMap);
            } else if (val instanceof List<?>) {
                List<Object> subList = (List<Object>) val;
                cleanList((List<Object>) subList);
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        if (isFileUpload) {
            try {
                return super.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        initBodyIfNecessary(); // 仅在 JSON 请求中才初始化 body

        if (body == null) {
            return new DelegatingServletInputStream(new ByteArrayInputStream(new byte[0]));
        }

        return new DelegatingServletInputStream(new ByteArrayInputStream(this.body));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public String getParameter(String name) {
        String val = super.getParameter(name);
        if (val == null || SKIP_FIELDS.contains(name)) return val;
        return cleanSQLInject(cleanXSS(val));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null || SKIP_FIELDS.contains(name)) return values;
        return Arrays.stream(values).map(v -> cleanSQLInject(cleanXSS(v))).toArray(String[]::new);
    }

    private String cleanXSS(String value) {
        if (PaasUtils.isEmpty(value)) return value;
        String result = value;
        for (Pattern pattern : XSS_PATTERNS) {
            result = pattern.matcher(result).replaceAll("");
        }
        return result;
    }

    private String cleanSQLInject(String value) {
        if (PaasUtils.isEmpty(value)) return value;
        Pattern sqlPattern = Pattern.compile(SQL_INJECT_REGEX, Pattern.CASE_INSENSITIVE);
        if (sqlPattern.matcher(value.toLowerCase()).find()) {
            log.error("检测到SQL注入：{}", value);
            throw new BusException("参数中包含非法SQL关键字！");
        }
        return value;
    }

    private static class DelegatingServletInputStream extends ServletInputStream {
        private final InputStream inputStream;

        public DelegatingServletInputStream(InputStream sourceStream) {
            this.inputStream = sourceStream;
        }

        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }

        @Override
        public boolean isFinished() {
            try {
                return inputStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}
