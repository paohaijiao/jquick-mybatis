package com.github.paohaijiao.ds.impl;

import com.github.paohaijiao.ds.JQuickConnection;

import java.util.Properties;

public class JBasicConnection extends JQuickConnection {

    private String driverClassName;

    private String url;

    private String username;

    private String password;


    public JBasicConnection(String driverClassName, String url, String username, String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private JBasicConnection(Properties properties) {
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
