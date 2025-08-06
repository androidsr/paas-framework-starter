package paas.framework.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WsActionEnum {
    BIND("初始化连接"),
    KEEP_ALIVE("心跳消息");
    private String title;
}
