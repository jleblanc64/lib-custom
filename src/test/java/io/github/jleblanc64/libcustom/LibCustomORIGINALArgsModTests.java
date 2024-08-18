package io.github.jleblanc64.libcustom;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomORIGINALArgsModTests {
    @Test
    void test() {
        var A = new A(5);
        assertEquals(10, A.f(5));
        assertEquals(11, A.f(6));
        assertEquals(12, A.f(7));
        LibCustom.modifyArgWithSelf(A.class, "f", 0, argsSelf -> {
            var i = (int) argsSelf.args[0];
            var a = ((A) argsSelf.self).a;

            if (i == 6)
                return LibCustom.ORIGINAL;
            else if (i == 7)
                return null;

            return a + i;
        });
        LibCustom.load();
        assertEquals(15, A.f(5));
        assertEquals(11, A.f(6));
        assertEquals(-1, A.f(7));

        assertEquals(5, B.f(5));
        assertEquals(6, B.f(6));
        assertEquals(7, B.f(7));
        LibCustom.modifyArg(B.class, "f", 0, args -> {
            var i = (int) args[0];

            if (i == 6)
                return LibCustom.ORIGINAL;
            else if (i == 7)
                return null;

            return 2 * i;
        });
        LibCustom.load();
        assertEquals(10, B.f(5));
        assertEquals(6, B.f(6));
        assertEquals(-1, B.f(7));
    }

    @AllArgsConstructor
    static class A {
        private int a;

        int f(Integer i) {
            if (i == null)
                return -1;

            return a + i;
        }
    }

    static class B {
        static int f(Integer i) {
            if (i == null)
                return -1;

            return i;
        }
    }
}
