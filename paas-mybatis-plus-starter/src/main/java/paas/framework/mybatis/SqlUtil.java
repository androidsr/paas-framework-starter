package paas.framework.mybatis;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlUtil {
    private final static String xssStr = "'|and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+";

    public static void injection(String value) {
        if (value == null || "".equals(value)) {
            return;
        }
        value = value.toLowerCase();
        String[] xssArr = xssStr.split("\\|");

        for (String xss : xssArr) {
            if (value.contains(xss)) {
                log.error("可能存在SQL注入风险：存在SQL注入关键词[{}]  值[{}]", xss, value);
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }

    }

    public static void injection(String[] values) {
        String[] xssArr = xssStr.split("\\|");
        for (String value : values) {
            if (value == null || "".equals(value)) {
                return;
            }
            value = value.toLowerCase();
            for (String xss : xssArr) {
                if (value.contains(xss)) {
                    log.error("可能存在SQL注入风险：存在SQL注入关键词[{}]  值[{}]", xss, value);
                    throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
                }
            }
        }
    }

}