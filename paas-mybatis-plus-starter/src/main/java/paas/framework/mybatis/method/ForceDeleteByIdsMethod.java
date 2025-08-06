package paas.framework.mybatis.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;


public class ForceDeleteByIdsMethod extends AbstractMethod {

    public ForceDeleteByIdsMethod(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        final String sql = "<script>delete from %s where %s in" +
                "<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"(\" separator=\",\" close=\")\">" +
                "#{item}" +
                "</foreach>" +
                "</script>";
        final String sqlResult = String.format(sql, tableInfo.getTableName(), tableInfo.getKeyColumn());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
        return this.addDeleteMappedStatement(mapperClass, "forceDeleteByIds", sqlSource);
    }

}
