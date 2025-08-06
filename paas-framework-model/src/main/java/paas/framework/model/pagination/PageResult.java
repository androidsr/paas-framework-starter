package paas.framework.model.pagination;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 *
 * @param <T>
 */
@Data
@ToString
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    /**
     * 当前第几页,从1开始
     */
    private int current;
    /**
     * 每页条数
     */
    private int size;

    /**
     * 总条数
     */
    private int total;
    /**
     * 响应数据
     */
    private List<T> rows;
}
