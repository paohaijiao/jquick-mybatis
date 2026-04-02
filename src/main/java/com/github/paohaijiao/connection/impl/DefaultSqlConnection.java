package com.github.paohaijiao.connection.impl;

import com.github.paohaijiao.connection.JSqlConnection;

import java.sql.Connection;

public class DefaultSqlConnection implements JSqlConnection {

    private Connection connection;

    private Boolean autoCommit = true;

    public DefaultSqlConnection(Connection connection) {
        this.connection = connection;
        this.autoCommit = true;
    }

    public DefaultSqlConnection(Connection connection, Boolean autoCommit) {
        this.connection = connection;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public Boolean getAutoCommit() {
        return this.autoCommit;
    }
}
