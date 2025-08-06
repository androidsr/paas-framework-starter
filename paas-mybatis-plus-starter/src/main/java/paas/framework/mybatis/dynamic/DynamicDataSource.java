package paas.framework.mybatis.dynamic;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import paas.framework.tools.PaasUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource implements InitializingBean {

    private static final TransmittableThreadLocal<String> dataSourceHolder = new TransmittableThreadLocal<>();
    private static DynamicDataSource instance;

    // 存储所有动态数据源
    private final Map<Object, Object> dynamicTargetDataSources = new HashMap<>();

    private DynamicDataSource() {
    }

    public static DynamicDataSource newInstance() {
        if (instance == null) {
            instance = new DynamicDataSource();
        }
        return instance;
    }

    // 设置当前数据源
    public void setDataSource(String dataSourceName) {
        if (PaasUtils.isNotEmpty(dataSourceName) && !dataSourceName.equals(dataSourceHolder.get())) {
            dataSourceHolder.set(dataSourceName);
            log.info("切换到数据源: {}", dataSourceName);
        } else {
            log.warn("指定数据源为空或已经是当前数据源: {}", dataSourceName);
        }
    }

    // 清除当前数据源
    public void clearDataSource() {
        dataSourceHolder.remove();
    }

    // 获取当前数据源 key
    @Override
    protected Object determineCurrentLookupKey() {
        String currentDataSource = dataSourceHolder.get();
        return currentDataSource;
    }

    // 添加数据源
    public void addTargetDataSource(String dataSourceName, String driverClass, String url, String username, String password) {
        if (dynamicTargetDataSources.containsKey(dataSourceName)) {
            log.info("数据源 {} 已存在，跳过添加", dataSourceName);
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClass);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);

        DataSource dataSource = new HikariDataSource(config);
        try {
            dataSource.getConnection().close();
        } catch (SQLException e) {
            log.error("无法连接到数据源 {}: {}", dataSourceName, e.getMessage());
            throw new RuntimeException("数据源配置失败: " + dataSourceName, e);
        }

        dynamicTargetDataSources.put(dataSourceName, dataSource);
        this.setTargetDataSources(dynamicTargetDataSources);
        afterPropertiesSet(); // 确保 AbstractRoutingDataSource 初始化
        log.info("成功添加数据源: {}", dataSourceName);
    }

    // 删除数据源
    public void removeTargetDataSource(String dataSourceName) {
        if (dynamicTargetDataSources.containsKey(dataSourceName)) {
            dynamicTargetDataSources.remove(dataSourceName);
            this.setTargetDataSources(dynamicTargetDataSources);
            afterPropertiesSet();
            log.info("成功移除数据源: {}", dataSourceName);
        } else {
            log.warn("数据源 {} 不存在，无法移除", dataSourceName);
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.setTargetDataSources(dynamicTargetDataSources);
        super.afterPropertiesSet();
        log.info("DynamicDataSource 初始化完成");
    }
}
