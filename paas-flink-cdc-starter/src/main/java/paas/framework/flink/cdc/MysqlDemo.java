/*
package paas.framework.flink.cdc;


import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import paas.framework.flink.cdc.event.MysqlEventHandler;
import paas.framework.flink.cdc.monitor.MysqlMonitor;
import paas.framework.flink.cdc.utils.ValueUtils;

import java.util.HashSet;
import java.util.Set;

public class MysqlDemo {
    public static void main(String[] args) throws Exception {
        MysqlEventHandler event = new MysqlEventHandler() {
            @Override
            public void insert(String dbName, String tableName, JSONObject data) {
                System.out.println(String.format("插入数据库：%s；表名：%s；数据：%s；", dbName, tableName, data));
            }

            @Override
            public void update(String dbName, String tableName, JSONObject afterData, JSONObject beforeData) {
                JSONObject jsonObject = ValueUtils.diffJSONObjects(afterData, beforeData);
                System.out.println(jsonObject);
            }

            @Override
            public void delete(String dbName, String tableName, JSONObject data) {
                System.out.println(String.format("删除数据库：%s；表名：%s；数据：%s；", dbName, tableName, data));
            }
        };
        Set<String> databases = new HashSet<>();
        databases.add("scswl_eoss");

        Set<String> tables = new HashSet<>();
        tables.add("scswl_eoss.fs_quota_log");
        MysqlMonitor sourcelReader = new MysqlMonitor("172.16.9.19", 3306, databases,
                tables, "root", "wisesoft", StartupOptions.latest(), event);

        sourcelReader.execute();
    }
}
*/
