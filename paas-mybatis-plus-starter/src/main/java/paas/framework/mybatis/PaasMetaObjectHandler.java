package paas.framework.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import paas.framework.web.SpringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * Mybatis自动填充
 *
 * @author sirui
 */
@Slf4j
@Component
public class PaasMetaObjectHandler implements MetaObjectHandler {

    MybatisPlusFillSource mybatisPlusFillSource;

    @Override
    public void insertFill(MetaObject metaObject) {
        if (mybatisPlusFillSource == null) {
            Map<String, MybatisPlusFillSource> fillMap = SpringUtils.getContext().getBeansOfType(MybatisPlusFillSource.class);
            for (MybatisPlusFillSource item : fillMap.values()) {
                mybatisPlusFillSource = item;
                break;
            }
            if (mybatisPlusFillSource == null) {
                log.warn("未实现自动填充接口(MybatisPlusFillSource)...");
                return;
            }
        }
        Map<String, MyBatisFillItem> data = mybatisPlusFillSource.getData();
        if (data == null) {
            log.warn("自动填充数据取值为空(MybatisPlusFillSource)...");
            return;
        }
        for (String item : mybatisPlusFillSource.createFillField()) {
            if (metaObject.hasGetter(item)) {
                MyBatisFillItem fillItem = data.get(item);
                if (fillItem == null || Objects.isNull(fillItem.getValue())) {
                    continue;
                }
                this.strictInsertFill(metaObject, item, () -> fillItem.getValue(), fillItem.getValueType());
            }
        }
    }

    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        if (mybatisPlusFillSource == null) {
            Map<String, MybatisPlusFillSource> fillMap = SpringUtils.getContext().getBeansOfType(MybatisPlusFillSource.class);
            for (MybatisPlusFillSource item : fillMap.values()) {
                mybatisPlusFillSource = item;
                break;
            }
            if (mybatisPlusFillSource == null) {
                log.warn("未实现自动填充接口(MybatisPlusFillSource)...");
                return this;
            }
        }
        Object obj = fieldVal.get();
        if (metaObject.getValue(fieldName) != null) {
            if (mybatisPlusFillSource.updateFillField().contains(fieldName)) {
                if (Objects.nonNull(obj)) {
                    metaObject.setValue(fieldName, obj);
                }
                return this;
            }
        } else {
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
        }
        return this;
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (mybatisPlusFillSource == null) {
            Map<String, MybatisPlusFillSource> fillMap = SpringUtils.getContext().getBeansOfType(MybatisPlusFillSource.class);
            for (MybatisPlusFillSource item : fillMap.values()) {
                mybatisPlusFillSource = item;
                break;
            }
            if (mybatisPlusFillSource == null) {
                log.warn("未实现自动填充接口(MybatisPlusFillSource)...");
                return;
            }
        }
        Map<String, MyBatisFillItem> data = mybatisPlusFillSource.getData();
        if (data == null) {
            log.warn("自动填充数据取值为空(MybatisPlusFillSource)...");
            return;
        }
        for (String item : mybatisPlusFillSource.updateFillField()) {
            if (metaObject.hasGetter(item)) {
                MyBatisFillItem fillItem = data.get(item);
                if (fillItem == null || Objects.isNull(fillItem.getValue())) {
                    continue;
                }
                log.info("数据库字段：{}; 自动填充值：{}; 自动填充值类型：{}", item, fillItem.getValue(), fillItem.getValueType());
                this.strictUpdateFill(metaObject, item, () -> fillItem.getValue(), fillItem.getValueType());
            }
        }
    }
}
