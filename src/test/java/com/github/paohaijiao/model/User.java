package com.github.paohaijiao.model;

import lombok.Data;

@Data
public class User {

    private Long id;

    private String username;

    private String password;

    private Integer age;

    private String email;

    private Integer status;
}
