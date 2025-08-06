package paas.framework.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.Asserts;
import paas.framework.model.exception.BusException;
import paas.framework.model.pagination.OrderItem;
import paas.framework.model.pagination.PageInfo;
import paas.framework.model.pagination.PageResult;
import paas.framework.tools.PaasUtils;

import java.lang.reflect.Field;
import java.util.*;

public class MybatisHandler {

    /**
     * 分页前端参数设置
     *
     * @param query
     * @param <E>
     * @return
     */
    public final static <E> IPage<E> convert(PageInfo query) {
        IPage<E> page = new Page<>();
        Asserts.isTrueError(query == null, ResultMessage.PARAMETER_ERROR);
        page.setCurrent(query.getCurrent() != null ? query.getCurrent().longValue() : 1);
        page.setSize(query.getSize() != null ? query.getSize().longValue() : 10);
        if (PaasUtils.isNotEmpty(query.getOrders())) {
            for (OrderItem item : query.getOrders()) {
                SqlUtil.injection(item.getColumn());
                if (item.isAsc()) {
                    page.orders().add(com.baomidou.mybatisplus.core.metadata.OrderItem.asc(PaasUtils.fieldToColumn(item.getColumn())));
                } else {
                    page.orders().add(com.baomidou.mybatisplus.core.metadata.OrderItem.desc(PaasUtils.fieldToColumn(item.getColumn())));
                }
            }
        }
        return page;
    }

    public static <E> PageResult<E> convert(IPage page, List<E> data) {
        return convert(page, data, false);
    }

    /**
     * 分页结果逻辑封装
     *
     * @param page
     * @param <E>
     * @return
     */
    public static <E> PageResult<E> convert(IPage page, List<E> data, Boolean isDbToName) {
        PageResult<E> result = new PageResult<>();
        result.setSize((int) page.getSize());
        result.setCurrent((int) page.getCurrent());
        result.setTotal((int) page.getTotal());
        if (isDbToName) {
            DbToNameUtils.getDbToNameValue(data);
        }
        result.setRows(data);
        return result;
    }

}
