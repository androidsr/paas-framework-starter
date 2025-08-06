package paas.framework.mybatis.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sirui
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchWrapper {

    /**
     * 条件的关键字
     */
    KeywordsEnum keyword() default KeywordsEnum.eq;

    /**
     * 数据表列
     */
    String column() default "";

}