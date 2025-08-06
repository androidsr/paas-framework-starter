package paas.framework.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import paas.framework.model.dto.SelectQueryDTO;
import paas.framework.model.vo.SelectVO;
import paas.framework.tools.PaasUtils;
import paas.framework.web.SpringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 公共Mapper接口
 *
 * @param <T>
 */
public interface RootMapper<T extends EmptyEntity> extends BaseMapper<T> {

    /**
     * 通过ID判断是否存在
     * 默认column id
     *
     * @param id id值
     * @return
     */
    default boolean exists(Serializable id) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return selectCount(queryWrapper) > 0;
    }

    /**
     * 通过ID判断是否存在
     *
     * @param column 主键ID
     * @param value  id值
     * @return
     */
    default boolean exists(String column, Serializable value) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return selectCount(queryWrapper) > 0;
    }

    /**
     * 通过ID判断是否存在
     *
     * @param columns 主键ID
     * @param values  id值
     * @return
     */
    default boolean exists(String[] columns, Serializable... values) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        for (int i = 0; i < columns.length; i++) {
            queryWrapper.eq(columns[i], values[i]);
        }
        return selectCount(queryWrapper) > 0;
    }

    /**
     * 单条件查询集合
     *
     * @param column 查询数据库列
     * @param value  查询条件值
     * @return
     */
    default List<T> selectList(String column, Serializable value) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return selectList(queryWrapper);
    }

    /**
     * 查询一条数据
     *
     * @param column 查询数据库列
     * @param value  查询条件值
     * @return
     */
    default T selectOne(String column, Serializable value) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return selectOne(queryWrapper);
    }

    /**
     * 查询多条数据
     *
     * @param columns 查询数据列
     * @param values  查询条件值
     * @return
     */
    default List<T> selectList(List<String> columns, Serializable... values) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        for (int i = 0; i < columns.size(); i++) {
            queryWrapper.eq(columns.get(i), values[i]);
        }
        return selectList(queryWrapper);
    }

    /**
     * 查询一条数据
     *
     * @param columns 查询数据列
     * @param values  查询条件值
     * @return
     */
    default T selectOne(List<String> columns, Serializable... values) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        for (int i = 0; i < columns.size(); i++) {
            queryWrapper.eq(columns.get(i), values[i]);
        }
        return selectOne(queryWrapper);
    }


    /**
     * 查询数据集合
     *
     * @param column in查询列
     * @param values in值集合
     * @return
     */
    default List<T> selectList(List<String> columns, Object... values) {
        if (PaasUtils.isEmpty(values)) {
            return new ArrayList<>(0);
        }
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        int i = 0;
        for (String column : columns) {
            Object value = values[i++];
            if (value instanceof Collection) {
                Collection v = (Collection) value;
                if (PaasUtils.isEmpty(v)) {
                    return new ArrayList<>(0);
                }
                queryWrapper.in(column, v);
            } else {
                queryWrapper.eq(column, value);
            }
        }
        return selectList(queryWrapper);
    }

    /**
     * in查询数据集合
     *
     * @param column in查询列
     * @param values in值集合
     * @return
     */
    default List<T> selectIn(String column, Collection<?> values) {
        if (PaasUtils.isEmpty(values)) {
            return new ArrayList<>(0);
        }
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(column, values);
        return selectList(queryWrapper);
    }

    /**
     * 删除操作（指定字段列和每一列对应和值）
     *
     * @param columns 删除条件列
     * @param values  删除条件值
     * @return
     */
    default Integer delete(String column, Serializable key) {
        UpdateWrapper<T> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq(column, key);
        return delete(queryWrapper);
    }

    /**
     * 删除操作（指定字段列和每一列对应和值）
     *
     * @param columns 删除条件列
     * @param values  删除条件值
     * @return
     */
    default Integer delete(String column, Collection<?> values) {
        UpdateWrapper<T> queryWrapper = new UpdateWrapper<>();
        queryWrapper.in(column, values);
        return delete(queryWrapper);
    }

    /**
     * 删除操作（指定字段列和每一列对应和值）
     *
     * @param columns 删除条件列
     * @param values  删除条件值
     * @return
     */
    default Integer delete(List<String> columns, Object... values) {
        UpdateWrapper<T> queryWrapper = new UpdateWrapper<>();
        int i = 0;
        for (String column : columns) {
            Object value = values[i++];
            if (value instanceof Collection) {
                queryWrapper.in(column, (Collection) value);
            } else {
                queryWrapper.eq(column, value);
            }
        }
        return delete(queryWrapper);
    }

    /**
     * 将查询出的多行数据转换成map（id转名称使用）
     *
     * @param column 查询条件列
     * @param values 查询条件值
     * @param col    id列和名称列
     * @return
     */
    default Map<Object, Object> convert(String column, Set<?> values, String... col) {
        if (PaasUtils.isEmpty(values)) {
            return new HashMap<>(0);
        }
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.select(col);
        queryWrapper.in(column, values);
        List<Map<String, Object>> maps = selectMaps(queryWrapper);
        Map<Object, Object> result = new HashMap<>();
        for (Map<String, Object> item : maps) {
            result.put(item.get(col[0]), item.get(col[1]));
        }
        return result;
    }

    /**
     * 将查询出的多行数据转换成map（id转名称使用）
     *
     * @param column 查询条件列
     * @param values 查询条件值
     * @param col    id列和名称列
     * @return
     */
    default Map<String, String> convertString(String column, Set<?> values, String... col) {
        if (PaasUtils.isEmpty(values)) {
            return new HashMap<>(0);
        }
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.select(col);
        queryWrapper.in(column, values);
        List<Map<String, Object>> maps = selectMaps(queryWrapper);
        Map<String, String> result = new HashMap<>();
        for (Map<String, Object> item : maps) {
            result.put(PaasUtils.nullToBlank(item.get(col[0])), PaasUtils.nullToBlank(item.get(col[1])));
        }
        return result;

    }

    /**
     * 批量插入内置方法(外部使用batchAdd方法扩展自动填充)
     *
     * @param list
     */
    void batchInsert(@Param("list") List<T> list);

    /**
     * 批量插入
     *
     * @param list
     */
    default void batchAdd(@Param("list") List<T> list) {
        PaasMetaObjectHandler metaObjectHandler = SpringUtils.getBean(PaasMetaObjectHandler.class);
        for (T entity : list) {
            MetaObject metaObject = SystemMetaObject.forObject(entity);
            metaObjectHandler.insertFill(metaObject);
        }
        batchInsert(list);
    }

    /**
     * 批量修改
     *
     * @param list
     */
    default void batchEdit(@Param("list") List<T> list) {
        PaasMetaObjectHandler metaObjectHandler = SpringUtils.getBean(PaasMetaObjectHandler.class);
        for (T entity : list) {
            MetaObject metaObject = SystemMetaObject.forObject(entity);
            metaObjectHandler.insertFill(metaObject);
        }
        batchUpdate(list);
    }

    /**
     * 批量更新内置方法（外部使用batchEdit方法扩展自动填充）
     *
     * @param list
     */
    void batchUpdate(@Param("list") List<T> list);

    /**
     * 扩展方法
     *
     * @param query 查询条件
     * @return 下拉选择数据
     */
    default List<SelectVO> queryList(@Param("page") IPage page, @Param("query") SelectQueryDTO query, @Param("key") String key, @Param("name") String name) {
        return queryList(page, query, key, name, "");
    }

    /**
     * 分页下拉查询指定简单列返回分页数据
     *
     * @param page     分页参数
     * @param query    查询条件
     * @param key      id列
     * @param name     名称列
     * @param supperId 上级id
     * @return
     */
    List<SelectVO> queryList(@Param("page") IPage page, @Param("query") SelectQueryDTO query, @Param("key") String key, @Param("name") String name, @Param("supperId") String supperId);

    /**
     * 通用查询
     *
     * @param columns 返回列信息
     * @param page    分页查询
     * @param ew      查询条件
     * @return
     */
    List<Map<String, Object>> selectQuery(@Param("page") IPage page, @Param(Constants.WRAPPER) Wrapper<T> ew, @Param("columns") String... columns);

    /**
     * 强制删除
     *
     * @param id id值
     * @return
     */
    Integer forceDeleteById(@Param("id") Object id);

    /**
     * 强制删除
     *
     * @param list id值
     * @return
     */
    Integer forceDeleteByIds(@Param("list") Collection<?> list);
}
