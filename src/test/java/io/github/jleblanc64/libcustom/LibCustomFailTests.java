package io.github.jleblanc64.libcustom;

import org.junit.jupiter.api.Test;

import static io.github.jleblanc64.libcustom.functional.Functor.catchEx;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomFailTests {
    @Test
    void testStatic() {
        var ex = catchEx(() -> LibCustom.overrideWithSelf(LibCustomTests.A.class, "f", x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);

        ex = catchEx(() -> LibCustom.modifyArgWithSelf(LibCustomTests.A.class, "f", 0, x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);
    }

    @Test
    void testFunctionName() {
        var ex = catchEx(() -> LibCustom.overrideWithSelf(LibCustomTests.A.class, "ff", x -> null));
        assertEquals("No method: ff in class: io.github.jleblanc64.libcustom.LibCustomTests$A", ex);

        ex = catchEx(() -> LibCustom.override(LibCustomTests.A.class, "ff", x -> null));
        assertEquals("No method: ff in class: io.github.jleblanc64.libcustom.LibCustomTests$A", ex);
    }

    @Test
    void testComposeModifyArgDiffIdx() {
        LibCustom.modifyArg(C.class, "f", 0, args -> "");

        var ex = catchEx(() -> LibCustom.modifyArg(C.class, "f", 1, args -> ""));
        assertEquals("Cannot compose modifyArg() on 2 different indexes", ex);
    }

    static class C {
        static String f(String a, String b) {
            return a + b;
        }
    }
}
