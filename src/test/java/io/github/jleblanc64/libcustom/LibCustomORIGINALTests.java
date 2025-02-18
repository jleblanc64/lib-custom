package io.github.jleblanc64.libcustom;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomORIGINALTests {
    @Test
    void test() {
        LibCustom.reset();
        var A = new A(5);
        assertEquals(11, A.f(6));
        assertEquals(12, A.f(7));
        LibCustom.overrideWithSelf(A.class, "f", argsSelf -> {
            var i = (int) argsSelf.args[0];
            var a = ((A) argsSelf.self).a;

            if (i == 6)
                return LibCustom.ORIGINAL;

            return a + 2 * i;
        });
        LibCustom.load();
        assertEquals(11, A.f(6));
        assertEquals(19, A.f(7));

        assertEquals(10, B.f(10));
        assertEquals(7, B.f(7));
        LibCustom.override(B.class, "f", args -> {
            var i = (int) args[0];

            if (i == 10)
                return LibCustom.ORIGINAL;

            return 2 * i;
        });
        LibCustom.load();
        assertEquals(10, B.f(10));
        assertEquals(14, B.f(7));
    }

    @Test
    void testReturned() {
        LibCustom.reset();
        assertEquals(1, B.f(1));
        LibCustom.modifyReturn(B.class, "f", x -> {
            var returned = (int) x.returned;
            return returned + 1;
        });
        LibCustom.load();
        assertEquals(2, B.f(1));

        LibCustom.reset();
        LibCustom.modifyReturn(B.class, "f", x -> LibCustom.ORIGINAL);
        LibCustom.load();
        assertEquals(1, B.f(1));

        LibCustom.reset();
        LibCustom.modifyReturn(B.class, "f", x -> x.returned);
        LibCustom.load();
        assertEquals(1, B.f(1));
    }

    @AllArgsConstructor
    static class A {
        private int a;

        int f(int i) {
            return a + i;
        }
    }

    static class B {
        static int f(int i) {
            return i;
        }
    }
}
