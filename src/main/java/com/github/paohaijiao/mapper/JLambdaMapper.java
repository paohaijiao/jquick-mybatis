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

import com.github.paohaijiao.model.JParam;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface JLambdaMapper<T> {
    /**
     * @param entity
     * @return
     */
    int insert(T entity);

    /**
     * update
     *
     * @param entity
     * @return
     */
    int updateById(T entity);

    /**
     * deleteById
     *
     * @param id id
     * @return
     */
    int deleteById(Serializable id);

    /**
     * selectById
     *
     * @param id
     * @return
     */
    T selectById(Serializable id);

    /**
     * select
     *
     * @param sql
     * @return
     */
    List<T> select(String sql, Map<String, Object> param);

    /**
     * select
     *
     * @param sql
     * @param param
     * @return
     */
    List<T> select(String sql, List<JParam> param);

    /**
     * page
     *
     * @return
     */
    JLambdaQuery<T> page();

    /**
     * Lambda query
     *
     * @return
     */
    JLambdaQuery<T> query();

    /**
     * lamdaupte update
     *
     * @return
     */
    JLambdaUpdate<T> update();
}
