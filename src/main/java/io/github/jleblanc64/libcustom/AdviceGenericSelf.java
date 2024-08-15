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

import static io.github.jleblanc64.libcustom.AdviceGeneric.modArgs;
import static io.github.jleblanc64.libcustom.Internal.*;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;

public class AdviceGenericSelf {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static Object enter(@Advice.This Object self, @Advice.AllArguments(readOnly = false, typing = DYNAMIC) Object[] args,
                               @Advice.Origin Method method) {
        var name = hash(method);

        var f = nameToMethodSelf.get(name);
        if (f != null) {
            var res = f.apply(new ArgsSelf(args, self));
            return !(res instanceof LibCustom.Original) ? new ValueWrapper(res) : null;
        }

        var methodArgIdxSelf = nameToMethodArgsModSelf.get(name);
        if (methodArgIdxSelf != null) {
            var argsMod = methodArgIdxSelf.method.apply(new ArgsSelf(args, self));
            if (argsMod != null)
                args = modArgs(args, methodArgIdxSelf.argIdx, argsMod);
        }

        return null;
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.Enter Object enter, @Advice.Return(readOnly = false, typing = DYNAMIC) Object returned) {
        if (enter != null)
            returned = ((ValueWrapper) enter).value;
    }
}
