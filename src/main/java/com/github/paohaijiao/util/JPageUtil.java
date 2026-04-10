package com.github.paohaijiao.util;

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
import com.github.paohaijiao.model.JPage;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基于 List 的分页工具类
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/7/8
 */
public class JPageUtil {

    /**
     * 对 List 进行分页
     *
     * @param list     原始数据列表
     * @param pageNum  当前页码（从1开始）
     * @param pageSize 每页大小
     * @param <T>      数据类型
     * @return 分页结果
     */
    public static <T> JPage<T> page(List<T> list, int pageNum, int pageSize) {
        if (list == null || list.isEmpty()) {
            return emptyPage(pageNum, pageSize);
        }
        pageNum = Math.max(1, pageNum);
        pageSize = Math.max(1, pageSize);
        long total = list.size();
        int pages = (int) Math.ceil((double) total / pageSize);
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, (int) total);
        List<T> records;
        if (startIndex >= total) {
            records = Collections.emptyList();
        } else {
            records = list.subList(startIndex, endIndex);
        }

        return new JPage<T>().setRecords(records).setTotal(total).setSize(pageSize).setCurrent(pageNum).setPages(pages);
    }

    /**
     * 对 List 进行分页（支持从0开始的页码）
     *
     * @param list         原始数据列表
     * @param pageIndex    当前页索引（从0开始）
     * @param pageSize     每页大小
     * @param zeroBased    是否从0开始（true: pageIndex从0开始, false: pageNum从1开始）
     * @param <T>          数据类型
     * @return 分页结果
     */
    public static <T> JPage<T> page(List<T> list, int pageIndex, int pageSize, boolean zeroBased) {
        if (zeroBased) {
            return page(list, pageIndex + 1, pageSize);
        }
        return page(list, pageIndex, pageSize);
    }

    /**
     * 对 List 进行分页并转换数据类型
     *
     * @param list      原始数据列表
     * @param pageNum   当前页码
     * @param pageSize  每页大小
     * @param converter 数据转换函数
     * @param <T>       原始数据类型
     * @param <R>       目标数据类型
     * @return 分页结果
     */
    public static <T, R> JPage<R> pageAndConvert(List<T> list, int pageNum, int pageSize, Function<T, R> converter) {
        JPage<T> page = page(list, pageNum, pageSize);
        List<R> convertedRecords = page.getRecords().stream().map(converter).collect(Collectors.toList());
        return new JPage<R>().setRecords(convertedRecords).setTotal(page.getTotal()).setSize(page.getSize()).setCurrent(page.getCurrent()).setPages(page.getPages());
    }

    /**
     * 获取空分页结果
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param <T>      数据类型
     * @return 空分页结果
     */
    public static <T> JPage<T> emptyPage(int pageNum, int pageSize) {
        return new JPage<T>().setRecords(Collections.emptyList()).setTotal(0).setSize(pageSize).setCurrent(Math.max(1, pageNum)).setPages(0);
    }

    /**
     * 计算总页数
     *
     * @param total     总记录数
     * @param pageSize  每页大小
     * @return 总页数
     */
    public static int calculatePages(long total, int pageSize) {
        if (total <= 0 || pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 获取分页起始索引（从0开始）
     *
     * @param pageNum  当前页码（从1开始）
     * @param pageSize 每页大小
     * @return 起始索引
     */
    public static int getStartIndex(int pageNum, int pageSize) {
        return Math.max(0, (pageNum - 1) * pageSize);
    }

    /**
     * 获取分页结束索引（从0开始，不包含）
     *
     * @param pageNum  当前页码（从1开始）
     * @param pageSize 每页大小
     * @param total    总记录数
     * @return 结束索引
     */
    public static int getEndIndex(int pageNum, int pageSize, long total) {
        return Math.min(getStartIndex(pageNum, pageSize) + pageSize, (int) total);
    }
}