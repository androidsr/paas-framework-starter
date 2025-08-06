package paas.framework.tools;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;

import javax.net.ssl.*;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * http 工具类
 *
 * @author sirui
 */
@Slf4j
public class OkHttpUtil {
    private static final String HTTPS = "https:";
    private static final int HTTP_OK = 200;

    /**
     * 自定义请求格式（公共）
     *
     * @param request okhttp请求信息定义
     * @return body string字符串，失败返回""
     */
    public static String newNet(Request request) {
        Response response = null;
        OkHttpClient client;
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS);
            if (request.url().url().toString().startsWith(HTTPS)) {
                builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager()).hostnameVerifier(SSLSocketClient.getHostnameVerifier());
            }
            client = builder.build();
            response = client.newCall(request).execute();
            String body = response.body().string();
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.EXTERNAL_NET_ERROR, e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return "";
    }

    /**
     * GET方式提交表单数据
     *
     * @param url 访问地址
     * @return body string字符串，失败返回""
     */
    public static String sendGet(String url) {
        Request request = new Request.Builder().url(url).build();
        return newNet(request);
    }

    public static String sendGet(String url, Map<String, String> headerMap) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headerMap != null) {
            headerMap.forEach((name, value) -> requestBuilder.addHeader(name, value == null ? "" : value));
        }
        return newNet(requestBuilder.build());
    }

    /**
     * json数据POST请求
     *
     * @param url     请求地址
     * @param jsonStr 字符串
     * @return
     */
    public static String sendPost(String url, String jsonStr) {
        RequestBody requestBody = RequestBody.create(jsonStr, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return newNet(request);
    }

    /**
     * 自定义类型POST请求
     *
     * @param url         请求地址
     * @param data        数据
     * @param contentType 数据类型
     * @return
     */
    public static String sendPost(String url, String data, String contentType) {
        RequestBody requestBody = RequestBody.create(data, MediaType.parse(contentType));
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return newNet(request);
    }

    /**
     * json数据POST请求-可设置header
     *
     * @param url       请求地址
     * @param jsonStr   数据
     * @param headerMap 请求header参数
     * @return
     */
    public static String sendPost(String url, String jsonStr, Map<String, String> headerMap) {
        RequestBody requestBody = RequestBody.create(jsonStr, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headerMap != null) {
            headerMap.forEach((name, value) -> requestBuilder.addHeader(name, value == null ? "" : value));
        }
        requestBuilder.post(requestBody);
        return newNet(requestBuilder.build());
    }

    /**
     * 自定义类型POST请求-可设置header
     *
     * @param url         请求地址
     * @param data        数据
     * @param headerMap   请求header参数
     * @param contentType 数据类型
     * @return
     */
    public static String sendPost(String url, String data, Map<String, String> headerMap, String contentType) {
        RequestBody requestBody = RequestBody.create(data, MediaType.parse(contentType));
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headerMap != null) {
            headerMap.forEach((name, value) -> requestBuilder.addHeader(name, value == null ? "" : value));
        }
        requestBuilder.post(requestBody);
        return newNet(requestBuilder.build());
    }

    /**
     * json数据PUT请求
     *
     * @param url     请求地址
     * @param jsonStr 字符串
     * @return
     */
    public static String sendPut(String url, String jsonStr) {
        RequestBody requestBody = RequestBody.create(jsonStr, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).put(requestBody).build();
        return newNet(request);
    }

    /**
     * 自定义类型PUT请求
     *
     * @param url         请求地址
     * @param data        数据
     * @param contentType 数据类型
     * @return
     */
    public static String sendPut(String url, String data, String contentType) {
        RequestBody requestBody = RequestBody.create(data, MediaType.parse(contentType));
        Request request = new Request.Builder().url(url).put(requestBody).build();
        return newNet(request);
    }

    /**
     * json数据put请求-可设置header
     *
     * @param url       请求地址
     * @param jsonStr   数据
     * @param headerMap 请求header参数
     * @return
     */
    public static String sendPut(String url, String jsonStr, Map<String, String> headerMap) {
        RequestBody requestBody = RequestBody.create(jsonStr, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headerMap != null) {
            headerMap.forEach((name, value) -> requestBuilder.addHeader(name, value == null ? "" : value));
        }
        requestBuilder.put(requestBody);
        return newNet(requestBuilder.build());
    }

    /**
     * 自定义类型PUT请求-可设置header
     *
     * @param url         请求地址
     * @param data        数据
     * @param headerMap   请求header参数
     * @param contentType 数据类型
     * @return
     */
    public static String sendPut(String url, String data, Map<String, String> headerMap, String contentType) {
        RequestBody requestBody = RequestBody.create(data, MediaType.parse(contentType));
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headerMap != null) {
            headerMap.forEach((name, value) -> requestBuilder.addHeader(name, value == null ? "" : value));
        }
        requestBuilder.put(requestBody);
        return newNet(requestBuilder.build());
    }

    public static String sendDelete(String url) {
        Request request = new Request.Builder().url(url).delete().build();
        return newNet(request);
    }

    /**
     * POST方式提交表单数据
     *
     * @param url    访问地址
     * @param params 数据
     * @return body string字符串，失败返回""
     */
    public static String sendPostForm(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            params.forEach((name, value) -> builder.add(name, value == null ? "" : value));
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return newNet(request);
    }

    public static String sendPostForm(String url, Map<String, String> params, Map<String, String> headerMap) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            params.forEach((name, value) -> builder.add(name, value == null ? "" : value));
        }
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headerMap != null) {
            headerMap.forEach((name, value) -> requestBuilder.addHeader(name, value == null ? "" : value));
        }
        requestBuilder.post(builder.build());
        return newNet(requestBuilder.build());
    }

    /**
     * POST方式提交xml格式数据
     *
     * @param url    访问地址
     * @param xmlStr 数据
     * @return body string字符串，失败返回""
     */
    public static String sendPostXml(String url, String xmlStr) {
        RequestBody requestBody = RequestBody.create(xmlStr, MediaType.parse("application/xml; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return newNet(request);
    }

    public static boolean downFile(String url, String filePath) {
        Request request = new Request.Builder().url(url).build();
        FileOutputStream fos = null;
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient();
            response = client.newCall(request).execute();
            int status = response.code();
            if (status == HTTP_OK) {
                InputStream is = response.body().byteStream();
                File file = new File(filePath);
                fos = new FileOutputStream(file);
                int len;
                byte[] buf = new byte[2048];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                return true;
            } else if (response.isSuccessful()) {
                log.error("http网络请求非正常响应：" + response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("http网络请求异常信息：" + e.getMessage());
        } finally {
            try {
                assert response != null;
                response.close();
            } catch (Exception e) {
            }
            try {
                assert fos != null;
                fos.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static String uploadFile(String url, String fileKey, Map<String, String> data, String filename, InputStream inputStream) {
        OkHttpClient client;
        Response response = null;
        try {
            RequestBody fileBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    try {
                        String type = Files.probeContentType(Paths.get(filename));
                        return MediaType.parse(type != null ? type : "application/octet-stream");
                    } catch (IOException e) {
                        return MediaType.parse("application/octet-stream");
                    }
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    byte[] buffer = new byte[2048];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        sink.write(buffer, 0, read);
                    }
                }

                @Override
                public long contentLength() {
                    return -1; // 避免设置错误
                }
            };

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (data != null) {
                data.forEach(builder::addFormDataPart);
            }

            builder.addFormDataPart(fileKey, filename, fileBody);

            Request request = new Request.Builder().url(url).post(builder.build()).build();
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
            httpBuilder.connectTimeout(5, TimeUnit.SECONDS).readTimeout(1, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES);
            if (request.url().url().toString().startsWith(HTTPS)) {
                httpBuilder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager()).hostnameVerifier(SSLSocketClient.getHostnameVerifier());
            }
            client = httpBuilder.build();
            response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : null;
            } else {
                log.error("文件上传失败，HTTP状态码: {}", response.code());
            }

        } catch (Exception e) {
            log.error("文件上传异常", e);
        } finally {
            try {
                if (response != null) response.close();
                // 通常不建议在这里关闭外部传入的 inputStream，除非你已约定
            } catch (Exception e) {
                log.error("关闭响应资源异常", e);
            }
        }

        return null;
    }

    //实现一个通用文件上传方法
    public static InputStream uploadFileToStream(String url, String fileKey, Map<String, String> data, String filename, InputStream inputStream) {
        OkHttpClient client;
        Response response;
        try {
            // 读取 inputStream 到 byte[]，以便复用
            byte[] fileBytes = readAllBytes(inputStream);
            MediaType mediaType = MediaType.parse(guessMimeType(filename));

            RequestBody fileBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return mediaType;
                }

                @Override
                public long contentLength() {
                    return fileBytes.length;
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    sink.write(fileBytes);
                }
            };

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (data != null) {
                data.forEach(builder::addFormDataPart);
            }

            builder.addFormDataPart(fileKey, filename, fileBody);

            Request request = new Request.Builder().url(url).post(builder.build()).build();
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
            httpBuilder.connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.MINUTES).writeTimeout(60, TimeUnit.SECONDS);
            if (request.url().url().toString().startsWith(HTTPS)) {
                httpBuilder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager()).hostnameVerifier(SSLSocketClient.getHostnameVerifier());
            }
            client = httpBuilder.build();
            response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return response.body() != null ? response.body().byteStream() : null;
            } else {
                System.err.println("文件上传失败，HTTP状态码: " + response.code());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("文件上传异常：" + e.getMessage());
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                // 注意：不能关闭 response，否则返回的 InputStream 也会关闭
                // 如果你要完全读取并返回 byte[]，就可以在这里关闭
            } catch (IOException e) {
                System.err.println("关闭资源异常：" + e.getMessage());
            }
        }

        return null;
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int read;
        while ((read = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, read);
        }
        return buffer.toByteArray();
    }

    private static String guessMimeType(String filename) {
        String type = URLConnection.guessContentTypeFromName(filename);
        return type != null ? type : "application/octet-stream";
    }


    public static class SSLSocketClient {

        //获取这个SSLSocketFactory
        public static SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, getTrustManager(), new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //获取TrustManager
        private static TrustManager[] getTrustManager() {
            return new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }};
        }

        //获取HostnameVerifier
        public static HostnameVerifier getHostnameVerifier() {
            return (s, sslSession) -> true;
        }

        public static X509TrustManager getX509TrustManager() {
            X509TrustManager trustManager = null;
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            } catch (Exception e) {
                e.printStackTrace();
            }

            return trustManager;
        }
    }
}