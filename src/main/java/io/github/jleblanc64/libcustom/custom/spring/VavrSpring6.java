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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import static io.github.jleblanc64.libcustom.FieldMocked.*;

public class VavrSpring6 {
    public static void override() {
        // replace null with empty OptionF or ListF
        LibCustom.modifyReturn(AbstractJackson2HttpMessageConverter.class, "readJavaType", argsR -> {
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
        LibCustom.modifyReturn(HttpHeaders.class, "getContentType", argsR -> {
            var mediaType = argsR.returned;
            if (mediaType != null && mediaType.toString().toLowerCase().startsWith("text/plain"))
                return MediaType.parseMediaType("application/json");

            return mediaType;
        });
    }
}
