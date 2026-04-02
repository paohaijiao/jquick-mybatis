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
package com.github.paohaijiao.support;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.List;

@Data
public class JMappedStatement {
    private String id;
    private String sql;
    private Class<?> resultType;
    private Type resultGenericType;
    private List<JParameterMapping> parameterMappings;

    public Type getResultGenericType() {
        return resultGenericType != null ? resultGenericType : resultType;
    }

    public void setResultGenericType(Type resultGenericType) {
        this.resultGenericType = resultGenericType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
        this.resultGenericType = resultType;
    }
}
