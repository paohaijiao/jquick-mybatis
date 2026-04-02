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
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JLogLevel;
import com.github.paohaijiao.model.JCondition;
import com.github.paohaijiao.model.JKeyValue;
import com.github.paohaijiao.model.JOrder;
import com.github.paohaijiao.model.JPage;
import com.github.paohaijiao.statement.JNamedParameterPreparedStatement;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JLambdaQueryPageImpl<T> extends JLambdaBaseImpl<T> {
    private int pageNum = 1;

    private int pageSize = 10;

    public JLambdaQueryPageImpl(Class<T> entityClass, JSqlConnection sqlConnection, int pageNum, int pageSize, List<JCondition> conditions, List<JOrder> orders) {
        this.entityClass = entityClass;
        this.sqlConnection = sqlConnection;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        for (JCondition condition : conditions) {
            this.conditions.add(condition);
        }
        for (JOrder order : orders) {
            this.orders.add(order);
        }
    }

    public JPage<T> page() {
        long total = count();
        int pages = (int) (total / pageSize);
        if (total % pageSize != 0) {
            pages++;
        }
        String sql = buildSelectSQL();
        String pageSql = buildPageSQL(sql);
        List<T> records = new ArrayList<>();
        List<JCondition> conditions = this.conditions;
        try {
            JConsole console = new JConsole();
            console.log(JLogLevel.INFO, pageSql);
            JNamedParameterPreparedStatement namedParameterPreparedStatement = new JNamedParameterPreparedStatement(sqlConnection.getConnection(), pageSql);
            for (int i = 0; i < conditions.size(); i++) {
                JKeyValue model = new JKeyValue();
                model.setNum(i + 1);
                model.setKey(conditions.get(i).getColumn());
                model.setValue(conditions.get(i).getValue());
                namedParameterPreparedStatement.setParameter(model);
            }

            JKeyValue limitModel = new JKeyValue();
            limitModel.setNum(conditions.size() + 1);
            limitModel.setKey("limit");
            limitModel.setValue(pageSize);
            namedParameterPreparedStatement.setParameter(limitModel);
            JKeyValue offsetModel = new JKeyValue();
            offsetModel.setNum(conditions.size() + 2);
            offsetModel.setKey("offset");
            offsetModel.setValue((this.pageNum - 1) * pageSize);
            namedParameterPreparedStatement.setParameter(offsetModel);
            namedParameterPreparedStatement.setParameter(offsetModel);
            ResultSet resultSet = namedParameterPreparedStatement.executeQuery();
            while (resultSet.next()) {
                T t = resultSetToObject(resultSet, entityClass);
                records.add(t);
            }
            return new JPage<T>()
                    .setRecords(records)
                    .setTotal(total)
                    .setSize(pageSize)
                    .setCurrent(pageNum)
                    .setPages(pages);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("failed to conduct page query", exception);
        }
    }

    protected String buildPageSQL(String originalSql) {
        return originalSql + " LIMIT #{limit} OFFSET #{offset}";
    }

    public long count() {
        String originalSql = buildSelectSQL();
        String countSql = "SELECT COUNT(*) FROM (" + originalSql + ") temp_count";
        JConsole console = new JConsole();
        console.log(JLogLevel.INFO, countSql);
        try {
            JNamedParameterPreparedStatement namedParameterPreparedStatement =
                    new JNamedParameterPreparedStatement(sqlConnection.getConnection(), countSql);
            for (int i = 0; i < conditions.size(); i++) {
                JKeyValue model = new JKeyValue();
                model.setNum(i + 1);
                model.setKey(conditions.get(i).getColumn());
                model.setValue(conditions.get(i).getValue());
                namedParameterPreparedStatement.setParameter(model);
            }

            ResultSet resultSet = namedParameterPreparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("failed to query the total count", exception);
        }
    }


}
