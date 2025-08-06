package paas.framework.model.dto;

import paas.framework.model.enums.BindActionEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class LongBindDTO implements Serializable {
    /**
     * 操作类型
     */
    private BindActionEnum action;
    /**
     * 主数据ID
     */
    private Long masterId;
    /**
     * 关联数据ID
     */
    private Set<Long> relevanceIds;
}
