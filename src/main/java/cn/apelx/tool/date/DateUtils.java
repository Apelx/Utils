package cn.apelx.tool.date;


import cn.apelx.tool.string.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author lx
 * @since 2020/4/27 13:15
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {
    public static final String FORMAT_TIME_S = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATE_M = "yyyyMMdd";

    /**
     * 获取当前时间 y-M-dH:m:s
     *
     * @return
     */
    public static Date getCurTime() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 获取当前日期 y-M-d
     *
     * @return
     */
    public static Date getCurDate() {
        try {
            return new SimpleDateFormat(FORMAT_DATE).parse(formatDate(getCurTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return DateFormatUtils.format(date, FORMAT_DATE);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        return DateFormatUtils.format(date, format);
    }

    /**
     * 格式化日期时间
     *
     * @param date
     * @return
     */
    public static String formatTimeS(Date date) {
        return DateFormatUtils.format(date, FORMAT_TIME_S);
    }

    /**
     * 格式化日期时间
     *
     * @param date
     * @return
     */
    public static String formatTimeS(Date date, String format) {
        return DateFormatUtils.format(date, format);
    }

    /**
     * 日期时间字符串 转换 date
     *
     * @param date
     * @return
     */
    public static Date parseTimeS(String date) {
        try {
            return new SimpleDateFormat(FORMAT_TIME_S).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期字符串 转换 date
     *
     * @param date
     * @return
     */
    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat(FORMAT_DATE).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期增减天数
     *
     * @param date
     * @param amount 天数(可为负数)
     * @return
     */
    public static Date addDays(Date date, int amount) {
        return addDays(date, amount);
    }

    /**
     * 获取日期间隔（比实际间隔多1天，如1月10日-1月19日，天数为10天）
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDays(Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            Date tempDate;
            if (startDate.compareTo(endDate) > 0) {
                tempDate = startDate;
                startDate = endDate;
                endDate = tempDate;
            } else if (startDate.compareTo(endDate) == 0) {
                return 0;
            }
            return new Long((endDate.getTime() - startDate.getTime())
                    / (3600 * 24 * 1000)).intValue() + 1;
        }
        return 0;
    }

    /**
     * 获取两个日期之间 日期数组
     *
     * @param startDate 格式为 yyyy-MM-dd
     * @param endDate   格式为 yyyy-MM-dd
     * @return
     */
    public static Date[] getDateIntervalArray(Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            if (startDate.equals(endDate)) {
                return new Date[]{startDate};
            }
            Date tempDate;
            // 确保 startDate < endDate
            if (startDate.compareTo(endDate) > 0) {
                tempDate = startDate;
                startDate = endDate;
                endDate = tempDate;
            }
            int days = getDays(startDate, endDate);
            Date[] dateArray = new Date[days];
            for (int i = 0; i < dateArray.length; i++) {
                dateArray[i] = addDays(startDate, i);
            }
            return dateArray;
        }
        return null;
    }

    /**
     * 获取两个日期之间 日期字符串数组
     * 传入格式化变量
     *
     * @param startDate 格式为 yyyy-MM-dd
     * @param endDate   格式为 yyyy-MM-dd
     * @return
     */
    public static String[] getDateIntervalArray(Date startDate, Date endDate, String format) {
        if (startDate != null && endDate != null && StringUtils.isNotEmpty(format)) {
            if (startDate.equals(endDate)) {
                return new String[]{formatDate(startDate, format)};
            }
            Date tempDate;
            // 确保 startDate < endDate
            if (startDate.compareTo(endDate) > 0) {
                tempDate = startDate;
                startDate = endDate;
                endDate = tempDate;
            }
            int days = getDays(startDate, endDate);
            String[] dateStringArray = new String[days];
            for (int i = 0; i < dateStringArray.length; i++) {
                dateStringArray[i] = formatDate(addDays(startDate, i), format);
            }
            return dateStringArray;
        }
        return null;
    }
}
