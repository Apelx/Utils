package cn.apelx.tool.converter;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 前端对象转换器
 * 前端包装类与实体字段名一致，直接用ViewConverter转换，不用定义mapper；
 * 前端包装类部分字段名与实体不一致，直接用ViewConverter转换，传入mapper定义字段映射；
 * 部分前端字段无实体字段对应或对应多个实体字段，覆盖ViewConverter方法，自定义字段映射。
 * 转换前端传递给后端排序字段
 * 转换后端返回给前端VO中字段
 *
 * @author lx
 * @since 2020/04/29 17:56
 */
public class BaseViewConverter {
    private static final Logger logger = LoggerFactory.getLogger(BaseViewConverter.class);
    private Class viewClass;
    // 排序时 转换实体字段对应数据库字段
    private Map<String, String> sortColumnConverter;
    // 返回VO 时转换Map
    private Map<String, String> voConverter;

    /**
     * 初始化转换器，只转换同名属性
     *
     * @param viewClass 前端对象类
     */
    public BaseViewConverter(Class viewClass) {
        this.viewClass = viewClass;
    }

    public BaseViewConverter(Class viewClass, Map<String, String> sortColumnConverter) {
        this.viewClass = viewClass;
        this.sortColumnConverter = sortColumnConverter;
    }

    public BaseViewConverter(Class viewClass, Map<String, String> sortColumnConverter, Map<String, String> voConverter) {
        this.viewClass = viewClass;
        this.sortColumnConverter = sortColumnConverter;
        this.voConverter = voConverter;
    }

    /**
     * 实体集合转为前端对象集合
     *
     * @param entities 实体集合
     * @return 前端对象集合
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public List toViewList(List entities) {
        List viewList = null;
        if (entities != null && entities.size() > 0) {
            viewList = new ArrayList<>();
            for (Object entity : entities) {
                viewList.add(entityToView(entity));
            }
        }
        return viewList;
    }

    /**
     * 前端字段转换为实体字段（排序列名），未配置mapper或mapper内未匹配到的情况下直接返回参数字段，覆盖此方法定义特殊排序列
     *
     * @param viewField 前端字段名
     * @return 实体字段，如存在多个排序列返回List
     */
    @Nullable
    public Object toEntityField(String viewField) {
        Object entityField;
        if (StringUtils.isNotEmpty(viewField) && sortColumnConverter != null) {
            entityField = sortColumnConverter.get(viewField);
            if (entityField == null) {
                entityField = viewField;
            }
        } else {
            entityField = viewField;
        }
        return entityField;
    }


    /**
     * 实体转为前端对象，覆盖此方法定义特殊字段转换规则
     *
     * @param entityObj 实体对象
     * @return 前端对象
     */
    @Nullable
    public Object entityToView(Object entityObj) {
        Object viewObj = copyEntity(entityObj);
        if (viewObj != null && voConverter != null) {
            for (Map.Entry<String, String> singleMapper : voConverter.entrySet()) {
                try {
                    Object entityValue = PropertyUtils.getPropertyCascade(entityObj, singleMapper.getValue());
                    PropertyUtils.setProperty(viewObj, singleMapper.getKey(), entityValue);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    logger.error("Set Property Error", e);
                }
            }
        }
        return viewObj;
    }

    /**
     * 相同属性名复制
     */
    @Nullable
    private Object copyEntity(Object entityObj) {
        Object viewObj = null;
        try {
            viewObj = viewClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (viewObj != null) {
            BeanUtils.copyProperties(entityObj, viewObj);
        }
        return viewObj;
    }
}