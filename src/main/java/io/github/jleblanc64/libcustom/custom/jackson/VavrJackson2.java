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
import lombok.SneakyThrows;

public class VavrJackson2 {
    @SneakyThrows
    public static void override(java.util.List<?> converters) {
        var om = new ObjectMapper();
        var simpleModule = new SimpleModule()
                .addDeserializer(List.class, new VavrListDeser.Deserializer())
                .addSerializer(List.class, new VavrListDeser.Serializer());
        om.registerModule(simpleModule);

        simpleModule = new SimpleModule()
                .addDeserializer(Option.class, new VavrOptionDeser.Deserializer())
                .addSerializer(Option.class, new VavrOptionDeser.Serializer());
        om.registerModule(simpleModule);

        var msgConverterClass = Class.forName("org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter");

        List.ofAll(converters).filter(c -> msgConverterClass.isAssignableFrom(c.getClass()))
                .forEach(c -> setObjectMapper(c, om, msgConverterClass));
    }

    @SneakyThrows
    static void setObjectMapper(Object msgConverter, ObjectMapper om, Class<?> msgConverterClass) {
        msgConverterClass.getMethod("setObjectMapper", ObjectMapper.class).invoke(msgConverter, om);
    }
}
