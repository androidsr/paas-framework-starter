package paas.framework.mybatis.dynamic;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
@ConditionalOnProperty(name = "paas.mybatis-plus.dynamic-data-source")
@EnableConfigurationProperties(DataSourceProperties.class)
@AutoConfigureBefore(value = {DataSourceAutoConfiguration.class})
public class DynamicDataSourceConfig {

    @Resource
    DataSourceProperties properties;

    @Bean
    public DataSource dataSource() {
        DynamicDataSource dynamicDataSource = DynamicDataSource.newInstance();
        DataSource dataSource = DataSourceBuilder.create().driverClassName(properties.getDriverClassName())
                .url(properties.getUrl()).username(properties.getUsername()).password(properties.getPassword()).build();
        dynamicDataSource.setDefaultTargetDataSource(dataSource);
        Map<Object, Object> targetDataSource = new HashMap<Object, Object>(0);
        dynamicDataSource.setTargetDataSources(targetDataSource);
        return dynamicDataSource;
    }

}
