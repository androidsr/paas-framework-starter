package paas.framework.encrypt.sm4;


/**
 * 国密算法SM4工具
 */
public class CypherSm4Utils {

    /**
     * 解密规则
     */
    public static interface RULES {
        //保留指定位置名的信息
        String REPLACE = "N_N";
    }

    public static String decode(String data, String rule, int start, int end, char sign) throws Exception {
        String result = decode(data);
        switch (rule) {
            case RULES.REPLACE: {
                char[] dv = result.toCharArray();
                for (int i = start; i < result.length() - end; i++) {
                    dv[i] = sign;
                }
                result = String.valueOf(dv);
                break;
            }
            default: {
                throw new Exception("数据解密指定规则不正确");
            }
        }
        return result;
    }

    public static String decode(String data) throws Exception {
        String result;
        result = SM4Util.decryptEcb(data, null);
        return result;
    }

    public static String encode(String data) {
        String result = null;
        try {
            result = SM4Util.encryptEcb(data, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
