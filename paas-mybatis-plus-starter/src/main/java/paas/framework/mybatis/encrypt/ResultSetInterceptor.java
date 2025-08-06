package paas.framework.mybatis.encrypt;

import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import paas.framework.encrypt.sm4.SM4Util;
import paas.framework.tools.PaasUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 解密拦截器
 */
@Component
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = Statement.class)
})
@ConditionalOnProperty(name = "paas.mybatis-plus.encrypt.enabled")
public class ResultSetInterceptor implements Interceptor {

    @Value("${paas.mybatis-plus.encrypt.key}")
    String key;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取结果集的类型
        DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(defaultResultSetHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        Class<?> resultType = resultMaps.get(0).getType();
        // 判断是否包含CiphertextData注解
        boolean isContain = resultType.isAnnotationPresent(EntityEncrypt.class);
        Object resultObject = invocation.proceed();
        if (isContain && !Objects.isNull(resultObject)) {
            List list = (List) resultObject;
            for (Object item : list) {
                this.deal(item);
            }
        }
        return resultObject;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 处理结果集
     *
     * @param data: 待处理的数据
     **/
    protected void deal(Object data) throws IllegalAccessException {
        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            boolean isContainCiphertextField = field.isAnnotationPresent(FieldEncrypt.class);
            if (isContainCiphertextField) {
                field.setAccessible(true);
                Object o = field.get(data);
                if (o != null) {
                    String content = decrypt(String.valueOf(o));
                    field.set(data, content);
                }
            }
        }
    }

    /**
     * 解密
     *
     * @param content: 密文
     * @return java.lang.String
     **/
    private String decrypt(String content) {
        if (PaasUtils.isEmpty(content) || !content.startsWith("enc:")){
            return content;
        }
        try {
            return SM4Util.decryptEcb(content.substring(4), key);
        } catch (Exception e) {
            return content;
        }
    }
}
