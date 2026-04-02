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
package com.github.paohaijiao.core;

import com.github.paohaijiao.connection.JSqlConnection;
import com.github.paohaijiao.mapper.JLambdaMapper;
import com.github.paohaijiao.mapper.JLambdaQuery;
import com.github.paohaijiao.mapper.JLambdaUpdate;
import com.github.paohaijiao.model.JParam;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class JLambdaMapperImpl<T> implements JLambdaMapper<T> {
    private final Class<T> entityClass;
    private final JSqlConnection sqlSession;

    public JLambdaMapperImpl(Class<T> entityClass, JSqlConnection sqlSession) {
        this.entityClass = entityClass;
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(T entity) {
        LambdaInsertImpl lambdaInsert = new LambdaInsertImpl<>(entityClass, sqlSession);
        return lambdaInsert.insert(entity);
    }

    @Override
    public int updateById(T entity) {
        LambdaUpdateImpl lambdaUpdate = new LambdaUpdateImpl<>(entityClass, sqlSession);
        return lambdaUpdate.updateById(entity);
    }

    @Override
    public int deleteById(Serializable id) {
        LambdaDeleteImpl<T> lambdaDelete = new LambdaDeleteImpl<>(entityClass, sqlSession);
        return lambdaDelete.deleteById(id);
    }

    @Override
    public T selectById(Serializable id) {
        JLambdaQueryImpl<T> lambdaQuery = new JLambdaQueryImpl<>(entityClass, sqlSession);
        return lambdaQuery.selectById(id);
    }

    @Override
    public List<T> select(String sql, Map<String, Object> param) {
        JLambdaQueryImpl<T> lambdaQuery = new JLambdaQueryImpl<>(entityClass, sqlSession);
        return lambdaQuery.select(sql, param);
    }

    @Override
    public List<T> select(String sql, List<JParam> param) {
        JLambdaQueryImpl<T> lambdaQuery = new JLambdaQueryImpl<>(entityClass, sqlSession);
        return lambdaQuery.select(sql, param);
    }

    @Override
    public JLambdaQuery<T> page() {
        return new JLambdaQueryImpl<>(entityClass, sqlSession);
    }


    @Override
    public JLambdaQuery<T> query() {
        return new JLambdaQueryImpl<>(entityClass, sqlSession);
    }

    @Override
    public JLambdaUpdate<T> update() {
        return new LambdaUpdateImpl<>(entityClass, sqlSession);
    }

}
