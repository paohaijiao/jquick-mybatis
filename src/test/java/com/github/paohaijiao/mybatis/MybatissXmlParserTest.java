package com.github.paohaijiao.mybatis;

import com.example.mapper.UserMapper;
import com.github.paohaijiao.ds.JQuickConnection;
import com.github.paohaijiao.ds.impl.JBasicConnection;
import com.github.paohaijiao.model.User;
import com.github.paohaijiao.xml.JQuickMyBatisXmlParseHandler;
import com.github.paohaijiao.xml.factory.JQuickFactory;
import com.github.paohaijiao.xml.factory.JQuickXmlFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public class MybatissXmlParserTest {
    private DataSource getDBConfig() throws ClassNotFoundException, SQLException {
        String userName = "SYSDBA";
        String password = "cfbj";
        String clazz = "dm.jdbc.driver.DmDriver";
        String url = "jdbc:dm://127.0.0.1:5236";
        JQuickConnection config = new JBasicConnection(clazz, url, userName, password);
        return config.createDataSource();
    }
    @Test
    public void insert() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");
        user.setPassword("123456abc");
        user.setAge(25);
        user.setEmail("test@example.com");
        user.setStatus(1);
        userApi.insertUser(user);
        handler.close();
        System.out.println(user);
    }
}
