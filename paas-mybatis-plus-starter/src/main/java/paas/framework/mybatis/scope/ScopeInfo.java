package paas.framework.mybatis.scope;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ScopeInfo {

    private Map<String, Object> params;

    private ScopeInfo() {
    }

    public static ScopeInfo create() {
        ScopeInfo config = new ScopeInfo();
        config.setParams(new ConcurrentHashMap<>(4));
        return config;
    }

    public ScopeInfo put(String key, Object value) {
        if (value != null) {
            params.put(key, value);
        }
        return this;
    }
}