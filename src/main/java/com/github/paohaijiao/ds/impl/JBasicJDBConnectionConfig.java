package com.github.paohaijiao.ds.impl;

import com.github.paohaijiao.ds.JDBCBaseConnectionConfig;

import java.util.Properties;

public class JBasicJDBConnectionConfig extends JDBCBaseConnectionConfig {

    private String driverClassName;

    private String url;

    private String username;

    private String password;


    public JBasicJDBConnectionConfig(String driverClassName, String url, String username, String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private JBasicJDBConnectionConfig(Properties properties) {
        this.driverClassName = properties.getProperty("driverClassName");
        this.url = properties.getProperty(url);
        this.username = properties.getProperty(username);
        ;
        this.password = properties.getProperty(password);
    }


    @Override
    public String getDriverClassName() {
        return driverClassName;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
