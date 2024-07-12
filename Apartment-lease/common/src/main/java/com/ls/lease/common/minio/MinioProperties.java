package com.ls.lease.common.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置参数类
 * @ConfigurationProperties(prefix = "minio")
 * 根据类的属性自动映射，yaml文件中minio下的属性值。
 * 注意要在配置类上开启配置参数类
 *
 */

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties{
     private String endpoint;
     private String accessKey;
     private String secretKey;

     private String bucketName;

}
