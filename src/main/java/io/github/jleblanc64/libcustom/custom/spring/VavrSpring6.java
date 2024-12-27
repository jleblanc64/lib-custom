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
package io.github.jleblanc64.libcustom.custom.spring;

import io.github.jleblanc64.libcustom.LibCustom;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.SneakyThrows;

import static io.github.jleblanc64.libcustom.FieldMocked.*;

public class VavrSpring6 {
    @SneakyThrows
    public static void override() {
        var methodParameterClass = Class.forName("org.springframework.core.MethodParameter");
        var httpHeadersClass = Class.forName("org.springframework.http.HttpHeaders");
        var argResolverClass = Class.forName("org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver");
        var msgConverterClass = Class.forName("org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter");
        var mediaTypeClass = Class.forName("org.springframework.http.MediaType");

        // replace null with empty OptionF or ListF
        LibCustom.modifyReturn(msgConverterClass, "readJavaType", argsR -> {
            var returned = argsR.returned;
            if (returned == null)
                return returned;

            fields(returned).forEach(f -> {
                var type = f.getType();
                Object empty;
                if (type == Option.class)
                    empty = Option.none();
                else if (type == List.class)
                    empty = List.empty();
                else
                    return;

                var o = getRefl(returned, f);
                if (o == null)
                    setRefl(returned, f, empty);
            });

            return returned;
        });

        // accept text/plain content-type as json
        LibCustom.modifyReturn(httpHeadersClass, "getContentType", argsR -> {
            var mediaType = argsR.returned;
            if (mediaType != null && mediaType.toString().toLowerCase().startsWith("text/plain"))
                return mediaTypeClass.getMethod("parseMediaType", String.class).invoke(null, "application/json");

            return mediaType;
        });

        LibCustom.modifyReturn(argResolverClass, "resolveArgument", argsRet -> {
            var args = argsRet.args;
            var returned = argsRet.returned;
            var type = methodParameterClass.getMethod("getParameterType").invoke(args[0]);

            if (type == Option.class && !(returned instanceof Option))
                return Option.of(returned);

            return returned;
        });

        LibCustom.modifyArg(Class.forName("org.springframework.beans.TypeConverterDelegate"), "doConvertValue", 1, args -> {
            var newValue = args[1];
            var requiredType = (Class<?>) args[2];

            if (requiredType == Option.class)
                return Option.of(newValue);

            return LibCustom.ORIGINAL;
        });
    }
}
