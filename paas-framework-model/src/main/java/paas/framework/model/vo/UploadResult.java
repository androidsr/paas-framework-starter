package paas.framework.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件上传响应
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResult implements Serializable {
    /**
     * 文件上传结果
     */
    private Boolean result;
    /**
     * 原始文件名
     */
    private String originalName = "";
    /**
     * 新文件名
     */
    private String newName = "";
}
