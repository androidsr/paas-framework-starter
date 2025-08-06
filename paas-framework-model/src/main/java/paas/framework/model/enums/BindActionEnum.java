package paas.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BindActionEnum {
    SHOW(1, "显示"),
    BIND(2, "绑定"),
    UN_BIND(3, "解绑");

    @JsonValue
    private Integer code;
    private String title;
}
