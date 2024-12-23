package io.github.jleblanc64.libcustom.custom.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import io.vavr.collection.List;

import java.util.Collection;

public class VavrListConverter {
    public static class FromCollec extends StdConverter<Collection<?>, List<?>> {
        @Override
        public List<?> convert(Collection value) {
            return List.ofAll(value);
        }
    }

    public static class ToCollec extends StdConverter<List<?>, Collection<?>> {
        @Override
        public Collection<?> convert(List value) {
            return value.toJavaList();
        }
    }
}
