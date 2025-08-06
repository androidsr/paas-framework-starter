package paas.framework.mybatis.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.springframework.stereotype.Component;

import java.util.Map;


public class SelectQueryMethod extends AbstractMethod {

    public SelectQueryMethod(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = "<script>" +
                "select " +
                "   <foreach collection=\"columns\"  separator=\", \" index=\"idx\" item=\"column\">" +
                "       ${column} as ${@paas.framework.tools.PaasUtils@columnToField(column)}" +
                "   </foreach>" +
                "from " + tableInfo.getTableName() + " a " +
                "        ${ew.customSqlSegment}" +
                "</script>";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForOther(mapperClass, "selectQuery", sqlSource, Map.class);
    }

}
