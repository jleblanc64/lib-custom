package io.github.jleblanc64.libcustom;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.github.jleblanc64.libcustom.functional.Functor.print;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LibCustomTests {
    @Test
    void test() {
        assertEquals(0, A.f());
        assertEquals(8, A.g());
        assertEquals(3, B.g(3));
        assertEquals(4, C.f());
        LibCustom.override(A.class, "f", args -> 1);
        LibCustom.modifyArg(B.class, "g", 0, args -> {
            // log args
            print(Arrays.toString(args));

            var i = (int) args[0];
            return i + 1;
        });
        LibCustom.override(C.class, "f", args -> 5);
        LibCustom.load();

        assertEquals(1, A.f());
        assertEquals(8, A.g());
        assertEquals(4, B.g(3));
        assertEquals(5, C.f());

        //
        LibCustom.reset();
        assertEquals(0, A.f());
        assertEquals(3, B.g(3));

        //
        LibCustom.reset();
        LibCustom.override(A.class, "f", args -> -1);
        LibCustom.override(A.class, "g", args -> -2);
        LibCustom.modifyArg(B.class, "g", 0, args -> {
            var i = (int) args[0];
            return i + 2;
        });
        LibCustom.load();

        assertEquals(-1, A.f());
        assertEquals(-2, A.g());
        assertEquals(6, B.g(4));

        //
        LibCustom.reset();
        assertEquals(8, A.g());
        LibCustom.modifyReturn(A.class, "g", x -> {
            var returned = (int) x.returned;
            return 2 * returned;
        });
        LibCustom.load();

        assertEquals(16, A.g());
    }

    @Test
    void testSelf() {
        var a = new A(11);
        assertEquals(11, a.get());
        assertEquals(13, a.getX(2));

        LibCustom.overrideWithSelf(A.class, "get", x -> {
            A self = (A) x.self;
            return self.a + 1;
        });
        LibCustom.modifyArgWithSelf(A.class, "getX", 0, x -> {
            Object[] args = x.args;
            A self = (A) x.self;

            var arg = (int) args[0];
            return self.a - arg;
        });
        LibCustom.load();

        assertEquals(12, a.get());
        assertEquals(20, a.getX(2));
    }

    @Test
    void testSelf2() {
        var a = new A(11);
        assertEquals(13, a.getX(2));

        LibCustom.overrideWithSelf(A.class, "getX", x -> {
            Object[] args = x.args;
            A self = (A) x.self;

            var arg = (int) args[0];
            return self.a + 2 * arg;
        });
        LibCustom.load();

        assertEquals(15, a.getX(2));
    }

    @Test
    void testReturn() {
        assertEquals(11, A.gX(3));
        assertEquals(4, D.f());
        LibCustom.modifyReturn(A.class, "gX", x -> {
            Object[] args = x.args;

            var arg = (int) args[0];
            var returned = (int) x.returned;
            return arg * returned;
        });
        LibCustom.modifyReturn(D.class, "f", x -> null);
        LibCustom.load();

        assertEquals(33, A.gX(3));
        assertNull(D.f());
    }

    @Test
    void testArgsModNull() {
        assertEquals(1, E.f(1));
        var e = new E();
        assertEquals(1, e.g(1));
        LibCustom.modifyArg(E.class, "f", 0, args -> null);
        LibCustom.modifyArgWithSelf(E.class, "g", 0, args -> null);
        LibCustom.load();

        assertNull(E.f(1));
        assertNull(e.g(1));
    }

    @Test
    void testMultiLoad() {
        assertEquals(4, C.f());
        assertEquals(4, D.f());
        LibCustom.modifyReturn(C.class, "f", x -> 1);
        LibCustom.load();
        LibCustom.modifyReturn(D.class, "f", x -> 1);
        LibCustom.load();

        assertEquals(1, C.f());
        assertEquals(1, D.f());
    }

    @AllArgsConstructor
    static class A {
        private int a;

        int get() {
            return a;
        }

        int getX(int x) {
            return a + x;
        }

        static int f() {
            return 0;
        }

        static int g() {
            return 8;
        }

        static int gX(int x) {
            return 8 + x;
        }
    }

    static class B {
        static int g(int i) {
            return i;
        }
    }

    static class C {
        static int f() {
            return 4;
        }
    }

    static class D {
        static Integer f() {
            return 4;
        }
    }

    static class E {
        static Integer f(Integer i) {
            return i;
        }

        Integer g(Integer i) {
            return i;
        }
    }

    @BeforeEach
    void reset() {
        LibCustom.reset();
    }
}
