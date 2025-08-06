package paas.framework.flink.cdc.monitor;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import paas.framework.flink.cdc.decoder.MysqlDecoder;
import paas.framework.flink.cdc.event.MysqlEventHandler;

import java.util.Set;

public class MysqlMonitor {
    private final String hostname;
    private final int port;
    private final Set<String> databaseList;
    private final Set<String> tableList;
    private final String username;
    private final String password;
    private final StartupOptions options;
    private final DebeziumDeserializationSchema deserializer;
    private final StreamExecutionEnvironment env;
    private final Configuration configuration;
    private MysqlEventHandler event;

    public MysqlMonitor(String hostname, int port, Set<String> databaseList, Set<String> tableList, String username,
                        String password, StartupOptions options, MysqlEventHandler event) {
        this.hostname = hostname;
        this.port = port;
        this.databaseList = databaseList;
        this.tableList = tableList;
        this.username = username;
        this.password = password;
        this.options = options;
        this.event = event;
        configuration = new Configuration();
        /*if (webPort > 0) {
            configuration.setInteger(RestOptions.PORT, webPort);
        }*/
        deserializer = new JsonDebeziumDeserializationSchema();
        env = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
    }

    private MySqlSource createSource() {
        return MySqlSource.<String>builder().hostname(hostname).port(port).databaseList(databaseList.toArray(new String[0])).tableList(tableList.toArray(new String[0])).username(username).password(password).deserializer(deserializer).includeSchemaChanges(true).startupOptions(options).build();
    }

    /**
     * 启动监听
     *
     * @param checkpointing 检查点存储位置
     * @return
     * @throws Exception
     */
    public <T> void execute(String... checkpointDataUri) throws Exception {
        //检查点间隔时间,容错时写入失败从检查点开始重新处理
        String checkpoint;
        if (checkpointDataUri.length > 0) {
            checkpoint = checkpointDataUri[0];
        } else {
            checkpoint = "mysql-checkpoint-cdc";
        }
        env.setStateBackend(new HashMapStateBackend());
        env.getCheckpointConfig().setCheckpointStorage("file:///" + checkpoint);
        env.enableCheckpointing(5000L);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(10000L);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(5);
        env.setRestartStrategy(RestartStrategies.failureRateRestart(5, Time.seconds(60), Time.seconds(2)));
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);
        MysqlDecoder decoder = new MysqlDecoder(event);
        env.fromSource(createSource(), WatermarkStrategy.noWatermarks(), "default-mysql-binlog").addSink(decoder);
        env.execute();
    }
}
