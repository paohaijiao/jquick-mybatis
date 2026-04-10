package com.github.paohaijiao.anno;

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
import java.lang.annotation.*;

/**
 * 分页参数注解
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/7/8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface JPageParam {

    /**
     * 参数类型：pageNum 或 pageSize
     */
    String value() default "";

    /**
     * 默认值
     */
    int defaultValue() default 0;
}
