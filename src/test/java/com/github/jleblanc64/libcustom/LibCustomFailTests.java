package com.github.jleblanc64.libcustom;

import org.junit.jupiter.api.Test;

import static com.github.jleblanc64.libcustom.functional.Functor.catchEx;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomFailTests {
    @Test
    void test() {
        var ex = catchEx(() -> LibCustom.overrideWithSelf(LibCustomTests.A.class, "f", x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);

        ex = catchEx(() -> LibCustom.modifyArgWithSelf(LibCustomTests.A.class, "f", 0, x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);
    }
}
