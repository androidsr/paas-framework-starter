package paas.framework.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import paas.framework.web.formatter.StringToName;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity extends IdEntity implements Serializable {
    /**
     * @ignore 创建时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * @ignore 更新时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * @ignore 创建者
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(fill = FieldFill.INSERT)
    @StringToName(key = "SysUsers:codeName", setField = "createName")
    private String createBy;
    /**
     * @ignore 更新者
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @StringToName(key = "SysUsers:codeName", setField = "updateName")
    private String updateBy;

    /**
     * @ignore 租户ID
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;
}
