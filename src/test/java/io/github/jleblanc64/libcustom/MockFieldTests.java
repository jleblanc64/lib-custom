package io.github.jleblanc64.libcustom;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class MockFieldTests {
    @SneakyThrows
    @Test
    public void test() {
        var field = A.class.getDeclaredField("i");

        // mock field, and override getType() method
        var fieldMocked = mock(Field.class, invocation -> {
            var args = invocation.getRawArguments();
            var m = invocation.getMethod();
            m.setAccessible(true);

            var name = m.getName();
            // override getType() method
            if (name.equals("getType"))
                return String.class;

            // forward all other method calls
            return m.invoke(field, args);
        });

        assertEquals(Integer.class, field.getType());
        assertEquals("i", field.getName());

        assertEquals(String.class, fieldMocked.getType());
        assertEquals("i", fieldMocked.getName());
    }

    static class A {
        Integer i;
    }
}

