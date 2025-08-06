package paas.framework.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("swagger")
public class SwaggerProperties {
    /**
     * 是否开启swagger，生产环境一般关闭，所以这里定义一个变量
     */
    private Boolean enable;

    /**
     * 项目应用名
     */
    private String appName;

    /**
     * 项目版本信息
     */
    private String appVersion;

    /**
     * 项目描述信息
     */
    private String appDesc;

    /**
     * 接口调试地址
     */
    private String tryHost;
    /**
     * 联系人姓名
     */
    private String contactName;
    /**
     * 联系人邮箱
     */
    private String contactEmail;

}