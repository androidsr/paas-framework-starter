package paas.framework.tools;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class EasyListListener extends AnalysisEventListener {
    List<List> data = new ArrayList<>();
    private String sheetName;

    @Override
    public void invoke(Object data, AnalysisContext context) {
        if (PaasUtils.isEmpty(sheetName)) {
            sheetName = context.readSheetHolder().getSheetName();
        }
        List item = new ArrayList();
        item.addAll(((LinkedHashMap) data).values());
        this.data.add(item);
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据读取完毕");
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<List> getData() {
        return data;
    }
}