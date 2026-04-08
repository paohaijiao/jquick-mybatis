package com.github.paohaijiao.xml;

import com.github.paohaijiao.ds.JQuickConnection;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.xml.element.JQuickXmlElement;
import com.github.paohaijiao.xml.handler.JQuickParseHandler;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JQuickMyBatisXmlParseHandler implements JQuickParseHandler {

    private static ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    public JQuickMyBatisXmlParseHandler(DataSource dataSource){
        JAssert.notNull(dataSource,"the database connection require not null");
        try {
            if(CONNECTION_HOLDER.get()==null){
                Connection connection=dataSource.getConnection();
                CONNECTION_HOLDER.set(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public JQuickXmlElement createJQuickXmlElement() {
        return new JQuickMyBatisXmlElement();
    }

    @Override
    public JQuickXmlInvocationHandler createlInvocationHandler(){
        JAssert.notNull(CONNECTION_HOLDER.get(),"the connection require not null");
        return new JQuickMyBatisXmlInvocationHandler(CONNECTION_HOLDER.get());
    }
    public void close(){
        Connection connection = CONNECTION_HOLDER.get();
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
        CONNECTION_HOLDER = null;
        CONNECTION_HOLDER = new ThreadLocal<>();
    }
}
