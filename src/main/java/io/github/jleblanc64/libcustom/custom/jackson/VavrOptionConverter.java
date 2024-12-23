package io.github.jleblanc64.libcustom.custom.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import io.vavr.control.Option;

public class VavrOptionConverter {
    public static class FromOption<T> extends StdConverter<Option<T>, T> {
        @Override
        public T convert(Option<T> value) {
            return value.getOrNull();
        }
    }

    public static class ToOption<T> extends StdConverter<T, Option<T>> {
        @Override
        public Option<T> convert(T value) {
            return Option.of(value);
        }
    }
}
