package com.github.paohaijiao.model;

import com.github.paohaijiao.anno.JTable;
import lombok.Data;

import java.util.Date;

@Data
@JTable(value = "test.t_user")
public class User {

    private Long id;

    private String username;

    private String password;

    private Integer age;

    private String email;

    private Integer status;

    private Date createTime;
    private Date updateTime;
}
