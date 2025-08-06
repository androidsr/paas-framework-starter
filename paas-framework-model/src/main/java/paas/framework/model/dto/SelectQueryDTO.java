package paas.framework.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import paas.framework.model.pagination.PageInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 选择器查询条件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectQueryDTO implements Serializable {

    /**
     * 分页参数
     */
    private PageInfo page;

    /**
     * 选中值
     */
    private String value;

    /**
     * 显示名称
     */
    private String label;

    /**
     * 上级ID
     */
    private String supperId;
    /**
     * 选中数据
     */
    private List selected;
    /**
     * @ignore 扩展条件
     */
    Map<String, Object> vars;
}
