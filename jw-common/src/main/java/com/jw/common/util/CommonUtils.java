package com.jw.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

@Slf4j
public final class CommonUtils {
    private final static Pattern PATTERN_INTEGER = Pattern.compile("^[-\\+]?[\\d]*$");
    private final static Pattern PATTERN_NUMBER = Pattern.compile("-?[0-9]+.？[0-9]*");
    private final static String METHOD_GET = "get", METHOD_IS = "is", BASE_TYPE_STRING = "String", BASE_TYPE_BOOLEAN = "boolean",
            BASE_TYPE_INTEGER = "Integer", BASE_TYPE_INT = "int";

    public static String blankStr() {
        return "";
    }

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isBlank(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isNotBlank(String str) {
        return str != null && !"".equals(str);
    }

    /**
     * 数组转map
     * @param list     数据源
     * @param getKey   获取key的方法
     * @param getValue 获取value的方法
     * @param <K>      key类型
     * @param <V>      value类型
     * @param <R>      数组元素类型
     * @return map集合
     */
    public static <K, V, R> Map<K, V> listConvertMap(List<R> list, Function<R, K> getKey, Function<R, V> getValue) {
        if (CommonUtils.isEmpty(list)) {
            return new HashMap<>(8);
        }
        Map<K, V> map = new HashMap<>(list.size());
        list.forEach(item -> map.put(getKey.apply(item), getValue.apply(item)));
        return map;
    }

    public static <K, V> Map<K, V> listConvertMap(List<V> list, Function<V, K> getKey) {
        return listConvertMap(list, getKey, Function.identity());
    }

    public static boolean isInteger(String str) {
        return !isBlank(str) && PATTERN_INTEGER.matcher(str).matches();
    }

    public static boolean isNumeric(String str) {
        return !isBlank(str) && PATTERN_NUMBER.matcher(str).matches();
    }

    public static <R, E> List<R> listConvert(List<E> sourceList, Function<E, R> getValue) {
        if (isEmpty(sourceList)) {
            return new ArrayList<>();
        }
        List<R> rList = new ArrayList<>(sourceList.size());
        for (E item : sourceList) {
            rList.add(getValue.apply(item));
        }
        return rList;
    }

    public static <K, V, R> List<R> mapConvertList(Map<K, V> source, Function<Map.Entry<K, V>, R> getValue) {
        if (isEmpty(source)) {
            return new ArrayList<>();
        }
        List<R> rList = new ArrayList<>(source.size());
        for (Map.Entry<K, V> e : source.entrySet()) {
            rList.add(getValue.apply(e));
        }
        return rList;
    }

    public static <K, V> Map<K, List<V>> listConvertListMap(List<V> list, Function<V, K> getKey) {
        return listConvertListMap(list, getKey, Function.identity());
    }

    public static <K, V, R> Map<K, List<R>> listConvertListMap(List<V> list, Function<V, K> getKey,
                                                               Function<V, R> getValue) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>(8);
        }
        Map<K, List<R>> map = new HashMap<>(list.size());
        K key;
        for (V v : list) {
            key = getKey.apply(v);
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(getValue.apply(v));
        }
        return map;
    }

    /**
     * 获取去下划线的uuid
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }



    public static String getFieldName(String methodName) {
        return firstToLowerCase(methodName.startsWith(METHOD_GET)
                ? methodName.substring(3)
                : (methodName.startsWith(METHOD_IS) ? methodName.substring(2) : methodName));
    }

    public static String firstToLowerCase(String param) {
        return CommonUtils.isBlank(param) ? "" : (param.substring(0, 1).toLowerCase() + param.substring(1));
    }

    public static String firstToUpperCase(String param) {
        return CommonUtils.isBlank(param) ? "" : (param.substring(0, 1).toUpperCase() + param.substring(1));
    }


    /**
     * 获取泛型类类型
     * @param clazz
     * @return
     */
    public static Class<Object> getSuperClassGenericType(Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    /**
     * list 转换
     * @param values 数据
     * @param call   回调函数
     * @param <R>
     * @param <I>
     * @return
     */
    public static <R, I> List<R> transform(List<I> values, Function<I, R> call) {
        if (CommonUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        List<R> result = new ArrayList<>(values.size());
        values.forEach(value -> result.add(call.apply(value)));
        return result;
    }

    public static <T> T getOrElse(T value, T defValue) {
        if (null == value) {
            return defValue;
        }
        if ("".equals(value.toString())) {
            return defValue;
        }
        return value;
    }

    /**
     * 获取指定下标泛型类类型
     * @param clazz
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static Class<Object> getSuperClassGenericType(Class clazz, int index) throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<Object>) params[index];
    }



    private static <T> Object checkCodeFieldAndGet(Enum enumParam) {
        try {
            Method getCode = enumParam.getClass().getMethod("getCode");
            return getCode.invoke(enumParam);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        }
        return null;
    }
}
