package paas.framework.encrypt.desensitize;

import cn.hutool.core.util.StrUtil;
import paas.framework.tools.PaasUtils;

import java.util.function.Function;

public enum DesensitizeRule {
    /**
     * 用户名，只显示第一个字
     */
    USERNAME(s -> PaasUtils.isEmpty(s) ? "" : StrUtil.hide(s, 1, s.length())),
    /**
     * 身份证，显示前四位和后两位，例如：1002************23
     */
    ID_CARD(s -> StrUtil.isBlank(s) ? "" : StrUtil.hide(s, 4, s.length() - 2)),
    /**
     * 银行卡号
     */
    CARD_NO(s -> StrUtil.isBlank(s) ? "" : StrUtil.hide(s, 4, s.length() - 4)),

    /**
     * 手机号，显示前四位和后四位，例如：
     */
    PHONE(s -> StrUtil.isBlank(s) ? "" : StrUtil.hide(s, 3, s.length() - 4)),
    /**
     * 密码，全部隐藏，只显示位数
     */
    PASSWORD(s -> StrUtil.isBlank(s) ? "" : StrUtil.repeat('*', s.length())),
    /**
     * 邮箱，隐藏第一位和@中间内容，例如：1*********@qq.com
     */
    EMAIL(s -> {
        if (StrUtil.isBlank(s)) {
            return "";
        } else {
            int index = StrUtil.indexOf(s, '@');
            return index <= 1 ? s : StrUtil.hide(s, 1, index);
        }
    });

    private final Function<String, String> desensitize;

    DesensitizeRule(Function<String, String> desensitize) {
        this.desensitize = desensitize;
    }

    public Function<String, String> desensitize() {
        return desensitize;
    }
}