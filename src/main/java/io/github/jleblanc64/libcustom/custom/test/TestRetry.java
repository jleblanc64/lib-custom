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
package io.github.jleblanc64.libcustom.custom.test;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.Reflection;
import io.github.jleblanc64.libcustom.functional.Functor.ThrowingSupplier;
import io.github.jleblanc64.libcustom.functional.ListF;
import lombok.SneakyThrows;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

// mvn clean test -Dtest=TestRetryTests#test
public class TestRetry {
    protected static volatile int MAX_ATTEMPTS = 3;
    protected static volatile ThrowingSupplier<Boolean> DISABLE = () -> false;

    @SneakyThrows
    public static void retryTests() {
        if (DISABLE.get())
            return;

        var methodSelectorClass = Class.forName("org.junit.platform.engine.discovery.MethodSelector");
        var testClass = Class.forName("org.junit.jupiter.api.Test");
        var reflectionUtilsClass = Class.forName("org.junit.platform.commons.util.ReflectionUtils");
        var retryingTestClass = (Class<? extends Annotation>) Class.forName("org.junitpioneer.jupiter.RetryingTest");

        LibCustom.modifyReturn(methodSelectorClass, "getJavaMethod", ret -> {
            var m = (Method) ret.returned;

            // ignore non-test function
            if (!isAnnotated(m, testClass))
                return LibCustom.ORIGINAL;

            return mockMethodAnnotations(m, retryingTestClass, testClass);
        });

        // bypass mockito for method.invoke(), use the base non mocked method (to preserve stacktrace)
        LibCustom.modifyArg(reflectionUtilsClass, "invokeMethod", 0, args -> {
            var method = (Method) args[0];
            var name = method.getName();
            var clazz = method.getDeclaringClass();
            var methods = ListF.of(clazz.getDeclaredMethods()).merge(ListF.of(clazz.getMethods()));

            return methods.findSafe(m -> m.getName().equals(name));
        });

        LibCustom.load();
    }

    private static boolean isAnnotated(Method m, Class<?> clazz) {
        var l = m.getAnnotations();
        return l.length == 1 && l[0].annotationType() == clazz;
    }

    @SneakyThrows
    private static Method mockMethodAnnotations(Method m, Class<? extends Annotation> retryingTestClass, Class<?> testClass) {

        return Mockito.mock(Method.class, invocation -> {
            var args = invocation.getRawArguments();
            var name = invocation.getMethod().getName();
            var retryingTestAnnotation = Reflection.mockAnnotation(retryingTestClass, Map.of("maxAttempts", MAX_ATTEMPTS));

            if (List.of("getDeclaredAnnotations", "getAnnotations").contains(name))
                return new Annotation[]{retryingTestAnnotation};

            if ("getDeclaredAnnotation".equals(name)) {
                var clazz = invocation.getArgument(0);
                if (clazz == retryingTestClass)
                    return retryingTestAnnotation;

                if (clazz == testClass)
                    return null;
            }

            return invocation.getMethod().invoke(m, args);
        });
    }
}
