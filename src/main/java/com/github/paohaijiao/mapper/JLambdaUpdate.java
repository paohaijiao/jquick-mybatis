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

import java.util.Collection;

public interface JLambdaUpdate<T> {
    JLambdaUpdate<T> set(JSFunction<T, ?> column, Object value);

    JLambdaUpdate<T> eq(JSFunction<T, ?> column, Object value);


    public JLambdaUpdate<T> ne(JSFunction<T, ?> column, Object value);

    public JLambdaUpdate<T> gt(JSFunction<T, ?> column, Object value);

    public JLambdaUpdate<T> ge(JSFunction<T, ?> column, Object value);

    public JLambdaUpdate<T> lt(JSFunction<T, ?> column, Object value);

    public JLambdaUpdate<T> le(JSFunction<T, ?> column, Object value);

    public JLambdaUpdate<T> like(JSFunction<T, ?> column, String value);

    public JLambdaUpdate<T> in(JSFunction<T, ?> column, Collection<?> values);

    int execute();
}
