package paas.framework.tools;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

public class StatisticsRowWriteHandler<T> implements RowWriteHandler {

    private final List<T> dataList;
    private final String title;
    private final Field[] fields;

    public StatisticsRowWriteHandler(String title, List<T> dataList, Class<T> clazz) {
        this.title = title;
        this.dataList = dataList;
        this.fields = clazz.getDeclaredFields();
    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        // 插入表名到第一行
        if (relativeRowIndex == 0 && isHead) {
            // 创建标题行
            Row titleRow = writeSheetHolder.getSheet().createRow(0);

            CellStyle titleStyle = createStyle(writeSheetHolder);
            Font font = writeSheetHolder.getSheet().getWorkbook().createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 16);
            titleStyle.setFont(font);
            writeSheetHolder.getSheet().addMergedRegion(new CellRangeAddress(0, 0, 0, fields.length - 1));

            for (int i = 0; i < fields.length; i++) {
                Cell cell = titleRow.createCell(i);
                cell.setCellStyle(titleStyle);
            }
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);


            // 创建统计行
            Row statRow = writeSheetHolder.getSheet().createRow(1);
            CellStyle cellStyle = createStyle(writeSheetHolder);
            Font statFont = writeSheetHolder.getSheet().getWorkbook().createFont();
            statFont.setBold(true);
            statFont.setFontHeightInPoints((short) 14);
            cellStyle.setFont(statFont);
            for (int i = 0; i < fields.length; i++) {
                Cell cell = statRow.createCell(i);
                cell.setCellStyle(cellStyle);
            }
            try {
                int cellIndex = 0;
                int firstStatColumn = -1;

                for (Field field : fields) {
                    field.setAccessible(true);
                    Statistics statAnnotation = field.getAnnotation(Statistics.class);
                    if (statAnnotation != null && statAnnotation.type().equals("sum")) {
                        if (firstStatColumn == -1) {
                            firstStatColumn = cellIndex;  // 记录第一个统计列
                        }
                        BigDecimal sum = new BigDecimal("0.0");
                        for (T data : dataList) {
                            Object object = field.get(data);
                            if (object instanceof Integer value) {
                                sum = sum.add(new BigDecimal(value));
                            } else if (object instanceof Long value) {
                                sum = sum.add(new BigDecimal(value));
                            } else if (object instanceof Float value) {
                                sum = sum.add(new BigDecimal(value));
                            } else if (object instanceof Double value) {
                                sum = sum.add(new BigDecimal(value));
                            } else if (object instanceof BigDecimal value) {
                                sum = sum.add(value);
                            }
                        }
                        Cell cell = statRow.createCell(cellIndex);  // 写入统计数据
                        cell.setCellValue(sum.toPlainString());
                        cell.setCellStyle(createStyle(writeSheetHolder));
                    }
                    cellIndex++;
                }
                writeSheetHolder.getSheet().addMergedRegion(new CellRangeAddress(1, 1, 0, firstStatColumn - 1));
                if (firstStatColumn > 0) {
                    Cell mergedCell = statRow.createCell(0);
                    mergedCell.setCellValue("统计");
                    mergedCell.setCellStyle(cellStyle);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private CellStyle createStyle(WriteSheetHolder writeSheetHolder) {
        CellStyle cellStyle = writeSheetHolder.getSheet().getWorkbook().createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }
}
