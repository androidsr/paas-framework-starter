package paas.framework.tools;


import paas.framework.model.model.TreeModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeUtils {
    /**
     * 集合转换tree树
     *
     * @param model   跟节点对象
     * @param allData 全部数据集合
     * @param
     * @return
     */
    public static List listToTreeData(Serializable parentId, List allData) {
        List result = new ArrayList();
        for (Object v : allData) {
            TreeModel item = (TreeModel) v;
            if (PaasUtils.nullToBlank(parentId).equals(PaasUtils.nullToBlank(item.getParentId()))) {
                item.setChildren(listToTreeData(item.getId(), allData));
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 集合转换tree树
     *
     * @param model   跟节点对象
     * @param allData 全部数据集合
     * @param
     * @return
     */
    public static List listToTreeData(TreeModel model, List allData) {
        List result = new ArrayList();
        for (Object v : allData) {
            TreeModel item = (TreeModel) v;
            if (PaasUtils.nullToBlank(model.getId()).equals(PaasUtils.nullToBlank(item.getParentId()))) {
                item.setChildren(listToTreeData(item, allData));
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 动态获取数据组装数
     *
     * @param model
     * @param callback
     * @return
     */
    public static List listToTreeCond(Serializable rootId, Callback callback) {
        List<TreeModel> data = callback.getData(rootId);
        for (TreeModel item : data) {
            List<TreeModel> sunData = listToTreeCond(item.getId(), callback);
            if (PaasUtils.isEmpty(sunData)) {
                continue;
            }
            item.setChildren(sunData);
        }
        return data;
    }

    /**
     * 动态获取数据组装数
     *
     * @param model
     * @param callback
     * @return
     */
    public static List listToTreeCond(TreeModel model, Callback callback) {
        List<TreeModel> data = callback.getData(model.getId());
        for (TreeModel item : data) {
            List<TreeModel> sunData = listToTreeCond(item, callback);
            if (PaasUtils.isEmpty(sunData)) {
                continue;
            }
            item.setChildren(sunData);
        }
        return data;
    }

    public interface Callback {
        List<TreeModel> getData(Serializable id);
    }
}
