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
package io.github.jleblanc64.libcustom.custom.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class VavrJackson2 {
    public static void override(java.util.List<HttpMessageConverter<?>> converters) {
        var om = new ObjectMapper();
        var simpleModule = new SimpleModule()
                .addDeserializer(List.class, new VavrListDeser.Deserializer())
                .addSerializer(List.class, new VavrListDeser.Serializer());
        om.registerModule(simpleModule);

        simpleModule = new SimpleModule()
                .addDeserializer(Option.class, new VavrOptionDeser.Deserializer())
                .addSerializer(Option.class, new VavrOptionDeser.Serializer());
        om.registerModule(simpleModule);

        List.ofAll(converters).filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .forEach(c -> ((MappingJackson2HttpMessageConverter) c).setObjectMapper(om));
    }
}
