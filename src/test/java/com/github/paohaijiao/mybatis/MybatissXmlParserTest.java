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
import java.util.*;

import static dm.jdbc.desc.Configuration.user;

public class MybatissXmlParserTest {
    private DataSource getDBConfig() throws ClassNotFoundException, SQLException {
        String userName = "SYSDBA";
        String password = "123";
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
        user.setId(3L);
        user.setUsername("paohaijiao1");
        user.setPassword("123456abc1");
        user.setAge(25);
        user.setEmail("test@example.com");
        user.setStatus(1);
        user.setCreateTime(new Date());
        userApi.insertUser(user);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void batchInsertUser() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user = new User();
        user.setId(5L);
        user.setUsername("paohaijiao");
        user.setPassword("123456abc");
        user.setAge(25);
        user.setEmail("test@example.com");
        user.setStatus(1);
        user.setCreateTime(new Date());
        List<User> list = new ArrayList<>();
        list.add(user);
        User user1 = new User();
        user1.setId(6L);
        user1.setUsername("paohaijiao1");
        user1.setPassword("123456abc1");
        user1.setAge(25);
        user1.setEmail("test@example.com");
        user1.setStatus(1);
        user1.setCreateTime(new Date());
        list.add(user1);
        userApi.batchInsertUser(list);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void deleteUserById() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        userApi.deleteUserById(6L);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void batchDeleteUser() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        List<Long> ids=new ArrayList<>();
        ids.add(6L);
        ids.add(5L);
        userApi.batchDeleteUser(ids);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void updateUser() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user1 = new User();
        user1.setId(3L);
        user1.setUsername("泡海椒");
        user1.setPassword("1qaz@WSX");
        user1.setAge(26);
        user1.setEmail("test@example1.com");
        user1.setStatus(2);
        user1.setCreateTime(new Date());
        userApi.updateUser(user1);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void getUserById() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user1 = userApi.getUserById(3L);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void getUserDetailById() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        Map<String,Object> user1 = userApi.getUserDetailById(3L);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void findUserList() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user=new User();
        List<User> user1 = userApi.findUserList(user);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void findUserMapList() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user=new User();
        user.setUsername("test");
        List<HashMap<String,Object>> user1 = userApi.findUserMapList(user);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void getUserByIds() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user=new User();
        user.setUsername("test");
        List<Long> ids=new ArrayList<>();
        ids.add(6L);
        ids.add(3L);
        List<User> user1 = userApi.getUserByIds(ids);
        handler.close();
        System.out.println(user);
    }
    @Test
    public void getUserByUserNames() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user=new User();
        user.setUsername("test");
        List<User> users=new ArrayList<>();
        User user1=new User();
        user1.setUsername("test1");
        users.add(user1);
        User user2=new User();
        user2.setUsername("泡海椒");
        users.add(user2);
        List<User> userList = userApi.getUserByUserNames(users);
        handler.close();
        System.out.println(userList);
    }
    @Test
    public void getUserByUserNamesAndValue() throws IOException, SQLException, ClassNotFoundException {
        DataSource connection=getDBConfig();
        JQuickMyBatisXmlParseHandler handler=new JQuickMyBatisXmlParseHandler(connection);
        JQuickFactory factory = new JQuickXmlFactory(handler,"mybatis.xml");
        System.out.println(factory);
        UserMapper userApi = factory.createApi(UserMapper.class);
        User user=new User();
        user.setUsername("test");
        List<User> users=new ArrayList<>();
        User user1=new User();
        user1.setUsername("test1");
        users.add(user1);
        User user2=new User();
        user2.setUsername("泡海椒");
        users.add(user2);
        List<User> userList = userApi.getUserByUserNamesAndValue(users);
        handler.close();
        System.out.println(userList);
    }












}
