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
package com.github.paohaijiao.param;

import com.github.paohaijiao.function.JParameterHandler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.param
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/7/8
 */
public class JPreparedStatementMapper {

    public static final Map<Class<?>, JParameterHandler<?>> PARAM_HANDLERS = new HashMap<>();

    static {
        registerHandler(String.class, (pstmt, param, index) -> pstmt.setString(index, (String) param));
        registerHandler(int.class, (pstmt, param, index) -> pstmt.setInt(index, (int) param));
        registerHandler(Integer.class, (pstmt, param, index) -> pstmt.setInt(index, (Integer) param));
        registerHandler(long.class, (pstmt, param, index) -> pstmt.setLong(index, (long) param));
        registerHandler(Long.class, (pstmt, param, index) -> pstmt.setLong(index, (Long) param));
        registerHandler(boolean.class, (pstmt, param, index) -> pstmt.setBoolean(index, (boolean) param));
        registerHandler(Boolean.class, (pstmt, param, index) -> pstmt.setBoolean(index, (Boolean) param));
        registerHandler(double.class, (pstmt, param, index) -> pstmt.setDouble(index, (double) param));
        registerHandler(Double.class, (pstmt, param, index) -> pstmt.setDouble(index, (Double) param));
        registerHandler(float.class, (pstmt, param, index) -> pstmt.setFloat(index, (float) param));
        registerHandler(Float.class, (pstmt, param, index) -> pstmt.setFloat(index, (Float) param));
        registerHandler(short.class, (pstmt, param, index) -> pstmt.setShort(index, (short) param));
        registerHandler(Short.class, (pstmt, param, index) -> pstmt.setShort(index, (Short) param));
        registerHandler(byte.class, (pstmt, param, index) -> pstmt.setByte(index, (byte) param));
        registerHandler(Byte.class, (pstmt, param, index) -> pstmt.setByte(index, (Byte) param));

        registerHandler(BigDecimal.class, (pstmt, param, index) -> pstmt.setBigDecimal(index, (BigDecimal) param));
        registerHandler(Date.class, (pstmt, param, index) -> pstmt.setTimestamp(index, new Timestamp(((Date) param).getTime())));
        //     registerHandler(LocalDate.class, (pstmt, param, index) -> pstmt.setDate(index, Date.parse( param.toString()));
        registerHandler(LocalDateTime.class, (pstmt, param, index) -> pstmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) param)));
        registerHandler(byte[].class, (pstmt, param, index) -> pstmt.setBytes(index, (byte[]) param));
    }

    public static <T> void registerHandler(Class<T> type, JParameterHandler<T> handler) {
        PARAM_HANDLERS.put(type, handler);
    }
}
