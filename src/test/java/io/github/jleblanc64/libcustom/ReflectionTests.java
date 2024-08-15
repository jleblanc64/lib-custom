package io.github.jleblanc64.libcustom;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static io.github.jleblanc64.libcustom.FieldMocked.fields;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectionTests {
    @Test
    public void test() {
        var fields = fields(new A());
        assertEquals("[a, b]", fields.map(Field::getName).sorted().toString());
    }

    static class A extends B {
        int a;
    }

    static class B {
        int b;
    }
}

