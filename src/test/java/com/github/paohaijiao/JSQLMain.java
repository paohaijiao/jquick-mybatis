/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao;

import com.github.paohaijiao.connection.JSqlConnectionFactory;
import com.github.paohaijiao.connection.impl.DefaultSqlConnectionactory;
import com.github.paohaijiao.ds.JDBCBaseConnectionConfig;
import com.github.paohaijiao.ds.impl.JBasicJDBConnectionConfig;
import com.github.paohaijiao.factory.JLambdaMapperFactory;
import com.github.paohaijiao.mapper.JLambdaMapper;
import com.github.paohaijiao.model.JPage;
import com.github.paohaijiao.model.JParam;
import com.github.paohaijiao.model.JUser;
import com.github.paohaijiao.support.JMappedStatement;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSQLMain {

    private DataSource getDBConfig() throws ClassNotFoundException, SQLException {
        String userName = "root";
        String password = "13579admin";
        String clazz = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://192.168.32.132:3306/test?serverTimezone=UTC";
        JDBCBaseConnectionConfig config = new JBasicJDBConnectionConfig(clazz, url, userName, password);
        return config.createDataSource();
    }


    @Test
    public void insert() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo = new JUser();
        userPo.setId(3L);
        userPo.setName("kimoo");
        userPo.setAge(12);
        int i = userMapper.insert(userPo);
        System.out.println(i);
    }

    @Test
    public void updateById() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo = new JUser();
        userPo.setId(1L);
        userPo.setName("kimoo1");
        userPo.setAge(12);
        int i = userMapper.updateById(userPo);
        System.out.println(i);
    }

    @Test
    public void selectById() throws IOException, SQLException, ClassNotFoundException {
        Map<String, JMappedStatement> map = new HashMap<>();
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo = userMapper.selectById(1);
        System.out.println(userPo);
    }

    @Test
    public void deleteById() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        int userPo = userMapper.deleteById(1);
        System.out.println(userPo);
    }

    @Test
    public void query() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> list = userMapper.query().eq(JUser::getAge, 12).list();
        System.out.println(list.size());
    }

    @Test
    public void update() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        userMapper.update().eq(JUser::getId, 1).set(JUser::getAge, 18).set(JUser::getName, "admin").execute();
        System.out.println("update");
    }

    @Test
    public void sql() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        String sql = "select * from j_user where id>?";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        List<JUser> list = userMapper.select(sql, map);
        System.out.println(list.size());
    }

    @Test
    public void sql1() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        String sql = "select * from j_user where id>?";
        JParam param = new JParam();
        param.setIndex(1);
        param.setValue(4L);
        List<JUser> list = userMapper.select(sql, Arrays.asList(param));
        System.out.println(list.size());
    }

    @Test
    public void page() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JPage<JUser> list = userMapper.page().orderByDesc(JUser::getAge).page(1, 10);
        System.out.println(list);
    }

    @Test
    public void one() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser list = userMapper.query().orderByDesc(JUser::getAge).one();
        System.out.println(list);
    }

    @Test
    public void count() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        Long count = userMapper.query().orderByDesc(JUser::getAge).count();
        System.out.println(count);
    }

    @Test
    public void like() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count = userMapper.query().like(JUser::getName, "张").list();
        System.out.println(count);
    }

    @Test
    public void eq() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count = userMapper.query().eq(JUser::getName, "张").list();
        System.out.println(count);
    }

    @Test
    public void ne() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count = userMapper.query().ne(JUser::getName, "张三").list();
        System.out.println(count);
    }

    @Test
    public void gt() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count = userMapper.query().gt(JUser::getAge, 40).list();
        System.out.println(count);
    }

    @Test
    public void ge() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count = userMapper.query().ge(JUser::getAge, 40).list();
        System.out.println(count);
    }

    @Test
    public void lt() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count = userMapper.query().lt(JUser::getAge, 40).list();
        System.out.println(count);
    }

    @Test
    public void le() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count = userMapper.query().le(JUser::getAge, 40).list();
        System.out.println(count);
    }


}
