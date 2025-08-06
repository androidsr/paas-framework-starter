package paas.framework.model.pagination;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 分页对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageInfo implements Serializable {
    /**
     * 当前页
     *
     * @mock 1
     */
    private Long current;
    /**
     * 分页大小
     * @mock 10
     */
    private Long size;

    /**
     * 排序规划
     */
    private List<OrderItem> orders;
}
