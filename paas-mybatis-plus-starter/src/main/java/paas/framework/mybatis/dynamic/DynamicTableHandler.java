package paas.framework.mybatis.dynamic;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;
import paas.framework.tools.PaasUtils;
import paas.framework.web.WebUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicTableHandler implements TableNameHandler {
    private Map<String, String> tableConfig;

    public DynamicTableHandler(List<String> tables) {
        tableConfig = new HashMap<>();
        for (String item : tables) {
            String[] conf = item.split(":");
            if (conf.length != 2) {
                throw new BusException(ResultMessage.SYSTEM_CONFIG_ERROR);
            }
            tableConfig.put(conf[0].toUpperCase(), conf[1]);
        }
    }

    @Override
    public String dynamicTableName(String sql, String tableName) {
        if (sql.toLowerCase().startsWith("create table")) {
            return tableName;
        }
        if (!tableConfig.containsKey(tableName.toUpperCase())) {
            return tableName;
        }
        //自动移除 DynamicTableFilter
        String suffix = DynamicTableConfig.get();
        if (PaasUtils.isEmpty(suffix)) {
            suffix = getSuffix(tableConfig.get(tableName.toUpperCase()));
        }

        if (PaasUtils.isNotEmpty(suffix)) {
            return String.join("_", tableName, suffix);
        } else {
            throw new BusException(ResultMessage.SYSTEM_CONFIG_ERROR);
        }
    }

    private static String getSuffix(String spl) {
        if (spl.startsWith("#header.")) {
            String key = spl.split("\\.")[1];
            String value = WebUtils.getHeaderValue(key);
            DynamicTableConfig.set(value);
            return value;
        } else if (spl.startsWith("#date.")) {
            String key = spl.split("\\.")[1];
            SimpleDateFormat sdf = new SimpleDateFormat(key);
            return sdf.format(new Date());
        }
        return "";
    }
}
