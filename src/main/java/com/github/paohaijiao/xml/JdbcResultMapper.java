package com.github.paohaijiao.xml;

import com.github.paohaijiao.type.JTypeReference;

import java.lang.reflect.*;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class JdbcResultMapper {

    /**
     * 将 ResultSet 转换为 JTypeReference 指定的类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T mapResultSet(ResultSet rs, JTypeReference<T> typeReference) throws Exception {
        if (rs == null || !rs.next()) {
            return null;
        }
        Class<?> rawType = typeReference.getRawType();
        if (rawType == Integer.class || rawType == int.class) {
            return (T) Integer.valueOf(rs.getInt(1));
        }
        if (rawType == Long.class || rawType == long.class) {
            return (T) Long.valueOf(rs.getLong(1));
        }
        if (rawType == String.class) {
            return (T) rs.getString(1);
        }
        if (rawType == Boolean.class || rawType == boolean.class) {
            return (T) Boolean.valueOf(rs.getBoolean(1));
        }
        if (rawType == Double.class || rawType == double.class) {
            return (T) Double.valueOf(rs.getDouble(1));
        }
        if (rawType == Float.class || rawType == float.class) {
            return (T) Float.valueOf(rs.getFloat(1));
        }
        if (rawType == Date.class) {
            return (T) rs.getTimestamp(1);
        }
        if (typeReference.isArray()) {
            return (T) mapToArray(rs, typeReference);
        }
        if (List.class.isAssignableFrom(rawType)) {
            return (T) mapToList(rs, typeReference);
        }
        if (Set.class.isAssignableFrom(rawType)) {
            return (T) mapToSet(rs, typeReference);
        }
        if (Map.class.isAssignableFrom(rawType)) {
            return (T) mapToMap(rs, typeReference);
        }
        return (T) mapToBean(rs, rawType);
    }

    /**
     * 将 ResultSet 转换为 List
     */
    private static <E> List<E> mapToList(ResultSet rs, JTypeReference<?> typeReference) throws Exception {
        List<E> list = new ArrayList<>();
        Type[] actualArgs = typeReference.getActualTypeArguments();
        if (actualArgs.length == 1) {
            JTypeReference<E> elementTypeRef = new JTypeReference.AccessibleJTypeReference<>(actualArgs[0]);
            do {
                E element = mapResultSetToSingleRow(rs, elementTypeRef);
                list.add(element);
            } while (rs.next());
        }
        return list;
    }

    /**
     * 将 ResultSet 转换为 Set
     */
    private static <E> Set<E> mapToSet(ResultSet rs, JTypeReference<?> typeReference) throws Exception {
        Set<E> set = new HashSet<>();
        Type[] actualArgs = typeReference.getActualTypeArguments();
        if (actualArgs.length == 1) {
            JTypeReference<E> elementTypeRef = new JTypeReference.AccessibleJTypeReference<>(actualArgs[0]);
            do {
                E element = mapResultSetToSingleRow(rs, elementTypeRef);
                set.add(element);
            } while (rs.next());
        }

        return set;
    }

    /**
     * 将 ResultSet 转换为 Map
     */
    private static <K, V> Map<K, V> mapToMap(ResultSet rs, JTypeReference<?> typeReference) throws Exception {
        Map<K, V> map = new HashMap<>();
        Type[] actualArgs = typeReference.getActualTypeArguments();
        if (actualArgs.length == 2) {
            JTypeReference<K> keyTypeRef = new JTypeReference.AccessibleJTypeReference<>(actualArgs[0]);
            JTypeReference<V> valueTypeRef = new JTypeReference.AccessibleJTypeReference<>(actualArgs[1]);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            do {
                K key = convertValue(rs.getObject(1), keyTypeRef);
                V value;
                if (columnCount > 1) {
                    value = mapColumnsToValue(rs, valueTypeRef, 2);
                } else {
                    value = convertValue(rs.getObject(1), valueTypeRef);
                }
                map.put(key, value);
            } while (rs.next());
        }

        return map;
    }

    /**
     * 将 ResultSet 转换为数组
     */
    private static <E> E[] mapToArray(ResultSet rs, JTypeReference<?> typeReference) throws Exception {
        List<E> list = new ArrayList<>();
        Type componentType = typeReference.getArrayComponentType();
        JTypeReference<E> elementTypeRef = new JTypeReference.AccessibleJTypeReference<>(componentType);
        do {
            E element = mapResultSetToSingleRow(rs, elementTypeRef);
            list.add(element);
        } while (rs.next());

        // 转换为数组
        Class<?> componentClass = getRawType(componentType);
        @SuppressWarnings("unchecked")
        E[] array = (E[]) Array.newInstance(componentClass, list.size());
        return list.toArray(array);
    }

    /**
     * 处理单行结果（用于 List/Set/Array 的元素）
     */
    private static <T> T mapResultSetToSingleRow(ResultSet rs, JTypeReference<T> typeReference) throws Exception {
        Class<?> rawType = typeReference.getRawType();
        if (isSimpleType(rawType)) {
            return convertValue(rs.getObject(1), typeReference);
        }
        return mapToBean(rs, (Class<T>)rawType);
    }

    /**
     * 将 ResultSet 当前行映射为 JavaBean
     */
    private static <T> T mapToBean(ResultSet rs, Class<T> beanClass) throws Exception {
        T instance = beanClass.getDeclaredConstructor().newInstance();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            String fieldName = columnNameToFieldName(columnName);
            try {
                Field field = findField(beanClass, fieldName);
                field.setAccessible(true);
                Object value = rs.getObject(i);
                if (value != null) {   // 类型转换
                    value = convertValue(value, field.getType());
                }
                field.set(instance, value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    /**
     * 将多列映射为对象
     */
    private static <T> T mapColumnsToValue(ResultSet rs, JTypeReference<T> typeReference, int startColumn) throws Exception {
        Class<?> rawType = typeReference.getRawType();
        if (isSimpleType(rawType)) {
            return convertValue(rs.getObject(startColumn), typeReference);
        }
        T instance = (T)rawType.getDeclaredConstructor().newInstance();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = startColumn; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            String fieldName = columnNameToFieldName(columnName);
            try {
                Field field = findField(rawType, fieldName);
                field.setAccessible(true);
                Object value = rs.getObject(i);
                if (value != null) {
                    value = convertValue(value, field.getType());
                }
                field.set(instance, value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    /**
     * 值类型转换
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertValue(Object value, JTypeReference<T> typeReference) {
        return (T)convertValue(value, typeReference.getRawType());
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertValue(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return (T) value;
        }
        if (targetType == Integer.class || targetType == int.class) {
            return (T) Integer.valueOf(((Number) value).intValue());
        }
        if (targetType == Long.class || targetType == long.class) {
            return (T) Long.valueOf(((Number) value).longValue());
        }
        if (targetType == Double.class || targetType == double.class) {
            return (T) Double.valueOf(((Number) value).doubleValue());
        }
        if (targetType == Float.class || targetType == float.class) {
            return (T) Float.valueOf(((Number) value).floatValue());
        }
        if (targetType == String.class) {
            return (T) value.toString();
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Number) {
                return (T) Boolean.valueOf(((Number) value).intValue() != 0);
            }
            return (T) Boolean.valueOf(value.toString());
        }
        if (targetType == Date.class || targetType == java.sql.Date.class) {
            if (value instanceof Timestamp) {
                return (T) new Date(((Timestamp) value).getTime());
            }
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to " + targetType);
    }

    /**
     * 判断是否为简单类型
     */
    private static boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == String.class ||
                Number.class.isAssignableFrom(clazz) ||
                clazz == Boolean.class ||
                clazz == Date.class ||
                clazz == java.sql.Date.class ||
                clazz == Timestamp.class;
    }

    /**
     * 查找字段（包括父类）
     */
    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return findField(superClass, fieldName);
            }
            throw e;
        }
    }

    /**
     * 列名转字段名 (user_name -> userName)
     */
    private static String columnNameToFieldName(String columnName) {
        StringBuilder result = new StringBuilder();
        boolean toUpper = false;
        for (char c : columnName.toCharArray()) {
            if (c == '_') {
                toUpper = true;
            } else {
                result.append(toUpper ? Character.toUpperCase(c) : c);
                toUpper = false;
            }
        }

        return result.toString();
    }
    /**
     * 从第三方类复制出来的工具方法：解决私有方法无法调用问题
     */
    private static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getRawType(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Class<?> componentType = getRawType(((GenericArrayType) type).getGenericComponentType());
            return Array.newInstance(componentType, 0).getClass();
        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        } else if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            return bounds.length > 0 ? getRawType(bounds[0]) : Object.class;
        } else {
            throw new IllegalArgumentException("不支持的类型: " + type);
        }
    }

}
