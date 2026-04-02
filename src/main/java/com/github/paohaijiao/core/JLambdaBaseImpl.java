/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.core;

import com.github.paohaijiao.anno.JColumn;
import com.github.paohaijiao.anno.JTable;
import com.github.paohaijiao.connection.JSqlConnection;
import com.github.paohaijiao.format.JSqlFormatter;
import com.github.paohaijiao.function.JSFunction;
import com.github.paohaijiao.model.JCondition;
import com.github.paohaijiao.model.JKeyValue;
import com.github.paohaijiao.model.JOrder;
import com.github.paohaijiao.util.JStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.paohaijiao.resultSet.JResultSetMapper.TYPE_HANDLERS;

public abstract class JLambdaBaseImpl<T> {
    protected final List<JCondition> conditions = new ArrayList<>();
    protected final List<JOrder> orders = new ArrayList<>();
    protected Class<T> entityClass;
    protected JSqlConnection sqlConnection;

    protected static String fillSqlWithEntity(String sqlTemplate, Object entity) {
        if (sqlTemplate == null || entity == null) {
            throw new IllegalArgumentException("sql templates and entity objects cannot be null");
        }
        List<JKeyValue> list = JSqlFormatter.parsePlaceholders(sqlTemplate);
        List<JKeyValue> fieldValueMap = JSqlFormatter.getFieldValues(entity, list);
        return JSqlFormatter.replacePlaceholders(sqlTemplate, fieldValueMap);
    }

    public static <T> T resultSetToObject(ResultSet rs, Class<T> targetClass) throws SQLException {
        try {
            T obj = targetClass.getDeclaredConstructor().newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnLabel(i);
                try {
                    Field field = targetClass.getDeclaredField(columnName);
                    field.setAccessible(true);
                    setFieldValue(rs, columnName, obj, field);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> void setFieldValue(ResultSet rs, String columnName, T obj, Field field) throws SQLException, IllegalAccessException {
        Class<?> fieldType = field.getType();
        Object value = TYPE_HANDLERS.getOrDefault(fieldType, ResultSet::getObject)
                .handle(rs, columnName);
        if (value != null || fieldType.isPrimitive()) {
            field.set(obj, value);
        }
    }

    protected String getColumnName(Field field) {
        JColumn columnAnnotation = field.getAnnotation(JColumn.class);
        return columnAnnotation != null && !columnAnnotation.value().isEmpty()
                ? columnAnnotation.value()
                : JStringUtils.camelToUnderline(field.getName());
    }

    protected String getColumnName(JSFunction<T, ?> column) {
        try {
            Method writeReplace = column.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(column);
            String methodName = lambda.getImplMethodName();
            if (methodName.startsWith("get")) {
                methodName = methodName.substring(3);
            } else if (methodName.startsWith("is")) {
                methodName = methodName.substring(2);
            }
            String fieldName = JStringUtils.uncapitalize(methodName);
            Field field = entityClass.getDeclaredField(fieldName);
            JColumn columnAnnotation = field.getAnnotation(JColumn.class);
            return columnAnnotation != null && !columnAnnotation.value().isEmpty()
                    ? columnAnnotation.value()
                    : JStringUtils.camelToUnderline(fieldName);
        } catch (Exception e) {
            throw new RuntimeException("failed to parse Lambda expression", e);
        }
    }

    protected String buildSelectSQL() {
        String tableName = getTableName();
        String selectFields = buildSelectFields();
        String whereClause = buildWhereClause();
        String orderByClause = buildOrderByClause();
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(selectFields).append(" FROM ").append(tableName);
        if (JStringUtils.isNotBlank(whereClause)) {
            sql.append(" WHERE ").append(whereClause);
        }
        if (JStringUtils.isNotBlank(orderByClause)) {
            sql.append(" ORDER BY ").append(orderByClause);
        }
        return sql.toString();
    }

    protected String getTableName() {
        JTable tableAnnotation = entityClass.getAnnotation(JTable.class);
        if (tableAnnotation != null && !tableAnnotation.value().isEmpty()) {
            return tableAnnotation.value();
        }
        return JStringUtils.camelToUnderline(entityClass.getSimpleName());
    }

    protected String buildSelectFields() {
        Field[] fields = entityClass.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    JColumn columnAnnotation = field.getAnnotation(JColumn.class);
                    String columnName = columnAnnotation != null && !columnAnnotation.value().isEmpty()
                            ? columnAnnotation.value()
                            : JStringUtils.camelToUnderline(field.getName());
                    return columnName + " AS " + field.getName();
                })
                .collect(Collectors.joining(", "));
    }

    protected String buildWhereClause() {
        if (conditions.isEmpty()) {
            return "";
        }
        return conditions.stream()
                .map(condition -> {
                    String column = condition.getColumn();
                    String operator = condition.getOperator();
                    String paramName = "" + column.replace('.', '_');
                    if ("IN".equalsIgnoreCase(operator)) {
                        Collection<?> values = (Collection<?>) condition.getValue();
                        String placeholders = values.stream()
                                .map(v -> "#{" + paramName + "}")
                                .collect(Collectors.joining(", "));
                        return column + " IN (" + placeholders + ")";
                    }
                    if ("LIKE".equalsIgnoreCase(operator)) {
                        return column + " LIKE CONCAT('%', #{" + paramName + "}, '%')";
                    }
                    return column + " " + operator + " #{" + paramName + "}";
                })
                .collect(Collectors.joining(" AND "));
    }

    private String buildOrderByClause() {
        if (orders.isEmpty()) {
            return "";
        }
        return orders.stream()
                .map(order -> {
                    String column = order.getColumn();
                    String direction = order.isAsc() ? "ASC" : "DESC";
                    return column + " " + direction;
                })
                .collect(Collectors.joining(", "));
    }

    protected Map<String, Object> buildParameterMap() {
        Map<String, Object> paramMap = new HashMap<>();
        for (JCondition condition : conditions) {
            String column = condition.getColumn();
            String paramName = "" + column.replace('.', '_');
            if ("IN".equalsIgnoreCase(condition.getOperator())) {
                Collection<?> values = (Collection<?>) condition.getValue();
                int index = 0;
                for (Object value : values) {
                    paramMap.put(paramName + "_" + index++, value);
                }
            } else {
                paramMap.put(paramName, condition.getValue());
            }
        }

        return paramMap;
    }

    protected String getIdFieldName() {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(JColumn.class) != null) {
                JColumn jColumn = field.getAnnotation(JColumn.class);
                if (jColumn.id()) {
                    String filedName = JStringUtils.camelToUnderline(field.getName());
                    String value = jColumn.value();
                    return StringUtils.isEmpty(value) ? filedName : jColumn.value();
                }
            }
        }
        return "id";
    }

    protected PreparedStatement buildPreparedStatement(String sql) throws Throwable {
        PreparedStatement preparedStatement = this.sqlConnection.getConnection().prepareStatement(sql);
        return preparedStatement;
    }
}
