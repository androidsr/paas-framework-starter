package paas.framework.flink.cdc.decoder;


import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import paas.framework.flink.cdc.event.MysqlEventHandler;
import paas.framework.flink.cdc.model.MysqlModel;
import paas.framework.tools.JSON;

/**
 * mysql 数据解码处理
 */
public class MysqlDecoder extends RichSinkFunction<String> {
    private final MysqlEventHandler event;

    public MysqlDecoder(MysqlEventHandler event) {
        this.event = event;
    }

    @Override
    public void invoke(String json, Context context) {
        MysqlModel data = JSON.parseObject(json, MysqlModel.class);
        event.any(data);
        if (data.getOp() == null || data.getOp().equals("")) {
            return;
        }
        String dbName = data.getSource().getDb();
        String tableName = data.getSource().getTable();
        switch (data.getOp()) {
            case "r":
                event.startFull(dbName, tableName, data.getAfter(), data.getBefore());
                break;
            case "u":
                event.update(dbName, tableName, data.getAfter(), data.getBefore());
                break;
            case "d":
                event.delete(dbName, tableName, data.getBefore());
                break;
            case "c":
                event.insert(dbName, tableName, data.getAfter());
                break;
        }
    }
}
