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
package com.github.paohaijiao.mapper;

import com.github.paohaijiao.function.JSFunction;
import com.github.paohaijiao.model.JPage;

import java.util.List;

public interface JLambdaQuery<T> {
    /***
     * eq
     * @param column column
     * @param value value
     * @return
     */
    JLambdaQuery<T> eq(JSFunction<T, ?> column, Object value);

    /**
     * ne
     *
     * @param column
     * @param value
     * @return
     */
    JLambdaQuery<T> ne(JSFunction<T, ?> column, Object value);

    /**
     * gt
     *
     * @param column
     * @param value
     * @return
     */
    JLambdaQuery<T> gt(JSFunction<T, ?> column, Object value);

    /**
     * ge
     *
     * @param column
     * @param value
     * @return
     */
    JLambdaQuery<T> ge(JSFunction<T, ?> column, Object value);

    /**
     * lt
     *
     * @param column
     * @param value
     * @return
     */
    JLambdaQuery<T> lt(JSFunction<T, ?> column, Object value);

    /**
     * le
     *
     * @param column
     * @param value
     * @return
     */
    JLambdaQuery<T> le(JSFunction<T, ?> column, Object value);

    /**
     * like
     *
     * @param column
     * @param value
     * @return
     */
    JLambdaQuery<T> like(JSFunction<T, ?> column, String value);


    /**
     * orderByAsc
     *
     * @param column
     * @return
     */
    JLambdaQuery<T> orderByAsc(JSFunction<T, ?> column);

    /**
     * orderByDesc
     *
     * @param column
     * @return
     */
    JLambdaQuery<T> orderByDesc(JSFunction<T, ?> column);

    /**
     * @return
     */
    List<T> list();

    /**
     * page
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    JPage<T> page(int pageNum, int pageSize);

    /**
     * one
     *
     * @return
     */
    T one();

    /**
     * count
     *
     * @return
     */
    long count();
}
