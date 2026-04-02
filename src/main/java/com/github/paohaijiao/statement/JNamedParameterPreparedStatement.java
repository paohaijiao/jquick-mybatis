package com.github.paohaijiao.statement;

import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.model.JKeyValue;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JNamedParameterPreparedStatement {

    private final Connection connection;

    private final String sql;

    private final LinkedHashMap<String, Integer> parameterIndexes;

    private PreparedStatement stmt = null;

    public JNamedParameterPreparedStatement(Connection connection, String sql) throws SQLException {
        this.connection = connection;
        this.parameterIndexes = new LinkedHashMap<>();
        this.sql = parseNamedParameters(sql);
        System.out.println(this.sql);
        String initialSql = this.sql;
        stmt = connection.prepareStatement(this.sql);
    }

    public static JNamedParameterPreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
        return new JNamedParameterPreparedStatement(conn, sql);
    }

    private String parseNamedParameters(String sql) {
        Pattern pattern = Pattern.compile("#\\{(\\w+)\\}");
        Matcher matcher = pattern.matcher(sql);
        StringBuffer parsedSql = new StringBuffer();
        int index = 1;
        while (matcher.find()) {
            String paramName = matcher.group(1);
            parameterIndexes.put(paramName, index++);
            matcher.appendReplacement(parsedSql, "?");
        }
        matcher.appendTail(parsedSql);
        return parsedSql.toString();
    }

    public void setParameter(JKeyValue keyValue) throws SQLException {
        Object value = keyValue.getValue();
        int index = keyValue.getNum();
        try {
            if (value == null) {
                stmt.setNull(index, Types.NULL);
            } else if (value instanceof String) {
                stmt.setString(index, (String) value);
            } else if (value instanceof Integer) {
                stmt.setInt(index, (Integer) value);
            } else if (value instanceof Long) {
                stmt.setLong(index, (Long) value);
            } else if (value instanceof Double) {
                stmt.setDouble(index, (Double) value);
            } else if (value instanceof Float) {
                stmt.setFloat(index, (Float) value);
            } else if (value instanceof Boolean) {
                stmt.setBoolean(index, (Boolean) value);
            } else if (value instanceof Date) {
                java.sql.Date sqlDate = new java.sql.Date(((Date) value).getTime());
                stmt.setDate(index, sqlDate);
            } else if (value instanceof Timestamp) {
                stmt.setTimestamp(index, (Timestamp) value);
            } else {
                stmt.setObject(index, value);
            }
//            stmt.setString(2,"admin");
//            stmt.setInt(3,12);
//            stmt.setLong(4,1);
//            stmt.execute();

        } catch (Throwable exception) {
            exception.printStackTrace();
        }
    }

    public void setParametersFromObject(Object entity) throws SQLException, IllegalAccessException {
        Class<?> clazz = entity.getClass();
        for (Map.Entry<String, Integer> entry : parameterIndexes.entrySet()) {
            String paramName = entry.getKey();
            try {
                Field field = clazz.getDeclaredField(paramName);
                field.setAccessible(true);
                Object value = field.get(entity);
                //setParameter(paramName, value);
            } catch (NoSuchFieldException e) {
                try {
                    String getterName = "get" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
                    Object value = clazz.getMethod(getterName).invoke(entity);
                    // setParameter(paramName, value);
                } catch (Exception ex) {
                    throw new SQLException("could not get  '" + paramName + "'s Value", ex);
                }
            }
        }
    }

    public ResultSet executeQuery() throws SQLException {
        JAssert.notNull(stmt, "statement is null");
        return stmt.executeQuery();
    }

    public int executeUpdate() throws SQLException {
        if (stmt == null) {
            return 0;
        }
        stmt.execute();
        return 1;
    }
}
