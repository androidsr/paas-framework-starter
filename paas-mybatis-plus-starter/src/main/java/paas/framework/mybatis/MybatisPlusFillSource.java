package paas.framework.mybatis;

import java.util.List;
import java.util.Map;


/**
 * 自动填充数据源
 *
 * @author sirui
 */
public interface MybatisPlusFillSource {
    List<String> createFillField();

    List<String> updateFillField();

    Map<String, MyBatisFillItem> getData();
}
