package com.github.paohaijiao.ds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class JDBCBaseConnectionConfig implements JDBConnectionConfig {


    public DataSource createDataSource() throws SQLException, ClassNotFoundException {
        String driverClassName = this.getDriverClassName();
        String url = this.getUrl();
        String user = this.getUsername();
        String password = this.getPassword();
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(this.getMaxPoolSize());
        config.setConnectionTimeout(this.getConnectionTimeout());
        config.setIdleTimeout(this.getIdleTimeout());
        config.setConnectionTestQuery(this.getConnectionTestQuery());
        config.setAutoCommit(this.isAutoCommit());
        return new HikariDataSource(config);
    }


    public boolean validate() {
        return getDriverClassName() != null
                && !getDriverClassName().isEmpty()
                && getUrl() != null
                && !getUrl().isEmpty();
    }

}
