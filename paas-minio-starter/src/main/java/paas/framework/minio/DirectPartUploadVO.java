package paas.framework.minio;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分片文件直传响应结果
 */
@Data
public class DirectPartUploadVO implements Serializable {
    /**
     * 分片初始化ID
     */
    private String uploadId;
    /**
     * 分页文件上传地址集合
     */
    private List<String> uploadUrl;
    /**
     * 生成文件名
     */
    private String objectName;
}
