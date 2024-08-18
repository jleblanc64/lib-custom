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

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static io.github.jleblanc64.libcustom.Internal.*;
import static io.github.jleblanc64.libcustom.functional.OptionF.o;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;

public class AdviceGeneric {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static Object enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC) Object[] args,
                               @Advice.Origin Method method) {
        var name = hash(method);
        var f = nameToMethod.get(name);
        if (f != null)
            return ValueWrapper.fromResult(f.apply(args));

        var methodArgIdx = nameToMethodArgsMod.get(name);
        if (methodArgIdx != null) {
            var argsMod = methodArgIdx.method.apply(args);
            if (!(argsMod instanceof LibCustom.C1))
                args = modArgs(args, methodArgIdx.argIdx, argsMod);
        }

        return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Enter Object enter, @Advice.AllArguments Object[] args,
                            @Advice.Return(readOnly = false, typing = DYNAMIC) Object returned,
                            @Advice.Origin Method method) {

        var returnedOverride = returnedOverride(args, returned, method);
        if (returnedOverride != null)
            returned = returnedOverride;
        else if (enter != null)
            returned = ((ValueWrapper) enter).value;
    }

    public static Object returnedOverride(Object[] args, Object returned, Method method) {
        var name = hash(method);
        var fArgs = nameToMethodExitArgs.get(name);
        if (fArgs != null)
            return fArgs.apply(new ArgsReturned(args, returned));

        var fOpt = o(nameToMethodExit.get(method.getName()));
        return fOpt.flatMap(f -> o(f.apply(returned))).get();
    }

    public static Object[] modArgs(Object[] args, int idx, Object updated) {
        var argsCloned = args.clone();
        argsCloned[idx] = updated;
        return argsCloned;
    }
}
