package paas.framework.tools;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Md5Util {
    @SneakyThrows
    public static String md5(String data) {
        MessageDigest md = MessageDigest.getInstance("MD5");// 生成一个MD5加密计算摘要
        md.update(data.getBytes());// 计算md5函数
        String md5Data = new BigInteger(1, md.digest()).toString(16);// 16是表示转换为16进制数
        return md5Data;
    }
}
