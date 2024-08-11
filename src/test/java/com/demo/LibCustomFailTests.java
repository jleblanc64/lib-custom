package com.demo;

import com.demo.lib_override.LibCustom;
import org.junit.jupiter.api.Test;

import static com.demo.LibCustomTests.A;
import static com.demo.functional.Functor.catchEx;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomFailTests {
    @Test
    void test() {
        var ex = catchEx(() -> LibCustom.overrideWithSelf(A.class, "f", x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);

        ex = catchEx(() -> LibCustom.modifyArgWithSelf(A.class, "f", 0, x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);
    }
}
