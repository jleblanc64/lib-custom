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

import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.util.ArrayList;

import static io.github.jleblanc64.libcustom.functional.Functor.ThrowingFunction;
import static io.github.jleblanc64.libcustom.functional.ListF.f;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;

public class LibCustom {
    public static void override(Class<?> clazz, String name, ThrowingFunction<Object[], Object> method) {
        Internal.methods.add(new Internal.MethodDesc(name, method, clazz));
    }

    public static void overrideWithSelf(Class<?> clazz, String name, ThrowingFunction<Internal.ArgsSelf, Object> method) {
        Internal.checkNotStatic(clazz, name);
        Internal.methodsSelf.add(new Internal.MethodDescSelf(name, method, clazz));
    }

    public static void modifyReturn(Class<?> clazz, String name, ThrowingFunction<Internal.ArgsReturned, Object> method) {
        Internal.methodsExitArgs.add(new Internal.MethodDescExitArgs(name, method, clazz));
    }

    public static void modifyArg(Class<?> clazz, String name, int argIdx, ThrowingFunction<Object[], Object> method) {
        Internal.methodsArgsMod.add(new Internal.MethodDescArgsMod(name, new Internal.MethodArgIdx(argIdx, method), clazz));
    }

    public static void modifyArgWithSelf(Class<?> clazz, String name, int argIdx, ThrowingFunction<Internal.ArgsSelf, Object> method) {
        Internal.checkNotStatic(clazz, name);
        Internal.methodsArgsModSelf.add(new Internal.MethodDescArgsModSelf(name, new Internal.MethodArgIdxSelf(argIdx, method), clazz));
    }

    @SneakyThrows
    public static void load() {
        // fill nameToMethod
        Internal.nameToMethod = Internal.methods.toMap(Internal::hash, m -> m.method);
        Internal.nameToMethodExit = Internal.methodsExit.toMap(Internal::hash, m -> m.method);
        Internal.nameToMethodExitArgs = Internal.methodsExitArgs.toMap(Internal::hash, m -> m.method);
        Internal.nameToMethodArgsMod = Internal.methodsArgsMod.toMap(Internal::hash, m -> m.method);

        Internal.nameToMethodSelf = Internal.methodsSelf.toMap(Internal::hash, m -> m.method);
        Internal.nameToMethodArgsModSelf = Internal.methodsArgsModSelf.toMap(Internal::hash, m -> m.method);

        if (Internal.instru == null)
            Internal.instru = ByteBuddyAgent.install();

        var methodMetas = new ArrayList<Internal.MethodMeta>(Internal.methods);
        methodMetas.addAll(Internal.methodsExit);
        methodMetas.addAll(Internal.methodsExitArgs);
        methodMetas.addAll(Internal.methodsArgsMod);
        var classToMethods = f(methodMetas).groupBy(Internal.MethodMeta::getClazz, Internal.MethodMeta::getName);
        classToMethods.forEach((c, m) -> Internal.agent(c, m, AdviceGeneric.class));

        // self
        var methodMetasSelf = new ArrayList<Internal.MethodMeta>(Internal.methodsSelf);
        methodMetasSelf.addAll(Internal.methodsArgsModSelf);
        var classToMethodsSelf = f(methodMetasSelf).groupBy(Internal.MethodMeta::getClazz, Internal.MethodMeta::getName);
        classToMethodsSelf.forEach((c, m) -> Internal.agent(c, m, AdviceGenericSelf.class));
    }

    public static void reset() {
        Internal.agents.forEach(a -> a.reset(Internal.instru, RETRANSFORMATION));
        Internal.agents.clear();

        Internal.methods.clear();
        Internal.methodsExit.clear();
        Internal.methodsExitArgs.clear();
        Internal.methodsArgsMod.clear();

        Internal.methodsSelf.clear();
        Internal.methodsArgsModSelf.clear();
    }

    public static final C1 ORIGINAL = new C1();

    public static final class C1 {
        private C1() {
        }
    }
}
