package paas.framework.tools;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SkipRowsAndColumnsListener extends AnalysisEventListener<Map<Integer, String>> {

    private final List<Map<String, Object>> dataList = new ArrayList<>();  // 存储每一行的数据
    private final int skipFirstRows;    // 要跳过的前几行
    private final int skipLastRows;    // 要跳过的最后几行
    private final int skipFirstColumns; // 要跳过的前几列
    private final List<String> headers = new ArrayList<>(); // 存储实际的标题行

    public SkipRowsAndColumnsListener(int skipFirstRows, int skipLastRows, int skipFirstColumns) {
        this.skipFirstRows = skipFirstRows;
        this.skipLastRows = skipLastRows;
        this.skipFirstColumns = skipFirstColumns;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        // 跳过前几行
        if (context.readRowHolder().getRowIndex() < skipFirstRows) {
            return;
        }

        // 标题行应该是跳过指定行数后的第一行
        if (context.readRowHolder().getRowIndex() == skipFirstRows) {
            // 获取并保存标题行
            for (int i = 0; i < data.size(); i++) {
                String header = data.get(i);
                headers.add(header);
            }
        } else {
            // 处理数据行
            Map<String, Object> rowMap = new LinkedHashMap<>();
            for (int i = skipFirstColumns; i < data.size(); i++) {
                if (i >= headers.size()) {
                    break;
                }
                String cellStr = data.get(i);
                Object cellValue;
                if (PaasUtils.isNotEmpty(cellStr) && cellStr.contains(",") && isNumeric(cellStr.replace(",", ""))) {
                    cellValue = new BigDecimal(cellStr.replace(",", ""));
                } else {
                    cellValue = cellStr;
                }
                String header = headers.get(i - skipFirstColumns);
                rowMap.put(header, cellValue);
            }
            dataList.add(rowMap);
        }
    }

    // 判断字符串是否为数值
    private boolean isNumeric(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 删除最后几行
        for (int i = 0; i < skipLastRows; i++) {
            if (!dataList.isEmpty()) {
                dataList.remove(dataList.size() - 1); // 删除最后一行数据
            }
        }
    }

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }
}
