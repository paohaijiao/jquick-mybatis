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
package com.github.paohaijiao.connection.impl;

import com.github.paohaijiao.connection.JSqlConnection;
import com.github.paohaijiao.connection.JSqlConnectionFactory;

import javax.sql.DataSource;

public class DefaultSqlConnectionactory implements JSqlConnectionFactory {

    private final DataSource dataSource;


    public DefaultSqlConnectionactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public JSqlConnection openConnection() {
        try {
            return new DefaultSqlConnection(dataSource.getConnection(), true);
        } catch (Exception e) {
            throw new RuntimeException("Cannot open connection", e);
        }
    }

    @Override
    public JSqlConnection openConnection(Boolean autoCommit) {
        try {
            return new DefaultSqlConnection(dataSource.getConnection(), autoCommit);
        } catch (Exception e) {
            throw new RuntimeException("Cannot open connection", e);
        }
    }

    @Override
    public JSqlConnection openConnection(String userName, String password) {
        try {
            return new DefaultSqlConnection(dataSource.getConnection(userName, password), true);
        } catch (Exception e) {
            throw new RuntimeException("Cannot open connection", e);
        }
    }


}
