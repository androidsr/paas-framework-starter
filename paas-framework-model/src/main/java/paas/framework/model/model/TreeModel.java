package paas.framework.model.model;

import java.io.Serializable;
import java.util.List;

public interface TreeModel {
    void setId(Serializable id);

    void setParentId(Serializable parentId);

    Serializable getId();

    Serializable getParentId();

    void setChildren(List<TreeModel> data);
}
