package paas.framework.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import paas.framework.mybatis.dynamic.DynamicTableHandler;
import paas.framework.mybatis.method.MysqlInjector;
import paas.framework.mybatis.scope.ScopeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import jakarta.annotation.Resource;

@AutoConfiguration
public class MybatisPlusConfig {
    @Resource
    MybatisConfigProperties mybatisConfigProperties;

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (mybatisConfigProperties.getDynamicTable() != null && mybatisConfigProperties.getDynamicTable().getEnable()) {
            DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
            dynamicTableNameInnerInterceptor.setTableNameHandler(new DynamicTableHandler(mybatisConfigProperties.getDynamicTable().getTables()));
            interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        }
        if (mybatisConfigProperties.getDataScope() != null && mybatisConfigProperties.getDataScope().getEnable()) {
            interceptor.addInnerInterceptor(new DataPermissionInterceptor(new ScopeHandler()));
        }
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor();
        innerInterceptor.setDbType(DbType.valueOf(mybatisConfigProperties.getDbType()));
        interceptor.addInnerInterceptor(innerInterceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.setCacheEnabled(true);
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setCallSettersOnNulls(true);
            configuration.setJdbcTypeForNull(JdbcType.NULL);
        };
    }

    @Bean
    public MysqlInjector customizedSqlInjector() {
        return new MysqlInjector();
    }
}