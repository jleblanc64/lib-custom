package io.github.jleblanc64.libcustom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomAddTests {
    @Test
    void test() {
        assertEquals(5, A.add(2, 3));

        LibCustom.override(A.class, "add", args -> {
            var a = (int) args[0];
            var b = (int) args[1];
            return a * b;
        });
        LibCustom.load();

        assertEquals(6, A.add(2, 3));
    }

    static class A {
        static int add(int a, int b) {
            return a + b;
        }
    }
}
