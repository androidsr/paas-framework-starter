package paas.framework.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.util.ObjectUtils;
import paas.framework.tools.PaasUtils;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
public class RedissonConfig {

    @Autowired
    RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (!ObjectUtils.isEmpty(redisProperties.getCluster())) {
            ClusterServersConfig clusterServers = config.useClusterServers();
            clusterServers.setScanInterval(2000);
            for (String node : redisProperties.getCluster().getNodes()) {
                clusterServers.addNodeAddress("redis://" + node);
            }
            if (PaasUtils.isNotEmpty(redisProperties.getPassword())) {
                clusterServers.setPassword(redisProperties.getPassword());
            }
        } else if (!ObjectUtils.isEmpty(redisProperties.getSentinel())) {
            SentinelServersConfig sentinelServers = config.useSentinelServers();
            sentinelServers.setScanInterval(2000);
            sentinelServers.setMasterName(redisProperties.getSentinel().getMaster());
            List<String> nodes = new ArrayList<>();
            for (String node : redisProperties.getSentinel().getNodes()) {
                nodes.add("redis://" + node);
            }
            sentinelServers.setSentinelAddresses(nodes);
            if (PaasUtils.isNotEmpty(redisProperties.getPassword())) {
                sentinelServers.setPassword(redisProperties.getPassword());
            }
        } else {
            config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
            if (PaasUtils.isNotEmpty(redisProperties.getPassword())) {
                config.useSingleServer().setPassword(redisProperties.getPassword());
            }
        }
        config.setCodec(new StringCodec());
        config.setLockWatchdogTimeout(60000);
        return Redisson.create(config);
    }
}

