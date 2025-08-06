package paas.framework.model.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetryRecord implements Serializable {
    private Serializable id;
    private String topic;
    private Integer partition;
    private Long timestamp;
    private Object key;
    private Object value;
    private Map<String, byte[]> header;
}
