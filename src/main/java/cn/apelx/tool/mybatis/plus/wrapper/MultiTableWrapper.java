package cn.apelx.tool.mybatis.plus.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.lang.NonNull;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * 多表查询条件Wrapper
 * 对QueryWrapper 进行封装，支持JAVA API 方式连表
 *
 * @author lx
 * @since 2019/10/25 15:49
 */
public class MultiTableWrapper<T> extends QueryWrapper<T> {

    // 查询主表别名
    @NonNull
    private String tableAlias;

    MultiTableWrapper(@NonNull String tableAlias) {
        this.tableAlias = tableAlias;
//        super.setEntity(entity);
//        super.initNeed();
    }

    private LinkedHashSet<JoinEntity> joinEntityList;

    /**
     * 添加连表操作
     * 连表操作不能重复; 但可以重复连一个表，但是连接字段不同
     *
     * @param joinEntity
     * @return
     */
    public MultiTableWrapper<T> join(JoinEntity joinEntity) {
        if (joinEntity != null) {
            initJoinEntityList();
            this.joinEntityList.add(joinEntity);
        }
        return this;
    }

    /**
     * 批量添加连表操作
     * 连表操作不能重复; 但可以重复连一个表，但是连接字段不同
     *
     * @param joinEntities
     * @return
     */
    public MultiTableWrapper<T> joinAll(LinkedHashSet<JoinEntity> joinEntities) {
        if (joinEntities != null && !joinEntities.isEmpty()) {
            initJoinEntityList();
            this.joinEntityList.addAll(joinEntities);
        }
        return this;
    }

    /**
     * 覆盖 getCustomSqlSegment方法, 拼接连表语句
     *
     * @return
     */
    @Override
    public String getCustomSqlSegment() {
        MergeSegments expression = getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = getSqlSegment();
            if (StringUtils.isNotEmpty(sqlSegment)) {
                StringBuilder joinTableSql = new StringBuilder(Constants.SPACE);
                if (joinEntityList != null && !joinEntityList.isEmpty()) {
                    for (JoinEntity joinEntity : joinEntityList) {
                        if (joinEntity != null) {
                            joinTableSql.append(joinEntity.getTableJoinWay().getJoinSql())
                                    .append(Constants.SPACE)
                                    .append(joinEntity.getJoinTableName())
                                    .append(Constants.SPACE)
                                    .append(joinEntity.getJoinTableAlias())
                                    .append(Constants.SPACE)
                                    .append(StringPool.ON)
                                    .append(Constants.SPACE)
                                    .append(joinEntity.getPrimaryTableJoinColumn().contains(".") ? joinEntity.getPrimaryTableJoinColumn() : (tableAlias + "." + joinEntity.getPrimaryTableJoinColumn()))
                                    .append(Constants.SPACE)
                                    .append(StringPool.EQUALS)
                                    .append(Constants.SPACE)
                                    .append(joinEntity.getJoinTableJoinColumn().contains(".") ? joinEntity.getJoinTableJoinColumn() : (joinEntity.getJoinTableAlias() + "." + joinEntity.getJoinTableJoinColumn()))
                                    .append(Constants.SPACE);
                        }
                    }
                }
                if (normal.isEmpty()) {
                    return joinTableSql + sqlSegment;
                } else {
                    return joinTableSql + Constants.WHERE + StringPool.SPACE + sqlSegment;
                }
            }
        }
        return StringPool.EMPTY;
    }


    @Override
    protected MultiTableWrapper<T> instance() {
        return new MultiTableWrapper<>(tableAlias);
    }

    private void initJoinEntityList() {
        if (this.joinEntityList == null) {
            this.joinEntityList = new LinkedHashSet<>();
        }
    }
}
