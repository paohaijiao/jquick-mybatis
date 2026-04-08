package com.github.paohaijiao.xml;

import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.type.JTypeReference;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JQuickMyBatisXmlInvocationHandler extends JQuickXmlInvocationHandler {

    private Connection connection;


    public JQuickMyBatisXmlInvocationHandler() {
    }

    public JQuickMyBatisXmlInvocationHandler(Connection connection) {
        this.connection = connection;
    }

    public void setDataSource(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected Object loadResult(String sql, JContext jContext, Method method) {
        JAssert.notNull(connection, "Connection is null");
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            Class<?> returnType = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();
            if (returnType.equals(Void.TYPE) || returnType.equals(java.lang.Void.class)) {
                return null;
            }
            JTypeReference<?> typeRef = createTypeReference(genericReturnType);
            ResultSet rs = stmt.executeQuery();
            return JdbcResultMapper.mapResultSet(rs, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
