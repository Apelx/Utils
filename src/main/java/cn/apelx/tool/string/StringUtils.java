package cn.apelx.tool.string;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串 Utils
 *
 * @author lx
 * @since 2020/1/11 15:33
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    public StringUtils() {
    }

    /**
     * 隐藏最后几位字符，用*代替
     *
     * @param str       原字符串
     * @param lastCount 隐藏位数
     * @return String
     */
    public static String hideLastWords(String str, int lastCount) {
        if (!StringUtils.isEmpty(str) && str.length() > lastCount) {
            str = str.substring(0, str.length() - lastCount);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lastCount; i++) {
                sb.append("*");
            }
            str += sb.toString();
        } else {
            str = "";
        }
        return str;
    }

    public static String hideMiddleWords(String str, int hideCount) {
        if (!StringUtils.isEmpty(str) && str.length() > hideCount) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hideCount; i++) {
                sb.append("*");
            }
            int hidePos = str.length() / 2 - hideCount / 2 + (str.length() % 2 == 0 ? 0 : 1);
            str = str.substring(0, hidePos) + sb.toString() + str.substring(hidePos + hideCount);
        } else {
            str = "";
        }
        return str;
    }

    /**
     * 隐藏指定位数字符 *
     *
     * @param str
     * @param start 开始位数（从1开始） 包含
     * @param end   结束位数（从1开始） 包含
     *              start <= end
     * @return
     */
    public static String hideSpecifyWords(String str, int start, int end) {
        if (isNotEmpty(str) && start >= 1 && end >= 1) {
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }
            if (str.length() < start) {
                return str;
            } else {
                int startIndex = start - 1;
                int endIndex = end > str.length() ? (str.length() - 1) : (end - 1);
                StringBuilder appendStr = new StringBuilder();
                for (int i = 0; i < str.length(); i++) {
                    if (i >= startIndex && i <= endIndex) {
                        appendStr.append("*");
                    } else {
                        appendStr.append(str.charAt(i));
                    }
                }
                str = appendStr.toString();
            }
        }
        return str;
    }


    /**
     * double 保留小数
     *
     * @param data
     * @param point
     * @return
     */
    public static Double keepDecimalDigits(Double data, int point) {
        BigDecimal bd = new BigDecimal(data);
        data = bd.setScale(point, BigDecimal.ROUND_HALF_UP).doubleValue();
        return data;

    }

    /**
     * 获取保单区间号
     *
     * @param policyCode 保单号
     */
    public static final String getInternalNumber(String policyCode) {
        String internalNumber = "";
        if (isNotEmpty(policyCode)) {
            String temp = policyCode.substring(0, policyCode.length() - 1);
            if (policyCode.length() > 1 && policyCode.length() < 4) {
                internalNumber = temp;
            } else if (policyCode.length() >= 4) {
                internalNumber = temp.substring(temp.length() - 3);
            }
        }
        return internalNumber;
    }

    public static String parseTemplate(String templateStr,
                                       Map<String, String> data, String... defaultNullReplaceVals) {
        if (templateStr == null) {
            return null;
        }
        if (data == null) {
            data = Collections.EMPTY_MAP;
        }
        String nullReplaceVal = defaultNullReplaceVals.length > 0 ? defaultNullReplaceVals[0] : "";
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");

        StringBuffer newValue = new StringBuffer();
        Matcher matcher = pattern.matcher(templateStr);
        while (matcher.find()) {
            String key = matcher.group(1);
            String r = nullReplaceVal;
            // 转换变量中的$,\
            if (data.get(key) != null) {
                r = data.get(key).toString();
                r = r.replaceAll("\\$", "&#36;");
                if (r.endsWith("\\")) {
                    r = r.substring(0, r.indexOf('\\')) + "&#92;";
                }
            }
            matcher.appendReplacement(newValue, r);
        }
        matcher.appendTail(newValue);
        return newValue.toString();
    }

    /**
     * List 转 字符串 逗号分隔
     *
     * @param list
     * @return
     */
    public static String listToString(List<String> list) {
        StringBuilder result = new StringBuilder();
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (isNotEmpty(list.get(i))) {
                    result.append(list.get(i)).append(i == (list.size() - 1) ? "" : ",");
                }
            }
        }
        return result.toString();
    }

    public static String removeLastSplitWord(String str) {
        return removeLastSplitWord(str, ",");
    }

    public static String removeLastSplitWord(String str, String splitWord) {
        if (!StringUtils.isEmpty(str) && !StringUtils.isEmpty(splitWord) && (str.length() - 1) == str.lastIndexOf(splitWord)) {
            return str.substring(0, str.lastIndexOf(splitWord));
        } else {
            return str;
        }
    }

    /**
     * 判断字符串是否为数字 <br>
     *
     * @param str 字符串
     * @return boolean 如果为数字则返回true，否则返回false
     */
    public static boolean isNumeric(String str) {
        if (StringUtils.isNotEmpty(str)) {
            Pattern pattern = Pattern.compile("[0-9]+");
            return pattern.matcher(str).matches();
        } else {
            return false;
        }
    }

    /**
     * 截断字符串，如果字符串长度小于最大字符数则不截断 <br>
     *
     * @param str       字符串
     * @param maxLength 最大字符数
     * @return String 截断后的字符串
     */
    public static String shortString(String str, int maxLength) {
        if (isNotEmpty(str) && str.length() > maxLength) {
            str = str.substring(0, maxLength);
        }
        return str;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static boolean isEmpty(String... values) {
        if (values != null && values.length > 0) {
            for (String value : values) {
                if (isEmpty(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 字符串数组转文本
     */
    public static String listConvertString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (isNotEmpty(list.get(i))) {
                sb.append(list.get(i)).append(i == list.size() - 1 ? "" : ",");
            }
        }
        return sb.toString();
    }


}
