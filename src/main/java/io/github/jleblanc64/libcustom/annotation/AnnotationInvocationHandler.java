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
package io.github.jleblanc64.libcustom.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationInvocationHandler implements Annotation, InvocationHandler, Serializable {

    private final Class<? extends Annotation> annotationType;
    private final Map<String, Object> values;

    public AnnotationInvocationHandler(Class<? extends Annotation> annotationType, Map<String, Object> values) {
        this.annotationType = annotationType;
        this.values = fillDefaults(annotationType, values);
    }

    private static Map<String, Object> fillDefaults(Class<? extends Annotation> annotationType, Map<String, Object> values) {
        values = new HashMap<>(values);
        for (var m : annotationType.getDeclaredMethods()) {
            var name = m.getName();
            if (!values.containsKey(name))
                values.put(name, m.getDefaultValue());
        }

        return values;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (values.containsKey(method.getName()))
            return values.get(method.getName());

        return method.invoke(this, args);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }
}
