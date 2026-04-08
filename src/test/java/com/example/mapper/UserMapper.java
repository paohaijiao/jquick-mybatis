package com.example.mapper;

import com.github.paohaijiao.model.User;
import com.github.paohaijiao.xml.param.Param;

import java.util.List;

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
    void deleteUserById(Long id);

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     */
    void batchDeleteUser(@Param("ids") List<Long> ids);

    /**
     * 更新用户信息
     * @param user 用户实体（含ID）
     */
    void updateUser(User user);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户实体
     */
    User getUserById(Long id);

    /**
     * 条件查询用户列表
     * @param user 查询条件
     * @return 用户列表
     */
    List<User> findUserList(User user);

    /**
     * 批量查询用户
     * @param ids 用户ID列表
     * @return 用户列表
     */
    List<User> getUserByIds(@Param("ids") List<Long> ids);
}