package com.github.paohaijiao.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface JParameterHandler<T> {

    void handle(PreparedStatement pstmt, T param, int index) throws SQLException;

}
