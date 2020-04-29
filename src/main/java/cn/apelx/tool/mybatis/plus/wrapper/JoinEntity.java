package cn.apelx.tool.mybatis.plus.wrapper;

import cn.apelx.tool.constant.MybatisPlusConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * 连表实体
 *
 * @author lx
 * @since 2019/10/25 16:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
public class JoinEntity {
    // Join方式
    @NonNull
    private MybatisPlusConstant.TableJoinWayEnum tableJoinWay;

    // 需Join的表名
    @NonNull
    private String joinTableName;

    // 需Join表别名
    @NonNull
    private String joinTableAlias;

    // 主表连表字段
    @NonNull
    private String primaryTableJoinColumn;

    // 需Join表连表字段
    @NonNull
    private String joinTableJoinColumn;

}
