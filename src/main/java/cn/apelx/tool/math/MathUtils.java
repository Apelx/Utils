package cn.apelx.tool.math;

import java.math.BigDecimal;

/**
 * 提供精确的浮点数运算，包括加减乘除和四舍五入。
 * @author zgm
 */
public class MathUtils {

    /**
     * 默认除法运算精度
     */
    private static final int DEF_DIV_SCALE = 10;

    /**
     * 不可实例化
     */
    private MathUtils() {
    }

    /**
     * 提供精确的加法运算。
     * @param parameters 被加数
     * @return 加数和
     */
    public static double add(Double ... parameters) {
        if (parameters != null && parameters.length > 0) {
            BigDecimal sum = new BigDecimal(0);
            for (Double parameter : parameters) {
                if (parameter == null) {
                    parameter = 0d;
                }
                sum = sum.add(BigDecimal.valueOf(parameter));
            }
            return sum.doubleValue();
        }
        else {
            return 0d;
        }
    }

    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(Double v1, Double v2) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null) {
            v2 = 0d;
        }
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(Double v1, Double v2) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null) {
            v2 = 0d;
        }
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(Double v1, Double v2) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null || v2 == 0) {
            return 0d;
        }
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(Double v1, Double v2, int scale) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null || v2 == 0) {
            return 0d;
        }
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(Double v, int scale) {
        if (v == null) {
            return 0;
        }
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = BigDecimal.valueOf(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
