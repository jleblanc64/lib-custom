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

import io.github.jleblanc64.libcustom.functional.Functor;
import io.github.jleblanc64.libcustom.functional.ListF;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.github.jleblanc64.libcustom.functional.ListF.empty;
import static io.github.jleblanc64.libcustom.functional.ListF.f;
import static net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.NoOp.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REDEFINE;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class Internal {
    public static Map<String, Function<Object[], Object>> nameToMethod;
    public static Map<String, Function<ArgsSelf, Object>> nameToMethodSelf;
    public static Map<String, Function<ArgsReturned, Object>> nameToMethodExitArgs;
    public static Map<String, MethodArgIdx> nameToMethodArgsMod;
    public static Map<String, MethodArgIdxSelf> nameToMethodArgsModSelf;
    public static ListF<MethodDesc> methods = empty();
    public static ListF<MethodDescSelf> methodsSelf = empty();
    public static ListF<MethodDescExitArgs> methodsExitArgs = empty();
    public static ListF<MethodDescArgsMod> methodsArgsMod = empty();
    public static ListF<MethodDescArgsModSelf> methodsArgsModSelf = empty();

    static volatile Instrumentation instru;
    static List<ResettableClassFileTransformer> agents = new ArrayList<>();

    static void agent(Class<?> clazz, ListF<String> methods, Class<?> adviceClass) {
        ElementMatcher.Junction<NamedElement> named = methods.fold(none(), (acc, m) -> acc.or(named(m)));
        var agent = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(RETRANSFORMATION)
                .with(INSTANCE)
                .with(REDEFINE)
                .type(is(clazz))
                .transform((b, type, classLoader, module, x) -> b.visit(Advice.to(adviceClass).on(named)))
                .installOn(instru);

        agents.add(agent);
    }

    static void checkFunctionName(Class<?> clazz, String name) {
        var methods = f(Reflection.getAllMethods(clazz));
        var m = methods.findSafe(x -> x.getName().equals(name));
        if (m == null)
            throw new RuntimeException("No method: " + name + " in class: " + clazz.getName());
    }

    static void checkNotStatic(Class<?> clazz, String name) {
        var methods = f(Reflection.getAllMethods(clazz));
        var m = methods.findSafe(x -> x.getName().equals(name));
        var isStatic = Modifier.isStatic(m.getModifiers());
        if (isStatic)
            throw new RuntimeException("WithSelf doesn't work for static methods");
    }

    public static String hash(Method m) {
        var mm = new MethodMeta() {

            @Override
            public String getName() {
                return m.getName();
            }

            @Override
            public Class<?> getClazz() {
                return m.getDeclaringClass();
            }
        };
        return hash(mm);
    }

    static String hash(MethodMeta m) {
        return m.getClazz().getName() + ":" + m.getName();
    }

    interface MethodMeta {
        String getName();

        Class<?> getClazz();
    }

    @AllArgsConstructor
    @Getter
    static class MethodDesc implements MethodMeta {
        String name;
        Function<Object[], Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescSelf implements MethodMeta {
        String name;
        Function<ArgsSelf, Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescExitArgs implements MethodMeta {
        String name;
        Functor.ThrowingFunction<ArgsReturned, Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescArgsMod implements MethodMeta {
        String name;
        MethodArgIdx method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescArgsModSelf implements MethodMeta {
        String name;
        MethodArgIdxSelf method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    public static class ArgsReturned {
        public Object[] args;
        public Object returned;
    }

    @AllArgsConstructor
    @Getter
    public static class ArgsSelf {
        public Object[] args;
        public Object self;
    }

    @AllArgsConstructor
    @Getter
    public static class MethodArgIdx {
        public int argIdx;
        public Function<Object[], Object> method;
    }

    @AllArgsConstructor
    @Getter
    public static class MethodArgIdxSelf {
        public int argIdx;
        public Function<ArgsSelf, Object> method;
    }
}
