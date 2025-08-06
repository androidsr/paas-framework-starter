package paas.framework.mybatis;

import lombok.Data;
import paas.framework.model.model.Between;
import paas.framework.model.pagination.PageInfo;
import paas.framework.mybatis.wrapper.KeywordsEnum;
import paas.framework.mybatis.wrapper.SearchWrapper;

import java.io.Serializable;

@Data
public class BaseQueryDTO implements Serializable {

    /**
     * 主键
     */
    @SearchWrapper(keyword = KeywordsEnum.eq, column = "id")
    private Long id;

    /**
     * 分页参数
     */
    private PageInfo page;

    /**
     * 创建时间
     */
    @SearchWrapper(keyword = KeywordsEnum.between, column = "create_time")
    private Between createTime;

    /**
     * 创建用户
     */
    @SearchWrapper(keyword = KeywordsEnum.eq, column = "create_by")
    private String createBy;

    /**
     * 更新用户
     */
    @SearchWrapper(keyword = KeywordsEnum.eq, column = "update_by")
    private String updateBy;

}
