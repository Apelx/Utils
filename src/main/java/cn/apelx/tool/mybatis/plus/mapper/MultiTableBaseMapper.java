package cn.apelx.tool.mybatis.plus.mapper;

import cn.apelx.tool.mybatis.plus.wrapper.MultiTableWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

/**
 * 多表查询 Base Mapper
 * 所有需要支持连表查询的Mapper继承该Mapper
 *
 * @author lx
 */
public interface MultiTableBaseMapper<T> extends BaseMapper<T> {

    /**
     * 分页支持多表关联查询
     *
     * @param page
     * @param wrapper
     * @return
     */
    IPage<T> findListPage(IPage<T> page, @Param(Constants.WRAPPER) MultiTableWrapper<T> wrapper);
}