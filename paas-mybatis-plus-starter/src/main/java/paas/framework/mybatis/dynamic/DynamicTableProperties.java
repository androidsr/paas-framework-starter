package paas.framework.mybatis.dynamic;

import java.util.ArrayList;
import java.util.List;


public class DynamicTableProperties {
    private static final ThreadLocal<String> TABLE_SUFFIX = new ThreadLocal<>();
    private static List<String> tableList = new ArrayList();

    public static void setDynamicTable(String table) {
        tableList.add(table.toUpperCase());
    }

    public static List<String> getTableList() {
        return tableList;
    }

    public static String getTableSuffix() {
        return TABLE_SUFFIX.get();
    }

    public static void setTableSuffix(String requestData) {
        TABLE_SUFFIX.set(requestData);
    }
}