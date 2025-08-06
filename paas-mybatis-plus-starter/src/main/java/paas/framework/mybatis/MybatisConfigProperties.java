package paas.framework.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: sirui
 * @date: 2021/11/11 19:19
 */
@Data
@Component
@ConfigurationProperties(prefix = "paas.mybatis-plus")
public class MybatisConfigProperties {
    private String dbType;
    private DynamicTable dynamicTable;
    private Boolean dynamicDataSource;
    private DataScope dataScope;

    @Data
    public static class DynamicTable {
        private Boolean enable;
        private List<String> tables;
    }

    @Data
    public static class DataScope {
        private Boolean enable;
    }
}
