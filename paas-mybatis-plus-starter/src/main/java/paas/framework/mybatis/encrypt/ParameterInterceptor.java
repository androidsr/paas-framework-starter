package paas.framework.mybatis.encrypt;

import jakarta.annotation.Resource;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import paas.framework.encrypt.sm4.SM4Util;
import paas.framework.tools.PaasUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Properties;

/**
 * 加密拦截器
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})
})
@ConditionalOnProperty(name = "paas.mybatis-plus.encrypt.enabled")
public class ParameterInterceptor implements Interceptor {

    @Value("${paas.mybatis-plus.encrypt.key}")
    String key;

    @Resource
    ResultSetInterceptor resultSetInterceptor;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof ParameterHandler) {
            ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();

            Object object = parameterHandler.getParameterObject();
            if (object != null) {
                if (object instanceof MapperMethod.ParamMap) {
                    Map map = (Map) object;
                    boolean boll = map.containsKey("param1");
                    Object obj;
                    if (boll) {
                        obj = map.get("param1");
                    } else {
                        obj = map.get("et");
                    }
                    setFieldValue(obj.getClass(), obj);
                }else{
                    setFieldValue(object.getClass(), object);
                }
            }
            return invocation.proceed();
        }else {
            Object object = invocation.getArgs()[1];
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                Class<?> clazz = object.getClass();
                boolean isContain = clazz.isAnnotationPresent(EntityEncrypt.class);
                if (isContain) {
                    setFieldValue(clazz, object);
                }
            } else if ("UPDATE".equals(sqlCommandType.name())) {
                Class<?> clazz = object.getClass();
                boolean res = clazz.isAnnotationPresent(EntityEncrypt.class);
                if (res) {
                    setFieldValue(clazz, object);
                } else {
                    if (object instanceof MapperMethod.ParamMap) {
                        Map map = (Map) object;
                        boolean boll = map.containsKey("param1");
                        Object obj;
                        if (boll) {
                            obj = map.get("param1");
                        } else {
                            obj = map.get("et");
                        }
                        setFieldValue(obj.getClass(), obj);
                    }
                }
            }
            //执行SQL方法
            Object result = invocation.proceed();
            //还原实体类被加密过的字段
            if (object instanceof MapperMethod.ParamMap) {
                Map map = (Map) object;
                if (map.containsKey("param1")) {
                    resultSetInterceptor.deal(map.get("param1"));
                } else {
                    resultSetInterceptor.deal(map.get("et"));
                }
            } else {
                resultSetInterceptor.deal(invocation.getArgs()[1]);
            }
            return result;
        }
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public void setFieldValue(Class<?> clazz,Object object) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            boolean isContainCiphertextField = field.isAnnotationPresent(FieldEncrypt.class);
            if (isContainCiphertextField) {
                field.setAccessible(true);
                Object o = field.get(object);
                if (o != null) {
                    String content = encrypt(String.valueOf(o));
                    field.set(object, content);
                }
            }
        }
    }

    /**
     * 加密
     *
     * @param content: 待加密的内容
     * @return java.lang.String
     **/
    private String encrypt(String content) {
        if (PaasUtils.isEmpty(content) || content.startsWith("enc:")){
            return content;
        }
        try {
            return "enc:"+SM4Util.encryptEcb(content, key);
        } catch (Exception e) {
            e.printStackTrace();
            return content;
        }
    }
}

