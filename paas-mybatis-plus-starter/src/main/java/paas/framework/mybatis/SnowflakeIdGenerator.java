package paas.framework.mybatis;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import paas.framework.snowflake.PaasIdWorker;

@Slf4j
@Primary
@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {

    @Override
    public Long nextId(Object entity) {
        return PaasIdWorker.nextId();
    }
}