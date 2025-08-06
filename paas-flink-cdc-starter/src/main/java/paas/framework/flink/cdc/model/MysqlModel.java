package paas.framework.flink.cdc.model;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.io.Serializable;

@Data
public class MysqlModel implements Serializable {
    /**
     * 更新前对象
     */
    private JsonNode before;
    /**
     * 更新后对象
     */
    private JsonNode after;
    /**
     * 操作类型（c:创建，u:更新，d:删除,r:重新读取）
     */
    public String op;
    public Long ts_ms;
    public String transaction;
    public Source source;
    public String historyRecord;

    @Data
    public static class Source {
        /**
         * flink版本
         */
        private String version;
        /**
         * 监听器类型
         */
        private String connector;
        /**
         * binlog名称
         */
        private String name;
        private long ts_ms;
        private String snapshot;
        /**
         * 数据库名称
         */
        private String db;
        private String sequence;
        /**
         * 表名称
         */
        private String table;
        private int server_id;
        private String gtid;
        private String file;
        private int pos;
        private int row;
        private String thread;
        private String query;
    }
}