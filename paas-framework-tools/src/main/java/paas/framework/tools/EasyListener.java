package paas.framework.tools;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;

import java.util.ArrayList;
import java.util.List;

public class EasyListener<T> extends AnalysisEventListener<T> {
    private static final int BATCH_COUNT = 400;
    List<T> list = new ArrayList<>();
    private ReadCallback readCallback;
    private String sheetName;

    public EasyListener(ReadCallback readCallback) {
        this.readCallback = readCallback;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        if (PaasUtils.isEmpty(sheetName)) {
            sheetName = context.readSheetHolder().getSheetName();
        }
        list.add(data);
        if (list.size() >= BATCH_COUNT) {
            if (readCallback.save(list)) {
                list.clear();
            } else {
                throw new BusException(ResultMessage.FAIL, "EasyExcel逻辑中断");
            }
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        readCallback.save(list);
    }

    public String getSheetName() {
        return sheetName;
    }

    public interface ReadCallback<T> {
        boolean save(List<T> list);
    }

    public interface WriteCallback<T> {
        boolean load(ExcelWriter excelWriter);
    }
}