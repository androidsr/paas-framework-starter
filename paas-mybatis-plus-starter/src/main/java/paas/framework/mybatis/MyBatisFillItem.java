package paas.framework.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyBatisFillItem implements Serializable {
    private Object value;
    private Class valueType;
}
