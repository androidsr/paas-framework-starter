package paas.framework.websocket.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WebsocketModel implements Serializable {
    private String action;
    private Header header;
    private String data;
}
