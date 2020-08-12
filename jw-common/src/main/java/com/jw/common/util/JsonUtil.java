package com.jw.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @date: 2020/8/12
 * @author: jinwei
 */
@Slf4j
public final class JsonUtil {
    private static Map<String, JavaType> javaListTypeMap = new HashMap<>(32);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JavaType MAP_JAVA_TYPE;
    private static JavaType LIST_MAP_TYPE;

    static {
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        MAP_JAVA_TYPE = MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        LIST_MAP_TYPE = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, MAP_JAVA_TYPE);
    }

    public static byte[] toBytes(Object ob) {
        if (ob == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsBytes(ob);
        } catch (JsonProcessingException e) {
            log.error("对象转为json失败", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static String toJsonString(Object ob) {
        if (ob == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(ob);
        } catch (JsonProcessingException e) {
            log.error("对象转为json失败", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static <T> T parse(String json, Class<T> type) {
        if (CommonUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            log.error("JSON字符串转换为对象失败", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static <T> T parse(String json, JavaType javaType) {
        if (CommonUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            log.error("JSON转换为对象失败", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static Map<String, Object> parse(String json) {
        return parse(json, MAP_JAVA_TYPE);
    }

    public static <E> List<E> parseAsList(String json, Class<E> type) {
        if (CommonUtils.isBlank(json)) {
            return null;
        }
        if (javaListTypeMap.get(type.getName()) == null) {
            javaListTypeMap.put(type.getName(), MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, type));
        }
        try {
            return MAPPER.readValue(json, javaListTypeMap.get(type.getName()));
        } catch (IOException e) {
            log.error("JSON字符串转换为对象失败", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static List<Map<String, Object>> parseAsList(String json) {
        if (CommonUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, LIST_MAP_TYPE);
        } catch (IOException e) {
            log.error("JSON字符串转换为对象数组失败", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
