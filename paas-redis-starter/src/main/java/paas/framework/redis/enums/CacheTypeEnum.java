package paas.framework.redis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CacheTypeEnum {
    CREATE("缓存加载"),
    DELETE("缓存删除");
    private String name;
}
