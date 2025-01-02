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
package io.github.jleblanc64.libcustom.meta;

import io.vavr.collection.List;

public class MetaListVavr implements MetaList<List<?>> {
    @Override
    public Class<?> monadClass() {
        return List.class;
    }

    @Override
    public List<?> fromJava(java.util.List l) {
        return List.ofAll(l);
    }

    @Override
    public java.util.List toJava(List<?> l) {
        return l.toJavaList();
    }
}
