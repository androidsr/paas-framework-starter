package paas.framework.feign;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FeignHeader {
    private static List<String> headers = new ArrayList<>();

    /**
     * 初始化feign传参数
     *
     * @param key
     */
    public static void addHeader(String key) {
        headers.add(key);
    }

    /**
     * 获取feign header参数key
     */
    public static List<String> getHeaders() {
        return headers;
    }
}
