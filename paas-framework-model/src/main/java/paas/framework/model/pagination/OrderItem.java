package paas.framework.model.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {

    /**
     * 需要进行排序的字段
     * @mock id
     */
    private String column;

    /**
     * 是否正序排列，默认 true
     */
    private boolean asc = true;

}
