# MinIO文件存储
MinIO通过默认集成方式，对常用文件上传/下载文件进行了封装处理。
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-minio-starter</artifactId>
</dependency>
```
## yml配置
```yaml
minio:
  bucketName: 为项目分配的存储目录
  endpoint: ip
  port: 9000
  secure: false
  minioRootUser: admin
  minioRootPassword: admin@minio...
```

## 操作工具类
文件上传工具类支持spring mvc web文件和文件流对象进行上传。文件下载可下载文件流，byte数组对象，以及直接返回http浏览器对象结果。
```java

/**
 * 创建bucket
 *
 * @param bucketName bucket名称
 */
@SneakyThrows
public void createBucket(String bucketName) 

/**
 * 获取全部bucket
 * <p>
 */
@SneakyThrows
public List<Bucket> getAllBuckets() 

/**
 * 判断bucketName是否存在
 */
@SneakyThrows
public Boolean bucketExists(String bucketName) 

/**
 * 根据bucketName删除信息
 *
 * @param bucketName bucket名称
 */
@SneakyThrows
public void removeBucket(String bucketName) 

/**
 * 删除文件
 */
public void removeObject(String bucketName, String objectName) throws Exception 

/**
 * 上传文件
 *
 * @param file 文件集合
 * @return
 * @throws ServerException
 */
public List<UploadResult> uploadFile(MultipartFile[] file, String bucketName, String group) throws ServerException 

/**
 * 默认根路径文件上传
 *
 * @param file
 * @return
 * @throws ServerException
 */
public String uploadFile(MultipartFile file, String group) throws ServerException 

/**
 * 文件上传
 *
 * @param file
 * @param bucketName
 * @return
 * @throws ServerException
 */
public String uploadFile(MultipartFile file, String bucketName, String group) throws ServerException 

/**
 * 默认根路径多上传文件
 *
 * @param file
 * @return
 * @throws ServerException
 */
public List<UploadResult> uploadFile(MultipartFile[] file, String group) throws ServerException 
/**
 * 文件流上传
 *
 * @param in         文件流
 * @param bucketName 根目录
 * @param suffix     后缀
 * @return
 */
public String uploadFile(InputStream in, String bucketName, String group, String suffix) 

/**
 * 默认根路径文件流上传
 *
 * @param in     文件数据流信息
 * @param suffix 文件名或后缀
 * @return
 */
public String uploadFile(InputStream in, String group, String suffix) 

/**
 * 文件流上传
 *
 * @param in          文件流
 * @param bucketName  上传位置
 * @param group       分组
 * @param suffix      后缀
 * @param contextType 上下文类型
 * @return
 */
public String uploadFile(InputStream in, String bucketName, String group, String suffix, String contextType) 

/**
 * 上传文件
 *
 * @param in           文件流信息
 * @param bucketName   根目录
 * @param saveFileName 保存文件名
 * @return
 */
public String upload(InputStream in, String bucketName, String saveFileName) 

/**
 * 默认根路径文件下载
 *
 * @param fileName
 * @return 文件流
 * @throws BusException
 */
public InputStream download(String fileName) throws BusException 

/**
 * 文件下载
 *
 * @param bucketName
 * @param fileName
 * @return 文件流
 * @throws BusException
 */
public InputStream download(String bucketName, String fileName) throws BusException 

/**
 * 获取文件信息
 *
 * @param bucketName
 * @param fileName
 * @return
 * @throws BusException
 */
public StatObjectResponse statObject(String bucketName, String fileName) throws BusException 

/**
 * 默认根路径文件下载
 *
 * @param fileName 文件名称
 * @return
 * @throws BusException
 */
public ByteArrayOutputStream bytesDownload(String fileName) throws BusException 

/**
 * 文件下载
 *
 * @param fileName
 * @return 文件内容数组
 * @throws BusException
 */
public ByteArrayOutputStream bytesDownload(String bucketName, String fileName) throws BusException 

/**
 * 默认根路径文件下载
 *
 * @param fileName
 * @param response
 * @return
 */
public boolean httpDownload(String fileName, HttpServletResponse response) 

/**
 * http文件下载
 *
 * @param fileName 文件名
 * @param response http对象
 * @return 直接响应前端
 */
public boolean httpDownload(String bucketName, String fileName, HttpServletResponse response) 

/**
 * 获取签名上传地址
 *
 * @param bucketName 存储桶
 * @param filename   文件名
 * @param expiry     到期时间（秒）
 * @return
 */
public String getObjectUrl(String bucketName, String filename, int expiry)


/**
 * 获取签名上传地址
 *
 * @param bucketName 存储桶
 * @param filename   文件名
 * @param expiry     到期时间（秒）
 * @return
 */
public String getObjectUrl(String bucketName, String filename, Map<String, String> reqParams, int expiry) 

/**
 * 获取文件下载地址
 *
 * @param bucketName
 * @param filename
 * @param expiry
 * @return
 */
public String getDownUrl(String bucketName, String filename, int expiry) 

/**
 * 分片文件初始化
 *
 * @param bucketName 目标桶
 * @param objectName 对象名称
 * @return uploadId
 */
public String initiateMultipartUpload(String bucketName, String objectName) 

/**
 * 获取上传分片列表
 *
 * @param bucketName 目标桶
 * @param objectName 对象名称
 * @return uploadId
 */
public List<Part> listParts(String bucketName, String objectName, int maxParts, int partNumberMarker, String uploadId) 


/**
 * 分片上传完成合并文件
 *
 * @param bucketName 目标桶
 * @param objectName 对象名称
 * @param uploadId   分片上传初始化ID
 * @return uploadId
 */
public ObjectWriteResponse mergeMultipartUpload(String bucketName, String objectName, String uploadId) 

/**
 * 分片直传处理
 *
 * @param bucketName 目标桶
 * @param objectName 对象名称
 * @return uploadId
 */
public DirectPartUploadVO directPartUpload(String bucketName, String objectName, int totalPart, int expiry) 

/**
 * 生成文件名
 *
 * @param group  文件分组
 * @param suffix 文件后续
 */
public String generateFilename(String group, String suffix) 
```
## 示例代码
```java
@Autowired
MinioService minioService;
```
文件上传默认会在指定分组目录下进行年月日子目录并生成UUID，分组id一般为不同业务进行目录隔离。
```java
String savePath = minioService.uploadFile(inputStream, groupid, fileName);

ByteArrayOutputStream out = minioService.bytesDownload(vo.getContent());

String savePath = minioService.uploadFile(inputStream, bucketName, ".html");

InputStream dataStream = minioService.download(bucketName,savePath);
FileOutputStream out = new FileOutputStream("D:\\xxx\\xxx.html");

```
