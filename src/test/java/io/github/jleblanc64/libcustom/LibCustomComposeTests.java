package io.github.jleblanc64.libcustom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomComposeTests {
    @Test
    void test() {
        assertEquals("a", A.f("a"));
        assertEquals("b", A.f("b"));
        assertEquals("c", A.f("c"));

        LibCustom.override(A.class, "f", args -> {
            var s = (String) args[0];
            if (s.equals("a"))
                return "a2";

            return LibCustom.ORIGINAL;
        });
        LibCustom.override(A.class, "f", args -> {
            var s = (String) args[0];
            if (s.equals("b"))
                return "b2";

            return LibCustom.ORIGINAL;
        });

        LibCustom.load();

        assertEquals("a2", A.f("a"));
        assertEquals("b2", A.f("b"));
        assertEquals("c", A.f("c"));
    }

    static class A {
        static String f(String s) {
            return s;
        }
    }
}
