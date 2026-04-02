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
import com.github.paohaijiao.model.JKeyValue;
import com.github.paohaijiao.statement.JNamedParameterPreparedStatement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LambdaDeleteImpl<T> extends JLambdaBaseImpl<T> {
    private final Map<String, Object> updateValues = new HashMap<>();

    public LambdaDeleteImpl(Class<T> entityClass, JSqlConnection sqlConnection) {
        this.entityClass = entityClass;
        this.sqlConnection = sqlConnection;
    }

    public int deleteById(Serializable id) {
        String deleteSql = "delete  from  %s  where %s = %s";
        String tableName = getTableName();
        String idClause = this.getIdFieldName();
        String value = "#{" + idClause + "}";
        String sql = String.format(deleteSql, tableName, idClause, value);
        try {
            JNamedParameterPreparedStatement namedParameterPreparedStatement = new JNamedParameterPreparedStatement(sqlConnection.getConnection(), sql);
            JKeyValue model = new JKeyValue();
            model.setNum(1);
            model.setKey(idClause);
            model.setValue(id);
            namedParameterPreparedStatement.setParameter(model);
            return namedParameterPreparedStatement.executeUpdate();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }


}
