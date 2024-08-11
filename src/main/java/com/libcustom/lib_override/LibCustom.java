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
package com.libcustom.lib_override;

import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.util.ArrayList;

import static com.libcustom.functional.Functor.ThrowingFunction;
import static com.libcustom.functional.ListF.f;
import static com.libcustom.lib_override.Internal.*;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;

public class LibCustom {
    public static void override(Class<?> clazz, String name, ThrowingFunction<Object[], Object> method) {
        Internal.methods.add(new Internal.MethodDesc(name, method, clazz));
    }

    public static void overrideWithSelf(Class<?> clazz, String name, ThrowingFunction<ArgsSelf, Object> method) {
        checkNotStatic(clazz, name);
        methodsSelf.add(new MethodDescSelf(name, method, clazz));
    }

    public static void modifyReturn(Class<?> clazz, String name, ThrowingFunction<ArgsReturned, Object> method) {
        methodsExitArgs.add(new MethodDescExitArgs(name, method, clazz));
    }

    public static void modifyArg(Class<?> clazz, String name, int argIdx, ThrowingFunction<Object[], Object> method) {
        methodsArgsMod.add(new MethodDescArgsMod(name, new MethodArgIdx(argIdx, method), clazz));
    }

    public static void modifyArgWithSelf(Class<?> clazz, String name, int argIdx, ThrowingFunction<ArgsSelf, Object> method) {
        checkNotStatic(clazz, name);
        methodsArgsModSelf.add(new MethodDescArgsModSelf(name, new MethodArgIdxSelf(argIdx, method), clazz));
    }

    @SneakyThrows
    public static void load() {
        // fill nameToMethod
        nameToMethod = methods.toMap(Internal::hash, m -> m.method);
        nameToMethodExit = methodsExit.toMap(Internal::hash, m -> m.method);
        nameToMethodExitArgs = methodsExitArgs.toMap(Internal::hash, m -> m.method);
        nameToMethodArgsMod = methodsArgsMod.toMap(Internal::hash, m -> m.method);

        nameToMethodSelf = methodsSelf.toMap(Internal::hash, m -> m.method);
        nameToMethodArgsModSelf = methodsArgsModSelf.toMap(Internal::hash, m -> m.method);

        if (instru == null)
            instru = ByteBuddyAgent.install();

        var methodMetas = new ArrayList<MethodMeta>(methods);
        methodMetas.addAll(methodsExit);
        methodMetas.addAll(methodsExitArgs);
        methodMetas.addAll(methodsArgsMod);
        var classToMethods = f(methodMetas).groupBy(MethodMeta::getClazz, MethodMeta::getName);
        classToMethods.forEach((c, m) -> agent(c, m, AdviceGeneric.class));

        // self
        var methodMetasSelf = new ArrayList<MethodMeta>(methodsSelf);
        methodMetasSelf.addAll(methodsArgsModSelf);
        var classToMethodsSelf = f(methodMetasSelf).groupBy(MethodMeta::getClazz, MethodMeta::getName);
        classToMethodsSelf.forEach((c, m) -> agent(c, m, AdviceGenericSelf.class));
    }

    public static void reset() {
        agents.forEach(a -> a.reset(instru, RETRANSFORMATION));
        agents.clear();

        methods.clear();
        methodsExit.clear();
        methodsExitArgs.clear();
        methodsArgsMod.clear();

        methodsSelf.clear();
        methodsArgsModSelf.clear();
    }
}
