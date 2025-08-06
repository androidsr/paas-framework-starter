package paas.framework.tools;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelUtil {

    /**
     * 读取excel数据
     *
     * @param inputStream   数据文件
     * @param cls           FastExcel实体对象
     * @param callback      回调函数
     * @param headRowNumber 标题所在行
     */
    public static <T> void readExcel(InputStream inputStream, Class<T> cls, EasyListener.ReadCallback callback, Integer headRowNumber) {
        EasyExcel.read(inputStream, cls, new EasyListener(callback)).sheet().headRowNumber(headRowNumber == null ? 1 : headRowNumber).doRead();
    }

    /**
     * 读取excel数据
     *
     * @param inputStream 数据文件
     * @param cls         FastExcel实体对象
     * @param callback    回调函数
     */
    public static <T> void readExcel(InputStream inputStream, Class<T> cls, EasyListener.ReadCallback callback) {
        EasyExcel.read(inputStream, cls, new EasyListener(callback)).sheet().doRead();
    }

    /**
     * 读取excel数据
     *
     * @param inputStream 数据文件
     * @param cls         FastExcel实体对象
     * @param callback    回调函数
     */
    public static List<Map<String, Object>> readExcel(InputStream inputStream, int headRowNumber, int skipLastRows, int skipFirstColumns) {
        SkipRowsAndColumnsListener listener = new SkipRowsAndColumnsListener(headRowNumber, skipLastRows, skipFirstColumns);
        EasyExcel.read(inputStream, listener).sheet().doRead();
        return listener.getDataList();
    }

    /**
     * 读取excel数据
     *
     * @param inputStream 数据文件
     * @param cls         FastExcel实体对象
     * @param callback    回调函数
     */
    public static List<Map<String, Object>> readExcel(InputStream inputStream, String sheet, int headRowNumber, int skipLastRows, int skipFirstColumns) {
        SkipRowsAndColumnsListener listener = new SkipRowsAndColumnsListener(headRowNumber, skipLastRows, skipFirstColumns);
        EasyExcel.read(inputStream, listener).sheet(sheet).doRead();
        return listener.getDataList();
    }

    /**
     * 读取excel数据
     *
     * @param inputStream 数据文件
     * @param cls         FastExcel实体对象
     * @param callback    回调函数
     */
    public static List<List> readExcel(InputStream inputStream, int headRowNumber) {
        EasyListListener listener = new EasyListListener();
        EasyExcel.read(inputStream, listener).headRowNumber(headRowNumber).sheet().doRead();
        return listener.getData();
    }

    /**
     * @param inputStream   数据文件
     * @param cls           FastExcel实体对象
     * @param callback      回调函数
     * @param headRowNumber 标题所在行
     * @param sheetNos      要读取的shell编号
     */
    public static <T> void readExcelBySheet(InputStream inputStream, Class<T> cls, EasyListener.ReadCallback callback, Integer headRowNumber, int... sheetNos) {
        List<ReadSheet> sheetList = new ArrayList<>();
        ExcelReader excelReader = EasyExcel.read(inputStream).headRowNumber(headRowNumber).build();
        try {
            for (int sheetNo : sheetNos) {
                sheetList.add(EasyExcel.readSheet(sheetNo).head(cls).registerReadListener(new EasyListener(callback)).build());
            }
            excelReader.read(sheetList.toArray(new ReadSheet[]{}));
        } catch (Exception e) {
            throw new BusException(ResultMessage.FAIL, e.getMessage());
        } finally {
            excelReader.finish();
        }
    }

    /**
     * http excel写入
     *
     * @param response http响应对象
     * @param fileName 下载文件名称
     * @param clazz    easy报表对象
     * @param data     数据
     * @throws Exception
     */
    public static <T> void writeExcel(HttpServletResponse response, String fileName, Class<T> clazz, List<T> data, WriteHandler writeHandler) {
        try {

            ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.write(getOutputStream(fileName, response), clazz).sheet().registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).registerWriteHandler(getHorizontalCellStyleStrategy());//.registerWriteHandler(new CustomCellStyleHandler());
            if (writeHandler != null) {
                writerSheetBuilder.registerWriteHandler(writeHandler).useDefaultStyle(true).relativeHeadRowIndex(2);
            }
            writerSheetBuilder.doWrite(data);
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static HorizontalCellStyleStrategy getHorizontalCellStyleStrategy() {
        //表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠左对齐
        // 设置边框
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);     // 上边框
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);  // 下边框
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);    // 左边框
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);   // 右边框
        // 可以根据需要设置其他样式
        // 设置居中
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    public static void writeExcel(HttpServletResponse response, String fileName, List<Map<String, Object>> head,
                                  List<Map<String, Object>> data, RowWriteHandler rowWriteHandler) {
        writeExcel(response, fileName, head, data, rowWriteHandler, 2);
    }

    public static void writeExcel(HttpServletResponse response, String fileName, List<Map<String, Object>> head,
                                  List<Map<String, Object>> data, RowWriteHandler rowWriteHandler, int relativeHeadRowIndex) {
        try {
            List<String> keyTitle = new ArrayList<>();
            List<List<String>> nameTitle = new ArrayList<>();
            for (Map<String, Object> item : head) {
                keyTitle.add(PaasUtils.nullToBlank(item.get("key")));
                nameTitle.add(Arrays.asList(PaasUtils.nullToBlank(item.get("name"))));
            }
            EasyExcel.write(getOutputStream(fileName, response)).sheet("sheet1")
                    .head(nameTitle)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).registerWriteHandler(getHorizontalCellStyleStrategy())
                    .registerWriteHandler(rowWriteHandler).useDefaultStyle(true).relativeHeadRowIndex(relativeHeadRowIndex)
                    .doWrite(dataList(data, keyTitle));
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void writeExcel(HttpServletResponse response, String fileName, Class<T> clazz, List<T> data) {
        writeExcel(response, fileName, clazz, data, null);
    }

    public static <T> void writeExcel(HttpServletResponse response, String fileName, List<String> head, List<Map<String, Object>> data) {
        try {
            EasyExcel.write(getOutputStream(fileName, response)).head(head.stream().map(v -> Arrays.asList(v)).collect(Collectors.toList())).sheet().registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).registerWriteHandler(getHorizontalCellStyleStrategy())
                    //.registerWriteHandler(new CustomCellStyleHandler())
                    .doWrite(dataList(data, head));
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void writeExcel(OutputStream outputStream, Class<T> clazz, List<T> data, WriteHandler writeHandler) {
        try {
            ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.write(outputStream, clazz).sheet().registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())  // 自动列宽处理器
                    .registerWriteHandler(getHorizontalCellStyleStrategy());
            //.registerWriteHandler(new CustomCellStyleHandler());
            if (writeHandler != null) {
                writerSheetBuilder.registerWriteHandler(writeHandler).useDefaultStyle(true).relativeHeadRowIndex(2);
            }
            writerSheetBuilder.doWrite(data);
        } catch (Exception e) {
            BusException.fail(5000, "导出excel出错！");
        }
    }

    public static <T> void writeExcel(OutputStream outputStream, Class<T> clazz, List<T> data) {
        writeExcel(outputStream, clazz, data, null);
    }

    /**
     * http excel写入
     *
     * @param response http响应对象
     * @param fileName 下载文件名称
     * @param clazz    easy报表对象
     * @param callback 数据写入操作
     */
    public static <T> void writeExcel(HttpServletResponse response, String fileName, Class<T> clazz, EasyListener.WriteCallback callback, WriteHandler writeHandler) {
        try {
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(getOutputStream(fileName, response), clazz).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())  // 自动列宽处理器
                    .registerWriteHandler(getHorizontalCellStyleStrategy());//.registerWriteHandler(new CustomCellStyleHandler());
            if (writeHandler != null) {
                excelWriterBuilder.registerWriteHandler(writeHandler).useDefaultStyle(true).relativeHeadRowIndex(2);
            }
            ExcelWriter excelWriter = excelWriterBuilder.build();
            callback.load(excelWriter);
            excelWriter.finish();
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void writeExcel(HttpServletResponse response, String fileName, Class<T> clazz, EasyListener.WriteCallback callback) {
        writeExcel(response, fileName, clazz, callback, null);
    }

    public static OutputStream getOutputStream(String fileName, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            if (!fileName.endsWith(".xlsx")) {
                fileName += ".xlsx";
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Access-Control-Expose-Headers", "Content-disposition");
            return response.getOutputStream();
        } catch (Exception e) {
            throw new BusException(ResultMessage.FAIL, e.getMessage());
        }
    }

    //设置导出的数据内容
    private static List<List<Object>> dataList(List<Map<String, Object>> dataList, List<String> dataStrMap) {
        List<List<Object>> list = new ArrayList<>();
        for (Map<String, Object> map : dataList) {
            List<Object> data = new ArrayList<>();
            for (int i = 0; i < dataStrMap.size(); i++) {
                data.add(map.get(dataStrMap.get(i)));
            }
            list.add(data);
        }
        return list;
    }
}
