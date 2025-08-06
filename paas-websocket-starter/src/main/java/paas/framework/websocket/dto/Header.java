package paas.framework.websocket.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Header {
    /**
     * 应用标识
     */
    private String appId;
    /**
     * 应用密钥
     */
    private String secret;
    /**
     * 用户标识
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 客户端类型(web,app,小程序)
     */
    private String clientType;
    /**
     * 平台类型（android,ios）
     */
    private String platformType;
    /**
     * 分组类型
     */
    private String groupId;
}
