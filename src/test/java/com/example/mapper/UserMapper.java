package com.example.mapper;

import com.github.paohaijiao.anno.JPageParam;
import com.github.paohaijiao.model.JPage;
import com.github.paohaijiao.model.User;
import com.github.paohaijiao.xml.param.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserMapper {

    /**
     * 新增单个用户
     * @param user 用户实体
     */
    void insertUser(@Param("user") User user);

    /**
     * 批量新增用户
     * @param list 用户列表
     */
    void batchInsertUser(@Param("list") List<User> list);

    /**
     * 根据ID删除用户
     * @param id 用户ID
     */
    void deleteUserById(@Param("id") Long id);

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     */
    void batchDeleteUser(@Param("ids") List<Long> ids);

    /**
     * 更新用户信息
     * @param user 用户实体（含ID）
     */
    void updateUser(@Param("user") User user);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户实体
     */
    User getUserById(@Param("id")Long id);

    Map<String,Object> getUserDetailById(@Param("id")Long id);
    /**
     * 条件查询用户列表
     * @param user 查询条件
     * @return 用户列表
     */
    List<User> findUserList(@Param("user")User user);

    List<HashMap<String,Object>> findUserMapList(@Param("user")User user);

    /**
     * 批量查询用户
     * @param ids 用户ID列表
     * @return 用户列表
     */
    List<User> getUserByIds(@Param("ids") List<Long> ids);

    /**
     *
     * @param users
     * @return
     */
    List<User> getUserByUserNames(@Param("users") List<User> users);

    List<User> getUserByUserNamesAndValue(@Param("users") List<User> users);

    JPage<User> page(@JPageParam("pageNum") Integer pageNum, @JPageParam("pageSize") Integer pageSize, @Param("users") List<User> users);
}