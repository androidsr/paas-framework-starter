package paas.framework.mybatis;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class DbToNameItem {
    private Field field;
    private DbToName annotation;
    private List<String> values;

    DbToNameItem() {
        values = new ArrayList<>();
    }

    public void addValue(String value) {
        values.add(value);
    }
}
