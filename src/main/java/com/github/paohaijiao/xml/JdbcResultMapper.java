package com.github.paohaijiao.xml;

import com.github.paohaijiao.type.JTypeReference;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.*;

public class JdbcResultMapper {

    /**
     * 将 ResultSet 转换为 JTypeReference 指定的类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T mapResultSet(ResultSet rs, JTypeReference<T> typeReference) throws Exception {
        if (rs == null) {
            return null;
        }
        Class<?> rawType = getActualRawType(typeReference);
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

    public static Class<?> getActualRawType(JTypeReference<?> typeReference) {
        Type type = typeReference.getType();
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class) {
                return (Class<?>) rawType;
            }
        }
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        return typeReference.getRawType();
    }
    private static <E> List<E> mapToList(ResultSet rs, JTypeReference<?> typeReference) throws Exception {
        List<E> resultList = new ArrayList<>();
        if (typeReference == null) {
            throw new IllegalArgumentException("JTypeReference cannot be null");
        }
        Type type = typeReference.getType();
        Type elementType = getCollectionElementType(type);
        Class<?> elementRawClass = getRawClass(elementType);
        ResultSetMetaData metaData = rs.getMetaData();
        while (rs.next()) {
            Object mappedObject;
            if (Map.class.isAssignableFrom(elementRawClass)) {
                mappedObject = convertToMap(rs, metaData, elementType);
            } else if (isSimpleType(elementRawClass)) {
                mappedObject = rs.getObject(1);
                mappedObject = convertValueToType(mappedObject, elementRawClass);
            } else {
                mappedObject = convertToEntity(rs, metaData, elementRawClass);
            }
            @SuppressWarnings("unchecked")
            E castedObject = (E) mappedObject;
            resultList.add(castedObject);
        }

        return resultList;
    }

    /**
     * 获取集合类型的元素类型
     * 例如：List<User> -> User.class
     * List<Map<String, Object>> -> ParameterizedType of Map
     */
    private static Type getCollectionElementType(Type collectionType) {
        if (collectionType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) collectionType;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                return actualTypeArguments[0];
            }
        }
        return Object.class;
    }

    private static <T> T convertToEntity(ResultSet rs, ResultSetMetaData metaData, Class<T> entityClass) throws Exception {
        T instance = entityClass.getDeclaredConstructor().newInstance();
        int columnCount = metaData.getColumnCount();
        Map<String, Field> fieldMap = getAllFields(entityClass);
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            Object columnValue = rs.getObject(i);
            Field field = fieldMap.get(columnName);
            if (field == null) {
                field = fieldMap.get(underscoreToCamelCase(columnName));
            }
            if (field == null) {
                field = fieldMap.get(columnName.toLowerCase());
            }

            if (field != null) {
                field.setAccessible(true);
                Object convertedValue = convertValueToType(columnValue, field.getType());
                field.set(instance, convertedValue);
            }
        }
        return instance;
    }

    /**
     * 下划线转驼峰
     */
    private static String underscoreToCamelCase(String underscore) {
        if (underscore == null || underscore.isEmpty()) {
            return underscore;
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (char c : underscore.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }

    private static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                fieldMap.put(field.getName(), field);
            }
            currentClass = currentClass.getSuperclass();
        }

        return fieldMap;
    }

    private static boolean isMapType(Type type) {
        Class<?> rawClass = getRawClass(type);
        return Map.class.isAssignableFrom(rawClass);
    }

    private static Map<?, ?> convertToMap(ResultSet rs, ResultSetMetaData metaData, Type mapType) throws Exception {
        int columnCount = metaData.getColumnCount();
        Class<?> keyType = String.class;
        Class<?> valueType = Object.class;
        if (mapType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) mapType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length >= 1) {
                keyType = getRawClass(actualTypeArguments[0]);
            }
            if (actualTypeArguments.length >= 2) {
                valueType = getRawClass(actualTypeArguments[1]);
            }
        }
        Map<Object, Object> resultMap = createMapInstance(getRawClass(mapType));
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            Object columnValue = rs.getObject(i);
            Object convertedKey = convertValueToType(columnName, keyType);
            Object convertedValue = convertValueToType(columnValue, valueType);
            resultMap.put(convertedKey, convertedValue);
        }

        return resultMap;
    }

    private static Map<Object, Object> createMapInstance(Class<?> mapClass) throws Exception {
        if (mapClass == null || mapClass == Map.class) {
            return new LinkedHashMap<>();
        }
        if (mapClass.isInterface()) {
            if (mapClass == java.util.SortedMap.class) {
                return new java.util.TreeMap<>();
            }
            return new LinkedHashMap<>();
        }
        return (Map<Object, Object>) mapClass.getDeclaredConstructor().newInstance();
    }

    private static Class<?> getRawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class) {
                return (Class<?>) rawType;
            }
        }
        if (type instanceof GenericArrayType) {
            return Object[].class;
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return Object.class;
        }
        return Object.class;
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
     * 每一行数据作为一个 Map，列名作为 key，列值作为 value
     * 兼容泛型参数缺失的情况，默认 key 为 String，value 为 Object
     *
     * @param rs ResultSet 结果集
     * @param typeReference 类型引用
     * @return Map 对象（单行）或 List<Map>（多行，取决于外层包装）
     */
    private static Map<?, ?> mapToMap(ResultSet rs, JTypeReference<?> typeReference) throws Exception {
        if (rs == null) {
            return new LinkedHashMap<>();
        }
        Type[] actualArgs = typeReference.getActualTypeArguments();
        Type keyType;
        Type valueType;
        if (actualArgs == null || actualArgs.length == 0) {
            keyType = String.class;
            valueType = Object.class;
        } else if (actualArgs.length == 1) {
            keyType = actualArgs[0];
            valueType = Object.class;
        } else {
            keyType = actualArgs[0];
            valueType = actualArgs[1];
        }
        Class<?> keyRawType = getRawTypeFromType(keyType);
        Class<?> valueRawType = getRawTypeFromType(valueType);
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Map<Object, Object> resultMap = new LinkedHashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            Object columnValue = rs.getObject(i);
            Object key = convertValueToType(columnName, keyRawType);
            Object value = convertValueToType(columnValue, valueRawType);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private static Class<?> getRawTypeFromType(Type type) {
        if (type == null) {
            return Object.class;
        }
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type rawType = pt.getRawType();
            if (rawType instanceof Class) {
                return (Class<?>) rawType;
            }
        }

        if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;
            Type[] upperBounds = wt.getUpperBounds();
            if (upperBounds != null && upperBounds.length > 0) {
                return getRawTypeFromType(upperBounds[0]);
            }
        }

        if (type instanceof TypeVariable) {
            TypeVariable<?> tv = (TypeVariable<?>) type;
            Type[] bounds = tv.getBounds();
            if (bounds != null && bounds.length > 0) {
                return getRawTypeFromType(bounds[0]);
            }
        }

        if (type instanceof GenericArrayType) {
            return Object[].class;
        }

        return Object.class;
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
    /**
     * 通用的值类型转换方法
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertValueToType(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return (T) value;
        }
        if (targetType == String.class) {
            return (T) value.toString();
        }
        if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof Number) {
                return (T) Integer.valueOf(((Number) value).intValue());
            }
            return (T) Integer.valueOf(value.toString());
        }
        if (targetType == Long.class || targetType == long.class) {
            if (value instanceof Number) {
                return (T) Long.valueOf(((Number) value).longValue());
            }
            return (T) Long.valueOf(value.toString());
        }
        if (targetType == Double.class || targetType == double.class) {
            if (value instanceof Number) {
                return (T) Double.valueOf(((Number) value).doubleValue());
            }
            return (T) Double.valueOf(value.toString());
        }
        if (targetType == Float.class || targetType == float.class) {
            if (value instanceof Number) {
                return (T) Float.valueOf(((Number) value).floatValue());
            }
            return (T) Float.valueOf(value.toString());
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Number) {
                return (T) Boolean.valueOf(((Number) value).intValue() != 0);
            }
            if (value instanceof String) {
                return (T) Boolean.valueOf(Boolean.parseBoolean((String) value));
            }
            return (T) Boolean.valueOf(value.toString());
        }
        if (targetType == Byte.class || targetType == byte.class) {
            if (value instanceof Number) {
                return (T) Byte.valueOf(((Number) value).byteValue());
            }
            return (T) Byte.valueOf(value.toString());
        }
        if (targetType == Short.class || targetType == short.class) {
            if (value instanceof Number) {
                return (T) Short.valueOf(((Number) value).shortValue());
            }
            return (T) Short.valueOf(value.toString());
        }
        if (targetType == Character.class || targetType == char.class) {
            String str = value.toString();
            if (str.length() > 0) {
                return (T) Character.valueOf(str.charAt(0));
            }
        }
        if (targetType == java.util.Date.class) {
            if (value instanceof java.sql.Timestamp) {
                return (T) new java.util.Date(((java.sql.Timestamp) value).getTime());
            }
            if (value instanceof java.sql.Date) {
                return (T) new java.util.Date(((java.sql.Date) value).getTime());
            }
        }
        if (targetType == java.sql.Date.class) {
            if (value instanceof java.util.Date) {
                return (T) new java.sql.Date(((java.util.Date) value).getTime());
            }
        }
        if (targetType == java.sql.Timestamp.class) {
            if (value instanceof java.util.Date) {
                return (T) new java.sql.Timestamp(((java.util.Date) value).getTime());
            }
        }
        return (T) value;
    }


}
