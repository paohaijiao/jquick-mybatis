package com.github.paohaijiao.ds;

public interface JDBConnectionConfig {

    String getDriverClassName();

    String getUrl();

    String getUsername();

    String getPassword();


    default int getMaxPoolSize() {
        return 10;
    }

    default int getConnectionTimeout() {
        return 30000;
    }

    default int getIdleTimeout() {
        return 600000;
    }

    default String getConnectionTestQuery() {
        return "SELECT 1";
    }

    default boolean isAutoCommit() {
        return true;
    }


}
