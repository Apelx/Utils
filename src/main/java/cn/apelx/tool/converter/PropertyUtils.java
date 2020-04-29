package cn.apelx.tool.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 根据属性名调用对象属性的set、get方法
 *
 * @author lx
 * @since 2020/04/29
 */
public class PropertyUtils {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    /**
     * 获取属性值（只获取一级属性）
     *
     * @param obj          属性所属对象
     * @param propertyName 属性名
     * @return 属性值
     */
    @Nullable
    public static Object getProperty(Object obj, String propertyName) {
        Object propertyValue = null;
        if (obj != null && propertyName != null) {
            try {
                PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName);
                if (pd != null) {
                    Method getMethod = pd.getReadMethod();
                    propertyValue = getMethod.invoke(obj);
                }
            } catch (IllegalAccessException | InvocationTargetException ex) {
                logger.error("Get Property Error", ex);
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
        return propertyValue;
    }

    /**
     * 递归获取级联对象值
     *
     * @param obj          属性所属对象
     * @param propertyName 属性名
     * @return 属性值
     */
    @Nullable
    public static Object getPropertyCascade(Object obj, String propertyName) {
        Object propertyValue;
        String splitStr = ".";
        if (propertyName != null && propertyName.contains(splitStr)) {
            Object cascadeObj = getProperty(obj, propertyName.substring(0, propertyName.indexOf(splitStr)));
            propertyValue = getPropertyCascade(cascadeObj, propertyName.substring(propertyName.indexOf(splitStr) + 1));
        } else {
            try {
                propertyValue = getProperty(obj, propertyName);
            } catch (IllegalArgumentException ex) {
                propertyValue = "<不存在>" /*+ getIdValue(obj)*/;
            }
        }
        return propertyValue;
    }

    /**
     * 设置属性值
     *
     * @param obj           属性所属对象
     * @param propertyName  属性名
     * @param propertyValue 属性值
     */
    public static void setProperty(Object obj, String propertyName, Object propertyValue)
            throws InvocationTargetException, IllegalAccessException {
        if (obj != null && propertyName != null) {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName);
            if (pd != null) {
                Method setMethod = pd.getWriteMethod();
                setMethod.invoke(obj, propertyValue);
            }
        }
    }
}