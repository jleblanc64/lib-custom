package io.github.jleblanc64.libcustom.custom.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import io.vavr.collection.List;

import java.util.Collection;

public class VavrListDeser {
   public static class Deserializer extends StdDelegatingDeserializer<List<?>> {
        public Deserializer() {
            super(new VavrListConverter.FromCollec());
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            var elementType = property.getType().getBindings().getBoundType(0);
            var delegateType = ctxt.getTypeFactory().constructCollectionLikeType(Collection.class, elementType);
            return withDelegate(_converter, delegateType, ctxt.findContextualValueDeserializer(delegateType, property));
        }

        @Override
        protected StdDelegatingDeserializer<List<?>> withDelegate(Converter<Object, List<?>> c, JavaType t, JsonDeserializer<?> deser) {
            return new StdDelegatingDeserializer<>(c, t, deser);
        }
    }

    public static class Serializer extends StdDelegatingSerializer {
        public Serializer() {
            super(new VavrListConverter.ToCollec());
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object, ?> c, JavaType t, JsonSerializer<?> deser) {
            return new StdDelegatingSerializer(c, t, deser);
        }
    }
}
