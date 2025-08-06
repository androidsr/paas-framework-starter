package paas.framework.flink.cdc.event;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import paas.framework.flink.cdc.model.MysqlModel;

import java.io.Serializable;

public abstract class MysqlEventHandler implements Serializable {

    /**
     * 初始化全量数据
     */
    public void startFull(String dbName, String tableName, JsonNode afterData, JsonNode beforeData) {

    }

    /**
     * 自定义处理
     */
    public void any(MysqlModel data) {

    }

    /**
     * 插入
     */
    public abstract void insert(String dbName, String tableName, JsonNode jsonObject);

    /**
     * 更新
     */
    public abstract void update(String dbName, String tableName, JsonNode afterData, JsonNode beforeData);

    /**
     * 删除
     */
    public abstract void delete(String dbName, String tableName, JsonNode beforeData);

}
