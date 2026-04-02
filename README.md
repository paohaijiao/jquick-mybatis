# JQuickMybatis - 基于JDBC的Java ORM框架

## 📖 项目简介

JQuickMybatis 是面向 Java 开发者的轻量级、无侵入 ORM 框架。它深度适配 Java 8+ 特性，核心优势在于**无需 XML 配置、无需注解、无需继承任何基类**——只需定义普通 POJO 实体类，框架即可自动完成 CRUD 操作。

核心特点：
- **零配置**：无需任何 XML 或注解，实体类即表结构
- **Lambda 风格**：类型安全的查询条件，避免字符串硬编码
- **轻量级**：仅依赖 JDBC，无任何第三方 ORM 框架依赖
- **灵活可控**：不管理数据源，开发者完全控制连接生命周期

## 📊 项目状态

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

## 🚀 核心特性

- **零注解零配置**：实体类即表，无需任何配置
- **Lambda条件构造器**：类型安全的查询条件，编译期检查
- **自动映射**：下划线转驼峰自动映射（user_name → userName）
- **连接自主管理**：框架不管理数据源，开发者完全控制
- **轻量级设计**：仅依赖JDBC，适合嵌入式/小规模项目
- **灵活SQL支持**：支持原生SQL和参数化查询

## 📦 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>io.github.paohaijiao</groupId>
    <artifactId>jquick-mybatis</artifactId>
    <version>最新版本</version>
</dependency>
```
### 2. 定义实体类

```java
public class JUser {
    private Long id;      
    private String name;  
    private Integer age;  
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
```
### 3. 配置数据库连接
```java
        JDBCBaseConnectionConfig config = new JBasicJDBConnectionConfig(
            "com.mysql.cj.jdbc.Driver",          
            "jdbc:mysql://localhost:3306/test?serverTimezone=UTC", 
            "root",                               
            "13579admin"                       
        );
```
### 4. 开始使用

```java
        // 1. 创建连接工厂
        JSqlConnectionFactory sqlSessionFactory = new DefaultSqlConnectionFactory(config);
        // 2. 创建Mapper工厂
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        // 3. 获取实体Mapper
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        // 4. 执行CRUD操作
        JUser user = new JUser();
        user.setName("张三");
        user.setAge(25);
        userMapper.insert(user);  // 插入
        JUser queryUser = userMapper.selectById(1L);  
        userMapper.updateById(user);  
        userMapper.deleteById(1L);    
```

## 🔧 详细功能示例
### 一、基础CRUD

#### 1. 插入数据 (insert)
```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo=new JUser();
        userPo.setId(3L);
        userPo.setName("kimoo");
        userPo.setAge(12);
        int i=userMapper.insert(userPo);
        System.out.println(i);
```

#### 2. 根据ID更新 (updateById)

```java
   JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo=new JUser();
        userPo.setId(1L);
        userPo.setName("kimoo1");
        userPo.setAge(12);
        int i=userMapper.updateById(userPo);
        System.out.println(i);
```

#### 3. 根据ID查询 (selectById)

```java
Map<String, JMappedStatement> map=new HashMap<>();
JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
JUser userPo=userMapper.selectById(1);
System.out.println(userPo);
```

#### 4. 根据ID删除 (deleteById)

```java
JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
int   userPo=userMapper.deleteById(1);
System.out.println(userPo);
```
### 二、条件查询 (Lambda Query)
#### 5. 基础条件查询 (query)

```java
    JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> list=userMapper.query().eq(JUser::getAge,12).list();
        System.out.println(list.size());
```

7. update

```java
      JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        userMapper.update().eq(JUser::getId,1).set(JUser::getAge,18).set(JUser::getName,"admin").execute();
        System.out.println("update");
```

8. sql

```java
  JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        String sql="select * from j_user where id>?";
        HashMap<String,Object> map=new HashMap<>();
        map.put("id",1L);
        List<JUser> list=userMapper.select(sql,map);
        System.out.println(list.size());
```

9. sql

```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        String sql="select * from j_user where id>?";
        JParam param=new JParam();
        param.setIndex(1);
        param.setValue(4L);
        List<JUser> list=userMapper.select(sql, Arrays.asList(param));
        System.out.println(list.size());
```

10. page

```java
 JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JPage<JUser> list=userMapper.page().orderByDesc(JUser::getAge).page(1,10);
        System.out.println(list);
```

11. select one

```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser list=userMapper.query().orderByDesc(JUser::getAge).one();
        System.out.println(list);
```

12. count

```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        Long count=userMapper.query().orderByDesc(JUser::getAge).count();
        System.out.println(count);
```

13. like

```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().like(JUser::getName,"张").list();
        System.out.println(count);
```

14. eq

```java
      JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().eq(JUser::getName,"张").list();
        System.out.println(count);
```

15. ne

```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().ne(JUser::getName,"张三").list();
        System.out.println(count);
```

16. gt

```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().gt(JUser::getAge,40).list();
        System.out.println(count);
```

17. ge

```java
    JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
    JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
    JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
    List<JUser> count=userMapper.query().ge(JUser::getAge,40).list();
    System.out.println(count);
```

18. ge

```java
       JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().lt(JUser::getAge,40).list();
        System.out.println(count);
```

18. le

```java
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> count=userMapper.query().le(JUser::getAge,40).list();
        System.out.println(count);
```
