/*
 * Copyright 2024 - Charles Dabadie
 *
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
 */
package io.github.jleblanc64.libcustom;

import io.github.jleblanc64.libcustom.functional.ListF;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

import static io.github.jleblanc64.libcustom.functional.ListF.f;

public class FieldMocked {
    @SneakyThrows
    public static <T> T getRefl(Object o, Field f) {
        f.setAccessible(true);
        return (T) f.get(o);
    }

    @SneakyThrows
    public static void setRefl(Object o, Field f, Object value) {
        f.setAccessible(true);
        f.set(o, value);
    }

    public static ListF<Field> fields(Object o) {
        return f(Reflection.getAllFields(o.getClass()));
    }
}
