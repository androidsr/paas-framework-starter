package paas.framework.minio;

import io.minio.*;
import io.minio.errors.ServerException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Part;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;
import paas.framework.model.vo.UploadResult;
import paas.framework.tools.PaasUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class MinioService {

    @Value("${minio.bucketName}")
    String defaultBucketName;

    @Autowired
    CustomMinioClient customMinioClient;

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    @SneakyThrows
    public void createBucket(String bucketName) {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        if (!bucketExists(bucketName)) {
            customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 获取全部bucket
     * <p>
     */
    @SneakyThrows
    public List<Bucket> getAllBuckets() {
        return customMinioClient.listBuckets();
    }

    /**
     * 判断bucketName是否存在
     */
    @SneakyThrows
    public Boolean bucketExists(String bucketName) {
        BucketExistsArgs.Builder bucket = BucketExistsArgs.builder().bucket(bucketName);
        return customMinioClient.bucketExists(bucket.build());
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    @SneakyThrows
    public void removeBucket(String bucketName) {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        customMinioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 删除文件
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        customMinioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 上传文件
     *
     * @param file 文件集合
     * @return
     * @throws ServerException
     */
    public List<UploadResult> uploadFile(MultipartFile[] file, String bucketName, String group) throws ServerException {
        if (file == null || file.length == 0) {
            BusException.fail(ResultMessage.PARAMETER_ERROR);
        }

        List<UploadResult> fileUrlList = new ArrayList<>(file.length);
        String url;
        for (MultipartFile multipartFile : file) {
            String originalFilename = multipartFile.getOriginalFilename();
            try {
                url = uploadFile(multipartFile.getInputStream(), bucketName, group, originalFilename.substring(originalFilename.lastIndexOf(".")), multipartFile.getContentType());
                fileUrlList.add(UploadResult.builder().result(true).originalName(originalFilename).newName(url).build());
            } catch (Exception e) {
                e.printStackTrace();
                fileUrlList.add(UploadResult.builder().result(false).originalName(originalFilename).build());
            }
        }
        return fileUrlList;
    }

    /**
     * 默认根路径文件上传
     *
     * @param file
     * @return
     * @throws ServerException
     */
    public String uploadFile(MultipartFile file, String group) throws ServerException {
        return this.uploadFile(file, null, group);
    }

    /**
     * 文件上传
     *
     * @param file
     * @param bucketName
     * @return
     * @throws ServerException
     */
    public String uploadFile(MultipartFile file, String bucketName, String group) throws ServerException {
        if (file == null) {
            BusException.fail(ResultMessage.PARAMETER_ERROR);
        }
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        String originalFilename = file.getOriginalFilename();
        try {
            return uploadFile(file.getInputStream(), bucketName, group, originalFilename.substring(originalFilename.lastIndexOf(".")), file.getContentType());
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.UNKNOW_ERROR);
        }
        return null;
    }

    /**
     * 默认根路径多上传文件
     *
     * @param file
     * @return
     * @throws ServerException
     */
    public List<UploadResult> uploadFile(MultipartFile[] file, String group) throws ServerException {
        return this.uploadFile(file, defaultBucketName, group);
    }

    /**
     * 文件流上传
     *
     * @param in         文件流
     * @param bucketName 根目录
     * @param suffix     后缀
     * @return
     */
    public String uploadFile(InputStream in, String bucketName, String group, String suffix) {
        return uploadFile(in, bucketName, group, suffix, "application/octet-stream");
    }

    /**
     * 默认根路径文件流上传
     *
     * @param in     文件数据流信息
     * @param suffix 文件名或后缀
     * @return
     */
    public String uploadFile(InputStream in, String group, String suffix) {
        return uploadFile(in, defaultBucketName, group, suffix, "application/octet-stream");
    }

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
    public String uploadFile(InputStream in, String bucketName, String group, String suffix, String contextType) {
        if (in == null || PaasUtils.isEmpty(suffix)) {
            BusException.fail(ResultMessage.PARAMETER_ERROR);
        }
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        if (!suffix.startsWith(".")) {
            suffix = suffix.substring(suffix.lastIndexOf("."));
        }
        String newFilename = String.join("/", group, UUID.randomUUID().toString().replace("-", "") + suffix);
        try {
            customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(newFilename).stream(in, in.available(), -1).contentType(contextType).build());
            in.close();
            return newFilename;
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.UNKNOW_ERROR);
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param in           文件流信息
     * @param bucketName   根目录
     * @param saveFileName 保存文件名
     * @return
     */
    public String upload(InputStream in, String bucketName, String saveFileName) {
        if (in == null || PaasUtils.isEmpty(saveFileName)) {
            BusException.fail(ResultMessage.PARAMETER_ERROR);
        }
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        try {
            customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(saveFileName).stream(in, in.available(), -1).contentType("application/octet-stream").build());
            in.close();
            return saveFileName;
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.UNKNOW_ERROR);
        }
        return null;
    }

    /**
     * 默认根路径文件下载
     *
     * @param fileName
     * @return 文件流
     * @throws BusException
     */
    public InputStream download(String fileName) throws BusException {
        return download(null, fileName);
    }

    /**
     * 文件下载
     *
     * @param bucketName
     * @param fileName
     * @return 文件流
     * @throws BusException
     */
    public InputStream download(String bucketName, String fileName) throws BusException {
        try {
            if (PaasUtils.isEmpty(bucketName)) {
                bucketName = defaultBucketName;
            }
            return customMinioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.UNKNOW_ERROR);
        }
        return null;
    }

    /**
     * 获取文件信息
     *
     * @param bucketName
     * @param fileName
     * @return
     * @throws BusException
     */
    public StatObjectResponse statObject(String bucketName, String fileName) throws BusException {
        try {
            if (PaasUtils.isEmpty(bucketName)) {
                bucketName = defaultBucketName;
            }
            return customMinioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.UNKNOW_ERROR);
        }
        return null;
    }

    /**
     * 默认根路径文件下载
     *
     * @param fileName 文件名称
     * @return
     * @throws BusException
     */
    public ByteArrayOutputStream bytesDownload(String fileName) throws BusException {
        return bytesDownload(null, fileName);
    }

    /**
     * 文件下载
     *
     * @param fileName
     * @return 文件内容数组
     * @throws BusException
     */
    public ByteArrayOutputStream bytesDownload(String bucketName, String fileName) throws BusException {
        InputStream inputStream = null;
        try {
            inputStream = download(bucketName, fileName);

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            return outStream;
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.UNKNOW_ERROR);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * 默认根路径文件下载
     *
     * @param fileName
     * @param response
     * @return
     */
    public boolean httpDownload(String fileName, HttpServletResponse response) {
        return httpDownload(null, fileName, response);
    }

    /**
     * http文件下载
     *
     * @param fileName 文件名
     * @param response http对象
     * @return 直接响应前端
     */
    public boolean httpDownload(String bucketName, String fileName, HttpServletResponse response) {
        InputStream inputStream = null;
        try {
            inputStream = download(bucketName, fileName);
            if (inputStream == null) {
                return false;
            }
            fileName = new String(fileName.getBytes(), "UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(inputStream, response.getOutputStream());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.UNKNOW_ERROR);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
        return false;
    }

    /**
     * 获取签名上传地址
     *
     * @param bucketName 存储桶
     * @param filename   文件名
     * @param expiry     到期时间（秒）
     * @return
     */
    public String getObjectUrl(String bucketName, String filename, int expiry) {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        try {
            String url = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.PUT).bucket(bucketName).object(filename).expiry(expiry, TimeUnit.MINUTES).build());
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取签名上传地址
     *
     * @param bucketName 存储桶
     * @param filename   文件名
     * @param expiry     到期时间（秒）
     * @return
     */
    public String getObjectUrl(String bucketName, String filename, Map<String, String> reqParams, int expiry) {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        try {
            String url = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.PUT).bucket(bucketName).object(filename).expiry(expiry, TimeUnit.MINUTES).extraQueryParams(reqParams).build());
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件下载地址
     *
     * @param bucketName
     * @param filename
     * @param expiry
     * @return
     */
    public String getDownUrl(String bucketName, String filename, int expiry) {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        try {
            String url = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).object(filename).expiry(expiry, TimeUnit.MINUTES).build());
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getDownUrl(String bucketName, String filename, String originalName, int expiry) {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        try {
            String url = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).object(filename).expiry(expiry, TimeUnit.MINUTES).extraQueryParams(Map.of(
                    "response-content-disposition", "attachment; filename=\"" + originalName + "\""
            )).build());
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分片文件初始化
     *
     * @param bucketName 目标桶
     * @param objectName 对象名称
     * @return uploadId
     */
    public String initiateMultipartUpload(String bucketName, String objectName) {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        try {
            return customMinioClient.initMultiPartUpload(bucketName, null, objectName, null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取上传分片列表
     *
     * @param bucketName 目标桶
     * @param objectName 对象名称
     * @return uploadId
     */
    public List<Part> listParts(String bucketName, String objectName, int maxParts, int partNumberMarker, String uploadId) {
        if (PaasUtils.isEmpty(bucketName)) {
            bucketName = defaultBucketName;
        }
        try {
            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, maxParts, partNumberMarker, uploadId, null, null);
            return partResult.result().partList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 分片上传完成合并文件
     *
     * @param bucketName 目标桶
     * @param objectName 对象名称
     * @param uploadId   分片上传初始化ID
     * @return uploadId
     */
    public ObjectWriteResponse mergeMultipartUpload(String bucketName, String objectName, String uploadId) {
        try {
            Part[] parts = listParts(bucketName, objectName, 1000, 0, uploadId).toArray(new Part[]{});
            return customMinioClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分片直传处理
     *
     * @param bucketName 目标桶
     * @param objectName 对象名称
     * @return uploadId
     */
    public DirectPartUploadVO directPartUpload(String bucketName, String objectName, int totalPart, int expiry) {
        DirectPartUploadVO result = new DirectPartUploadVO();

        String uploadId = initiateMultipartUpload(bucketName, objectName);
        result.setUploadId(uploadId);
        List<String> partList = new ArrayList<>();

        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("uploadId", uploadId);
        for (int i = 1; i <= totalPart; i++) {
            reqParams.put("partNumber", String.valueOf(i));
            String uploadUrl = getObjectUrl(bucketName, objectName, reqParams, expiry);
            partList.add(uploadUrl);
        }
        result.setObjectName(objectName);
        result.setUploadUrl(partList);
        return result;
    }

    /**
     * 生成文件名
     *
     * @param group  文件分组
     * @param suffix 文件后续
     */
    public String generateFilename(String group, String suffix) {
        if (!suffix.startsWith(".")) {
            suffix = suffix.substring(suffix.lastIndexOf("."));
        }
        String newFileName = String.join("/", group, UUID.randomUUID().toString().replace("-", "")) + suffix;
        return newFileName;
    }
}
