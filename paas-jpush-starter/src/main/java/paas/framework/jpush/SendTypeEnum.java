package paas.framework.jpush;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息推送类型
 */
@Getter
@AllArgsConstructor
public enum SendTypeEnum {
    ALIAS(1, "别名推送【用户ID】"),
    TAGS(2, "标签【自定义类型】"),
    ;
    @JsonValue
    private int code;
    private String title;
}
