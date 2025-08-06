package paas.framework.encrypt.sm4;


import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import paas.framework.tools.PaasUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

@Slf4j
public class SM4Util {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    private static String key;
    private static final String ENCODING = "UTF-8";
    public static final String ALGORITHM_NAME = "SM4";
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    public static final int DEFAULT_KEY_SIZE = 128;

    public static void init(String finalKey){
        key = finalKey;
    }

    private static String init() {
        if (PaasUtils.isEmpty(key)) {
            key = "b61b2b925a6b1e807a3718e9b507c4c4";
        }
        return key;
    }

    /**
     * 生成密钥串
     *
     * @param keySize
     * @return
     * @throws Exception
     */
    public static byte[] generateKey(int keySize) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("SM4", BouncyCastleProvider.PROVIDER_NAME);
        kg.init(keySize, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    public static String encryptEcb(String data) throws Exception{
        return encryptEcb(data,init());
    }

    /**
     * SM4加密
     *
     * @param hexKey
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptEcb(String data, String hexKey) throws Exception {
        if (hexKey == null || "".equals(hexKey)) {
            hexKey = init();
        }
        String cipherText = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] srcData = data.getBytes("UTF-8");
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText = ByteUtils.toHexString(cipherArray);
        return cipherText;
    }

    public static String encryptEcb(byte[] srcData, String hexKey) throws Exception {
        if (hexKey == null || "".equals(hexKey)) {
            hexKey = init();
        }
        String cipherText = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText = ByteUtils.toHexString(cipherArray);
        return cipherText;
    }

    public static String decryptEcb(String data) throws Exception {
        return decryptEcb(data,init());
    }

    /**
     * SM4解密
     *
     * @param hexKey
     * @param data
     * @return
     * @throws Exception
     */
    public static String decryptEcb(String data, String hexKey) throws Exception {
        if (hexKey == null || "".equals(hexKey)) {
            hexKey = init();
        }
        String decryptStr = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(data);
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }

    public static String decryptEcb(byte[] cipherData, String hexKey) throws Exception {
        if (hexKey == null || "".equals(hexKey)) {
            hexKey = init();
        }
        String decryptStr = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }

    /**
     * 加密模式之ECB
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    private static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 生成ECB暗号
     *
     * @param algorithmName
     * @param mode
     * @param key
     * @return
     * @throws Exception
     */
    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    private static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    public static boolean verifyEcb(String hexKey, String cipherText, String paramStr) throws Exception {
        boolean flag = false;
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
        byte[] srcData = paramStr.getBytes(ENCODING);
        flag = Arrays.equals(decryptData, srcData);
        return flag;
    }
}