package com.github.sparkzxl.mongodb.utils;

import com.github.sparkzxl.core.utils.ReflectObjectUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * description: mongoDb字段获取
 *
 * @author: zhouxinlei
 * @date: 2021-01-07 11:10:30
 */
public class MongoDbHandleUtil {

    public static Map<String, Object> getAndAnnotationValue(Object model) {
        Map<String, Object> annotationMap = Maps.newHashMap();
        List<Field> fields = ReflectObjectUtils.getAllField(model);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                org.springframework.data.mongodb.core.mapping.Field declaredAnnotation = field.getDeclaredAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
                if (ObjectUtils.isNotEmpty(declaredAnnotation)) {
                    annotationMap.put(declaredAnnotation.value(), field.get(model));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return annotationMap;
    }

}
