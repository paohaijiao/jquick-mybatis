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

import com.github.paohaijiao.connection.JSqlConnection;
import com.github.paohaijiao.format.JSqlFormatter;
import com.github.paohaijiao.function.JSFunction;
import com.github.paohaijiao.mapper.JLambdaUpdate;
import com.github.paohaijiao.model.JCondition;
import com.github.paohaijiao.model.JKeyValue;
import com.github.paohaijiao.statement.JNamedParameterPreparedStatement;
import com.github.paohaijiao.util.JStringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class LambdaUpdateImpl<T> extends JLambdaBaseImpl<T> implements JLambdaUpdate<T> {
    private final Map<String, Object> updateValues = new HashMap<>();

    public LambdaUpdateImpl(Class<T> entityClass, JSqlConnection sqlConnection) {
        this.entityClass = entityClass;
        this.sqlConnection = sqlConnection;
    }

    @Override
    public JLambdaUpdate<T> set(JSFunction<T, ?> column, Object value) {
        String columnName = getColumnName(column);
        updateValues.put(columnName, value);
        return this;
    }

    @Override
    public JLambdaUpdate<T> eq(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), "=", value));
        return this;
    }

    @Override
    public JLambdaUpdate<T> ne(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), "!=", value));
        return this;
    }

    @Override
    public JLambdaUpdate<T> gt(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), ">", value));
        return this;
    }

    @Override
    public JLambdaUpdate<T> ge(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), ">=", value));
        return this;
    }

    @Override
    public JLambdaUpdate<T> lt(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), "<", value));
        return this;
    }

    @Override
    public JLambdaUpdate<T> le(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), "<=", value));
        return this;
    }

    @Override
    public JLambdaUpdate<T> like(JSFunction<T, ?> column, String value) {
        conditions.add(new JCondition(getColumnName(column), "like", value));
        return this;
    }

    @Override
    public JLambdaUpdate<T> in(JSFunction<T, ?> column, Collection<?> values) {
        conditions.add(new JCondition(getColumnName(column), "in", values));
        return this;
    }

    @Override
    public int execute() {
        String sql = buildUpdateSQL();
        System.out.println("update sql:" + sql);
        List<JCondition> condition = this.conditions;
        Map<String, Object> updateCause = this.updateValues;
        int k = 0;
        try {
            JNamedParameterPreparedStatement namedParameterPreparedStatement = new JNamedParameterPreparedStatement(sqlConnection.getConnection(), sql);
            for (String key : updateCause.keySet()) {
                k = k + 1;
                JKeyValue model = new JKeyValue();
                model.setNum(k);
                model.setKey(key);
                model.setValue(updateCause.get(key));
                namedParameterPreparedStatement.setParameter(model);
            }
            for (int i = 0; i < condition.size(); i++) {
                k = k + 1;
                JKeyValue model = new JKeyValue();
                model.setNum(k);
                model.setKey(condition.get(i).getColumn());
                model.setValue(condition.get(i).getValue());
                namedParameterPreparedStatement.setParameter(model);
            }
            return namedParameterPreparedStatement.executeUpdate();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private String buildUpdateSQL() {
        String tableName = getTableName();
        String setClause = buildSetClause();
        String whereClause = buildWhereClause();
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName).append(" SET ").append(setClause);
        if (JStringUtils.isNotBlank(whereClause)) {
            sql.append(" WHERE ").append(whereClause);
        }
        return sql.toString();
    }

    private String buildSetClause() {
        return updateValues.keySet().stream()
                .map(column -> column + " = #{" + column + "}")
                .collect(Collectors.joining(", "));
    }

    @Override
    protected Map<String, Object> buildParameterMap() {
        Map<String, Object> paramMap = super.buildParameterMap();
        updateValues.forEach((column, value) -> {
            paramMap.put("" + column, value);
        });
        return paramMap;
    }

    public int updateById(T entity) {
        String tableName = getTableName();
        String setClause = buildUpdateSetClause();
        String idClause = getIdFieldName();
        String whereClause = idClause + " = #{" + idClause + "}";
        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
        try {
            JNamedParameterPreparedStatement namedParameterPreparedStatement = new JNamedParameterPreparedStatement(sqlConnection.getConnection(), sql);
            List<JKeyValue> placeholderMap = JSqlFormatter.parsePlaceholders(sql);
            List<JKeyValue> fieldValueMap = JSqlFormatter.getFieldValues(entity, placeholderMap);
            for (JKeyValue entry : fieldValueMap) {
                namedParameterPreparedStatement.setParameter(entry);
            }
            namedParameterPreparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private String buildUpdateSetClause() {
        Field[] fields = entityClass.getDeclaredFields();
        String id = getIdFieldName();
        return Arrays.stream(fields)
                .filter(field -> !field.equals(id)) // 排除主键字段
                .map(field -> getColumnName(field) + " = #{" + field.getName() + "}")
                .collect(Collectors.joining(", "));
    }

}
