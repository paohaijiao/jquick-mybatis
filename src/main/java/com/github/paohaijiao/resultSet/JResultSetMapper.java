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
package com.github.paohaijiao.resultSet;

import com.github.paohaijiao.function.JTypeHandler;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.resultSet
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/7/8
 */
public class JResultSetMapper {
    public static final Map<Class<?>, JTypeHandler<?>> TYPE_HANDLERS = new HashMap<>();

    static {
        registerHandler(String.class, ResultSet::getString);
        registerHandler(int.class, ResultSet::getInt);
        registerHandler(Integer.class, ResultSet::getInt);
        registerHandler(long.class, ResultSet::getLong);
        registerHandler(Long.class, ResultSet::getLong);
        registerHandler(boolean.class, ResultSet::getBoolean);
        registerHandler(Boolean.class, ResultSet::getBoolean);
        registerHandler(double.class, ResultSet::getDouble);
        registerHandler(Double.class, ResultSet::getDouble);

        registerHandler(Date.class, (rs, col) -> rs.getTimestamp(col));
        registerHandler(LocalDate.class, (rs, col) -> {
            java.sql.Date date = rs.getDate(col);
            return date != null ? date.toLocalDate() : null;
        });
        registerHandler(LocalDateTime.class, (rs, col) -> {
            java.sql.Timestamp timestamp = rs.getTimestamp(col);
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        });
    }

    private static <T> void registerHandler(Class<T> type, JTypeHandler<T> handler) {
        TYPE_HANDLERS.put(type, handler);
    }
}
