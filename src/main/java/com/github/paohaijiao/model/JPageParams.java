package com.github.paohaijiao.model;

import lombok.Data;

@Data
public class JPageParams {

    final int pageNum;

    final int pageSize;

    public JPageParams(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
