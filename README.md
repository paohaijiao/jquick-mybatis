# JQuickMybatis - 基于JDBC的Java ORM框架

## 📖 项目简介

JQuickMybatis 是面向 Java 开发者的轻量级、无侵入 ORM 框架。它深度适配 Java 8+ 特性，核心优势在于**无需 XML 配置、无需注解、无需继承任何基类**——只需定义普通 POJO 实体类，框架即可自动完成 CRUD 操作。

同时，框架还支持 **XML 映射文件配置**，提供类似 MyBatis 的动态 SQL 能力（if、foreach 等标签），满足复杂查询场景。

### 核心特点

- **零配置**：无需任何 XML 或注解，实体类即表结构
- **Lambda 风格**：类型安全的查询条件，避免字符串硬编码
- **XML 映射支持**：动态 SQL、foreach、if 标签等
- **轻量级**：仅依赖 JDBC，无任何第三方 ORM 框架依赖
- **灵活可控**：不管理数据源，开发者完全控制连接生命周期

### 📊 项目状态

[![License](https://img.shields.io/badge/License-Apache%202.0-5470c6.svg)](https://opensource.org/licenses/Apache-2.0)
[![Version](https://img.shields.io/badge/Version-1.0.0-91cc75.svg)](https://github.com/paohaijiao/jquick-mybatis/releases)
[![Build](https://img.shields.io/badge/Build-Passing-ee6666.svg)](https://github.com/paohaijiao/jquick-mybatis/actions)
[![Coverage](https://img.shields.io/badge/Coverage-88%25-73c0de.svg)](https://github.com/paohaijiao/jquick-mybatis)

## 🌟 核心标签

⚡ 零配置 | 🎯 Lambda风格 | 🔧 轻量级 | 📦 无侵入

## 📖 目录

- [✨ 特性介绍](#特性介绍)
- [🚀 快速开始](#快速开始)
- [🔧 核心功能](#核心功能)
    - [基础CRUD](#基础crud)
    - [条件查询](#条件查询)
    - [更新操作](#更新操作)
    - [分页查询](#分页查询)
    - [原生SQL](#原生sql)
- [📝 实体类规范](#实体类规范)
- [🔍 常见问题](#常见问题)
- [📄 许可证](#许可证)
## 📖 目录

1. [快速开始](#1-快速开始)
2. [Lambda 风格 CRUD](#2-lambda-风格-crud)
3. [条件查询](#3-条件查询)
4. [更新操作](#4-更新操作)
5. [分页查询](#5-分页查询)
6. [原生 SQL](#6-原生-sql)
7. [XML 映射配置](#7-xml-映射配置)
8. [实体类规范](#8-实体类规范)
9. [常见问题](#9-常见问题)

## 🚀 核心特性

- **零注解零配置**：实体类即表，无需任何配置
- **Lambda条件构造器**：类型安全的查询条件，编译期检查
- **自动映射**：下划线转驼峰自动映射（user_name → userName）
- **连接自主管理**：框架不管理数据源，开发者完全控制
- **轻量级设计**：仅依赖JDBC，适合嵌入式/小规模项目
- **灵活SQL支持**：支持原生SQL和参数化查询

## 📦 快速开始
### 1.1 添加依赖
```xml
<dependency>
  <groupId>io.github.paohaijiao</groupId>
  <artifactId>jquick-mybatis</artifactId>
  <version>版本号</version>
</dependency>
```

### 1.2 添加依赖
```java
// 方式一：使用注解指定表名和列名
import com.github.paohaijiao.anno.JColumn;
import com.github.paohaijiao.anno.JTable;
import lombok.Data;
@JTable("test.t_user")
@Data
public class JUser {
  @JColumn(id = true)  
  private Long id;

  @JColumn("name")     
  private String name;

  private Integer age; 
}

// 方式二：纯 POJO
@Data
public class User {
    
  private Long id;
  
  private String userName; 
  
  private Integer age;
}
```
### 1.3 配置数据源
```java
import com.github.paohaijiao.ds.JQuickConnection;
import com.github.paohaijiao.ds.impl.JBasicConnection;
import javax.sql.DataSource;
JQuickConnection connection = new JBasicConnection(
        "com.mysql.cj.jdbc.Driver",                    
        "jdbc:mysql://localhost:3306/test?serverTimezone=UTC", 
        "root",                                        
        "password"             
);
DataSource dataSource = connection.createDataSource();
```
### 1.4 开始使用

```java
      @Test
      public void insert() throws Exception {
        DataSource dataSource = getDBConfig();
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(dataSource);
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser user = new JUser();
        user.setId(3L);
        user.setName("kimoo");
        user.setAge(12);
        int rows = userMapper.insert(user);
        System.out.println("影响行数: " + rows);
        userMapper.close();
      }
```

## 🔧 2. Lambda 风格 CRUD
### 2.1 插入数据 (insert)
```java
      @Test
      public void insert() throws Exception {
        DataSource dataSource = getDBConfig();
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(dataSource);
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser user = new JUser();
        user.setId(3L);
        user.setName("kimoo");
        user.setAge(12);
        int rows = userMapper.insert(user);
        System.out.println("影响行数: " + rows);
        userMapper.close();
      }
```

### 2.2 根据 ID 更新 (updateById)

```java
      @Test
      public void updateById() throws Exception {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser user = new JUser();
        user.setId(1L);
        user.setName("kimoo1");
        user.setAge(12);
        int rows = userMapper.updateById(user);
        System.out.println("更新行数: " + rows);
      }
```

### 2.3 根据 ID 查询 (selectById)
```java
      @Test
      public void selectById() throws Exception {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser user = userMapper.selectById(1L);
        System.out.println(user);
      }
```

#### 2.4 根据 ID 删除 (deleteById)

```java
      @Test
      public void deleteById() throws Exception {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        int rows = userMapper.deleteById(1L);
        System.out.println("删除行数: " + rows);
      }
```

## 3. 条件查询
### 3.1 基础条件查询 (query)
```java
      @Test
      public void query() throws Exception {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> list = userMapper.query()
                .eq(JUser::getAge, 12)
                .list();
        System.out.println("查询结果数: " + list.size());
      }
```

### 3.2 等于 (eq)

```java
      @Test
      public void eq() throws Exception {
        JLambdaMapper<JUser> userMapper = getMapper();
        List<JUser> list = userMapper.query()
                .eq(JUser::getName, "张三")
                .list();
        System.out.println(list);
      }
```

### 3.3 不等于 (ne)
```java
    @Test
    public void ne() throws Exception {
      JLambdaMapper<JUser> userMapper = getMapper();
    
      List<JUser> list = userMapper.query()
              .ne(JUser::getName, "张三")
              .list();
    }
```

### 3.4 大于 (gt) / 大于等于 (ge)
```java
      @Test
      public void gt_ge() throws Exception {
        JLambdaMapper<JUser> userMapper = getMapper();
        List<JUser> gtList = userMapper.query()
                .gt(JUser::getAge, 40)
                .list();
        List<JUser> geList = userMapper.query()
                .ge(JUser::getAge, 40)
                .list();
      }
```

### 3.5 小于 (lt) / 小于等于 (le)

```java
      @Test
      public void lt_le() throws Exception {
        JLambdaMapper<JUser> userMapper = getMapper();
        List<JUser> ltList = userMapper.query()
                .lt(JUser::getAge, 40)
                .list();
        List<JUser> leList = userMapper.query()
                .le(JUser::getAge, 40)
                .list();
      }
```

### 3.6 模糊查询 (like)

```java
      @Test
      public void like() throws Exception {
        JLambdaMapper<JUser> userMapper = getMapper();
        List<JUser> list = userMapper.query()
                .like(JUser::getName, "张")
                .list();
      }
```


### 3.7 查询单条记录 (one)

```java
      @Test
      public void one() throws Exception {
        JLambdaMapper<JUser> userMapper = getMapper();
        JUser user = userMapper.query()
                .orderByDesc(JUser::getAge)
                .one();
        System.out.println(user);
      }
```

### 3.8 统计数量 (count)
```java
      @Test
      public void count() throws Exception {
        JLambdaMapper<JUser> userMapper = getMapper();
        Long count = userMapper.query()
                .orderByDesc(JUser::getAge)
                .count();
        System.out.println("总记录数: " + count);
      }
```
## 4. 更新操作
### 4.1 Lambda Update 更新
```java
      @Test
      public void update() throws Exception {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        userMapper.update()
                .eq(JUser::getId, 1)
                .set(JUser::getAge, 18)
                .set(JUser::getName, "admin")
                .execute();
        System.out.println("更新完成");
      }
```
## 5. 分页查询
### 5.1 分页
```java
      @Test
      public void update() throws Exception {
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JPage<JUser> page = userMapper.page()
                .orderByDesc(JUser::getAge)
                .page(1, 10);
        System.out.println("总记录数: " + page.getTotal());
        System.out.println("当前页数据: " + page.getRecords());
      }
```
## 6. 原生 SQL
### 6.1 使用 Map 参数
```java
      @Test
      public void sqlWithMap() throws Exception {
        JLambdaMapper<JUser> userMapper = getMapper();
        String sql = "select * from j_user where id > ?";
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        List<JUser> list = userMapper.select(sql, params);
        System.out.println(list.size());
      }
```
### 6.2 使用 JParam 参数列表
```java
      @Test
      public void sqlWithJParam() throws Exception {
        JLambdaMapper<JUser> userMapper = getMapper();
        String sql = "select * from j_user where id > ?";
        JParam param = new JParam();
        param.setIndex(1);
        param.setValue(4L);
        List<JUser> list = userMapper.select(sql, Arrays.asList(param));
        System.out.println(list.size());
      }

```

## 7. XML 映射配置
### 7.1 XML 配置文件示例 (mybatis.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//JQUICK//DTD MYBATIS MAPPER 1.0//EN"
        "classpath:paohaijiao/dtd/Jquick-mybatis.dtd">
<mapper namespace="com.example.mapper.UserMapper">
  <insert name="insertUser" paramClass="com.github.paohaijiao.model.User">
        INSERT INTO test.t_user (
            <if test="user.id != null">id,</if>
            <if test="user.username != null">username,</if>
            <if test="user.password != null">password,</if>
            <if test="user.age != null">age,</if>
            <if test="user.email != null">email,</if>
            <if test="user.status != null">status,</if>
            <if test="user.createTime != null">create_time</if>
        ) VALUES (
            <if test="user.id != null">#{user.id},</if>
            <if test="user.username != null">#{user.username},</if>
            <if test="user.password != null">#{user.password},</if>
            <if test="user.age != null">#{user.age},</if>
            <if test="user.email != null">#{user.email},</if>
            <if test="user.status != null">#{user.status},</if>
            <if test="user.createTime != null">#{user.createTime}</if>
        )
    </insert>
    <insert name="batchInsertUser">
        INSERT INTO test.t_user (id, username, password, age, email, status)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.id}, #{item.username}, #{item.password}, #{item.age}, #{item.email}, #{item.status})
        </foreach>
    </insert>
    <delete name="deleteUserById" paramClass="java.lang.Long">
        DELETE FROM test.t_user WHERE id = #{id}
    </delete>
    <delete name="batchDeleteUser">
        DELETE FROM test.t_user WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </delete>
    <update name="updateUser" paramClass="com.github.paohaijiao.model.User">
        UPDATE test.t_user
        SET id = #{user.id}
            <if test="user.username != null">, username = #{user.username}</if>
            <if test="user.password != null">, password = #{user.password}</if>
            <if test="user.age != null">, age = #{user.age}</if>
            <if test="user.email != null">, email = #{user.email}</if>
            <if test="user.status != null">, status = #{user.status}</if>
        WHERE id = #{user.id}
    </update>
    <select name="getUserById" returnClass="com.github.paohaijiao.model.User">
        SELECT * FROM test.t_user WHERE id = #{id}
    </select>
    <select name="getUserDetailById" returnClass="java.util.Map">
        SELECT * FROM test.t_user WHERE id = #{id}
    </select>
    <select name="findUserList" paramClass="com.github.paohaijiao.model.User" returnClass="java.util.List">
        SELECT * FROM test.t_user
        WHERE 1=1
            <if test="username != null and username != ''">
                AND username LIKE CONCAT('%', #{username}, '%')
            </if>
            <if test="age != null">
                AND age = #{age}
            </if>
            <if test="email != null and email != ''">
                AND email = #{email}
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
        ORDER BY id DESC
    </select>
    <select name="getUserByIds" returnClass="java.util.List">
        SELECT * FROM test.t_user WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </select>
   <select name="page" returnClass="java.util.List">
        SELECT * FROM test.t_user WHERE username IN
        <foreach collection="users" item="user" open="(" separator="," close=")">
            #{user.username}
        </foreach>
    </select>
</mapper>
```
### 7.2 定义 Mapper 接口
```java
      package com.example.mapper;
      import com.github.paohaijiao.anno.JPageParam;
      import com.github.paohaijiao.model.JPage;
      import com.github.paohaijiao.model.User;
      import com.github.paohaijiao.xml.param.Param;
      import java.util.List;
      import java.util.Map;
      public interface UserMapper {
          
        void insertUser(@Param("user") User user);
        
        void batchInsertUser(@Param("list") List<User> list);
        
        void deleteUserById(@Param("id") Long id);
        
        void batchDeleteUser(@Param("ids") List<Long> ids);
      
        void updateUser(@Param("user") User user);
      
        User getUserById(@Param("id") Long id);
      
        Map<String, Object> getUserDetailById(@Param("id") Long id);
      
        List<User> findUserList(@Param("user") User user);
      
        List<HashMap<String, Object>> findUserMapList(@Param("user") User user);
      
        List<User> getUserByIds(@Param("ids") List<Long> ids);
      
        List<User> getUserByUserNames(@Param("users") List<User> users);
      
        JPage<User> page(@JPageParam("pageNum") Integer pageNum, @JPageParam("pageSize") Integer pageSize, @Param("users") List<User> users);
      
      }
```
### 7.3 使用 XML 映射
```java
      @Test
      public void testXmlMapper() throws Exception {
          DataSource dataSource = getDBConfig();
          JQuickMyBatisXmlParseHandler handler = new JQuickMyBatisXmlParseHandler(dataSource);
          JQuickFactory factory = new JQuickXmlFactory(handler, "mybatis.xml");
          UserMapper userMapper = factory.createApi(UserMapper.class);
          User user = new User();
          user.setId(8L);
          user.setUsername("paohaijiao");
          user.setPassword("123456");
          user.setAge(25);
          user.setEmail("test@example.com");
          user.setStatus(1);
          user.setCreateTime(new Date());
          userMapper.insertUser(user);
          User result = userMapper.getUserById(8L);
          System.out.println(result);
          List<User> userList = new ArrayList<>();
          userList.add(user);
          userMapper.batchInsertUser(userList);
          List<Long> ids = Arrays.asList(8L, 9L);
          userMapper.batchDeleteUser(ids);
          handler.close();
      }

```
### 7.4 XML 分页查询示例
```java
      @Test
      public void testPage() throws Exception {
        DataSource dataSource = getDBConfig();
        JQuickMyBatisXmlParseHandler handler = new JQuickMyBatisXmlParseHandler(dataSource);
        JQuickFactory factory = new JQuickXmlFactory(handler, "mybatis.xml");
        UserMapper userMapper = factory.createApi(UserMapper.class);
        List<User> users = new ArrayList<>();
        User condition1 = new User();
        condition1.setUsername("测试人员");
        users.add(condition1);
        User condition2 = new User();
        condition2.setUsername("paohaijiao");
        users.add(condition2);
        JPage<User> page = userMapper.page(1, 4, users);
        System.out.println("总记录数: " + page.getTotal());
        System.out.println("当前页数据: " + page.getRecords());
        handler.close();
      }

```

## 8. 常见问题

### Q1: 如何处理下划线转驼峰映射？

框架默认支持驼峰转换，数据库列名 `user_name` 会自动映射到 Java 属性 `userName`。

### Q2: 如何指定主键？

使用 `@JColumn(id = true)` 注解标识主键字段。

```java
@JTable("test.t_user")
public class User {
    
    @JColumn(id = true)
    private Long id;
    
    private String userName;
}
```

### Q3: 是否支持事务？
```string  
框架不管理事务，开发者可以通过 JDBC 的 Connection 手动控制事务：
```
```java
Connection conn = dataSource.getConnection();
      try {
          conn.setAutoCommit(false);
          // 执行数据库操作
          userMapper.insert(user);
          conn.commit();
      } catch (Exception e) {
          conn.rollback();
      } finally {
          conn.close();
      }
```
### Q4: XML 映射文件中的参数引用格式？
| 参数类型 | 格式 | 示例 |
|---------|------|------|
| 简单参数 | `#{paramName}` | `#{id}` |
| 对象属性 | `#{object.property}` | `#{user.username}` |
| 集合元素 | `#{item.property}` | `#{item.username}` |
```xml
    <!-- 简单参数 -->
    <select name="getUserById" paramClass="java.lang.Long">
        SELECT * FROM t_user WHERE id = #{id}
    </select>
    
    <!-- 对象属性 -->
    <insert name="insertUser" paramClass="com.example.User">
        INSERT INTO t_user (username) VALUES (#{user.username})
    </insert>
    
    <!-- 集合元素 -->
    <foreach collection="list" item="item">
        #{item.username}
    </foreach>
```

### Q5: 如何切换数据库？
```string  
只需更换驱动类和 URL 即可，框架会自动适配不同数据库的方言。
```
```java
      JBasicConnection config = new JBasicConnection(
                      "com.mysql.cj.jdbc.Driver",
                      "jdbc:mysql://localhost:3306/test",
                      "root", "password"
              );
      JBasicConnection config = new JBasicConnection(
              "oracle.jdbc.driver.OracleDriver",
              "jdbc:oracle:thin:@localhost:1521:xe",
              "scott", "tiger"
      );
      
      JBasicConnection config = new JBasicConnection(
              "org.postgresql.Driver",
              "jdbc:postgresql://localhost:5432/test",
              "postgres", "password"
      );
```

### Q6: 如何关闭资源？
```string  
    使用完 Mapper 后，调用 mapper.close() 方法释放资源。对于 XML 方式，调用 handler.close()。
```
```java
    // Lambda 方式
    JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
    try {
            // 执行数据库操作
            userMapper.insert(user);
    } finally {
            userMapper.close();
    }
    // XML 方式
    JQuickMyBatisXmlParseHandler handler = new JQuickMyBatisXmlParseHandler(dataSource);
    try {
    UserMapper userMapper = factory.createApi(UserMapper.class);
    // 执行数据库操作
    } finally {
            handler.close();
    }
```