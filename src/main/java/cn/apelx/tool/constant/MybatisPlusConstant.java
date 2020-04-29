package cn.apelx.tool.constant;

/**
 * MybatisPlus常量类
 *
 * @author lx
 * @since 2020/04/29
 */
public class MybatisPlusConstant {

    /**
     * 连表方式 枚举类
     */
    public enum TableJoinWayEnum {

        /**
         * 内连接
         */
        INNER_JOIN("INNER JOIN", "内连接"),
        /**
         * 左连接
         */
        LEFT_JOIN("LEFT JOIN", "左连接"),
        /**
         * 左外连接
         */
        LEFT_OUTER_JOIN("LEFT OUTER JOIN", "左外连接"),
        /**
         * 右连接
         */
        RIGHT_JOIN("RIGHT JOIN", "右连接"),
        /**
         * 右外连接
         */
        RIGHT_OUTER_JOIN("RIGHT OUTER JOIN", "右外连接");

        private String joinSql;
        private String description;

        TableJoinWayEnum(String joinSql, String description) {
            this.joinSql = joinSql;
            this.description = description;
        }

        public String getJoinSql() {
            return joinSql;
        }

        public String getDescription() {
            return description;
        }
    }
}
