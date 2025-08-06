package paas.framework.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: BaseEntity
 * @author: sirui
 * @date: 2021/11/20 17:11
 */
@Data
public class IdEntity extends EmptyEntity implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

}