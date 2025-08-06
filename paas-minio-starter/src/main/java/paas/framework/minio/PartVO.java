package paas.framework.minio;

import lombok.Data;

import java.io.Serializable;

@Data
public class PartVO implements Serializable {
    private Integer partNumber;
    private String eTag;
    private Long size;

}
