package paas.framework.mybatis.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.util.List;

public class MysqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new BatchAddMethod("batchAdd"));
        methodList.add(new BatchUpdateMethod("batchUpdate"));
        methodList.add(new QueryListMethod("queryList"));
        methodList.add(new SelectQueryMethod("selectQuery"));
        methodList.add(new ForceDeleteByIdMethod("forceDeleteById"));
        methodList.add(new ForceDeleteByIdsMethod("forceDeleteByIds"));
        return methodList;
    }
}