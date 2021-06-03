package wmkang.domain.util;


import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


public class Util {


    private static ObjectMapper objectMapper = new ObjectMapper();


    public static <T> T copy(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <T> T copy(Object source, T target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    public static <T> T copy(Object source, Class<T> toValueType) {
        try {
            T target = toValueType.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T copy(Object source, Class<T> toValueType, String... ignoreProperties) {
        try {
            T target = toValueType.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target, ignoreProperties);
            return target;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, ?> copyToMap(Object source) {
        return objectMapper.convertValue(source, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, ?> copyToMap(Object source, String... ignoreProperties) {
        Map<String, ?> resultMap = objectMapper.convertValue(source, Map.class);
        Arrays.stream(ignoreProperties).forEach(resultMap::remove);
        return resultMap;
    }
}
