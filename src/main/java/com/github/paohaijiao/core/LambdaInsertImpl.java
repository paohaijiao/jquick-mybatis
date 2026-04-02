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
import com.github.paohaijiao.connection.JSqlConnection;
import com.github.paohaijiao.format.JSqlFormatter;
import com.github.paohaijiao.model.JKeyValue;
import com.github.paohaijiao.statement.JNamedParameterPreparedStatement;
import com.github.paohaijiao.util.JStringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LambdaInsertImpl<T> extends JLambdaBaseImpl<T> {


    public LambdaInsertImpl(Class<T> entityClass, JSqlConnection sqlConnection) {
        this.entityClass = entityClass;
        this.sqlConnection = sqlConnection;
    }

    public String buildInsertSQL() {
        String tableName = getTableName();
        String columns = buildInsertColumns();
        String values = buildInsertValues();
        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
    }

    private String buildInsertColumns() {
        Field[] fields = entityClass.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    JColumn columnAnnotation = field.getAnnotation(JColumn.class);
                    return columnAnnotation != null && !columnAnnotation.value().isEmpty()
                            ? columnAnnotation.value()
                            : JStringUtils.camelToUnderline(field.getName());
                })
                .collect(Collectors.joining(", "));
    }

    private String buildInsertValues() {
        Field[] fields = entityClass.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    String fieldName = field.getName();
                    return "#{" + fieldName + "}";
                })
                .collect(Collectors.joining(", "));
    }

    private Map<String, Object> buildInsertParameterMap(T entity) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("entity", entity);
        return paramMap;
    }

    public int insert(T entity) {
        String sql = buildInsertSQL();
        try {
            JNamedParameterPreparedStatement namedParameterPreparedStatement = new JNamedParameterPreparedStatement(sqlConnection.getConnection(), sql);
            List<JKeyValue> placeholderMap = JSqlFormatter.parsePlaceholders(sql);
            List<JKeyValue> fieldValueMap = JSqlFormatter.getFieldValues(entity, placeholderMap);
            for (JKeyValue entry : fieldValueMap) {
                namedParameterPreparedStatement.setParameter(entry);
            }
            return namedParameterPreparedStatement.executeUpdate();
        } catch (Throwable exception) {
            exception.printStackTrace();
        }
        return 0;
    }


}
