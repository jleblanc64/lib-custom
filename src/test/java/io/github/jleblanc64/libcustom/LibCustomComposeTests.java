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

    @Test
    void testReturn() {
        var b = B.f();
        assertEquals(0, b.a);
        assertEquals(0, b.b);

        LibCustom.modifyReturn(B.class, "f", x -> {
            var returned = (B) x.returned;
            returned.a = 1;

            return returned;
        });

        LibCustom.modifyReturn(B.class, "f", x -> {
            var returned = (B) x.returned;
            returned.b = 1;

            return returned;
        });
        LibCustom.load();

        b = B.f();
        assertEquals(1, b.a);
        assertEquals(1, b.b);

        LibCustom.reset();

        //
        b = B.f();
        assertEquals(0, b.a);
        assertEquals(0, b.b);

        LibCustom.modifyReturn(B.class, "f", x -> LibCustom.ORIGINAL);

        LibCustom.modifyReturn(B.class, "f", x -> {
            var returned = (B) x.returned;
            returned.b = 1;

            return returned;
        });
        LibCustom.load();

        b = B.f();
        assertEquals(0, b.a);
        assertEquals(1, b.b);
    }

    @Test
    void testModifyArg() {
        LibCustom.modifyArg(C.class, "f", 0, args -> {
            var s = args[0];
            if ("a".equals(s))
                return "a2";

            return LibCustom.ORIGINAL;
        });
        LibCustom.load();

        assertEquals("a2", C.f("a"));
        assertEquals("b", C.f("b"));
        assertEquals("c", C.f("c"));

        LibCustom.modifyArg(C.class, "f", 0, args -> {
            var s = args[0];
            if ("b".equals(s))
                return "b2";

            return LibCustom.ORIGINAL;
        });
        LibCustom.load();

        assertEquals("a2", C.f("a"));
        assertEquals("b2", C.f("b"));
        assertEquals("c", C.f("c"));
    }

    @Test
    void testModifyWithSelf() {
        LibCustom.overrideWithSelf(D.class, "f", x -> {
            var s = x.args[0];
            if ("a".equals(s))
                return "a2";

            return LibCustom.ORIGINAL;
        });
        LibCustom.load();

        var d = new D();
        assertEquals("a2", d.f("a"));
        assertEquals("b", d.f("b"));
        assertEquals("c", d.f("c"));

        LibCustom.overrideWithSelf(D.class, "f", x -> {
            var s = x.args[0];
            if ("b".equals(s))
                return "b2";

            return LibCustom.ORIGINAL;
        });
        LibCustom.load();

        assertEquals("a2", d.f("a"));
        assertEquals("b2", d.f("b"));
        assertEquals("c", d.f("c"));
    }

    static class A {
        static String f(String s) {
            return s;
        }
    }

    static class B {
        public int a;
        public int b;

        static B f() {
            return new B();
        }
    }

    static class C {
        static String f(String s) {
            return s;
        }
    }

    static class D {
        String f(String s) {
            return s;
        }
    }
}
