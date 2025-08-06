package paas.framework.mybatis.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.springframework.stereotype.Component;
import paas.framework.model.vo.SelectVO;

import java.util.List;


public class QueryListMethod extends AbstractMethod {

    public QueryListMethod(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = "<script>" +
                "select ${key} as value,${name} as label" +
                "<choose><when test=\"@paas.framework.tools.PaasUtils@isNotEmpty(supperId)\">" +
                ", ${supperId} as supper_id " +
                "</when></choose>" +
                "from " + tableInfo.getTableName() + " a where 1=1 " +
                "            <if test=\"@paas.framework.tools.PaasUtils@isNotEmpty(query.value)\">and ${key} = #{query.value}</if>" +
                "            <if test=\"@paas.framework.tools.PaasUtils@isNotEmpty(query.label)\">and ${name} like" +
                "                CONCAT('%',#{query.label},'%')" +
                "            </if>" +
                "            <if test=\"query.vars != null and !query.vars.isEmpty()\">" +
                "               <foreach collection=\"query.vars.entrySet()\"  separator=\" \" index=\"key\" item=\"val\">" +
                "                   <if test=\"@paas.framework.tools.PaasUtils@isNotEmpty(val)\">" +
                "                       and ${key} = #{val}" +
                "                   </if>" +
                "               </foreach>" +
                "            </if>" +
                "            <if test=\"@paas.framework.tools.PaasUtils@isNotEmpty(query.selected)\">" +
                "               or ${key} in" +
                "                <foreach collection=\"query.selected\" item=\"item\" open=\"(\" close=\")\" separator=\",\">" +
                "                    #{item}" +
                "                </foreach>" +
                "               ORDER BY ${key} NOT IN" +
                "                <foreach collection=\"query.selected\" item=\"item\" open=\"(\" close=\")\" separator=\",\">" +
                "                    #{item}" +
                "                </foreach>" +
                "            </if>" +
                "</script>";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForOther(mapperClass, "queryList", sqlSource, SelectVO.class);
    }



}
