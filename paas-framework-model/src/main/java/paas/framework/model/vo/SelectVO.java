package paas.framework.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import paas.framework.model.model.TreeModel;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectVO implements Serializable, TreeModel {
    /**
     * 数据
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

    private List<TreeModel> children;

    @Override
    public void setId(Serializable id) {
        this.value = (String) id;
    }

    @Override
    public void setParentId(Serializable parentId) {
        this.supperId = (String) parentId;
    }

    @Override
    @JsonIgnore
    public Serializable getId() {
        return value;
    }

    @Override
    @JsonIgnore
    public Serializable getParentId() {
        return supperId;
    }

    @Override
    public void setChildren(List<TreeModel> data) {
        this.children = data;
    }
}
