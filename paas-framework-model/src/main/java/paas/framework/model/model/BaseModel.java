package paas.framework.model.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: BaseModel
 * @author: sirui
 * @date: 2021/11/23 10:30
 */
@Data
public class BaseModel  implements Serializable {
    private String userId;
    private String token;
    private String roleId;
    private String clientType;
}
