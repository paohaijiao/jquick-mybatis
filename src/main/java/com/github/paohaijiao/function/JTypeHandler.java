package com.github.paohaijiao.function;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface JTypeHandler<T> {

    T handle(ResultSet rs, String columnName) throws SQLException;


}
