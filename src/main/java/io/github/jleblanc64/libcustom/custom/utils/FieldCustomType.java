package io.github.jleblanc64.libcustom.custom.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.mock;

public class FieldCustomType {

    public static Field create(Field field, Type type) {
        return mock(Field.class, invocation -> {
            var args = invocation.getRawArguments();
            var m = invocation.getMethod();
            m.setAccessible(true);
            var name = m.getName();

            var result = m.invoke(field, args);
            if (name.equals("getGenericType"))
                return type;
            if (name.equals("getType"))
                return List.class;


            return result;
        });
    }
}
