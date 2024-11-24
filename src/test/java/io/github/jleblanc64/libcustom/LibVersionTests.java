package io.github.jleblanc64.libcustom;

import org.junit.jupiter.api.Test;

import static io.github.jleblanc64.libcustom.LibVersion.byteBuddyVersionToInt;
import static io.github.jleblanc64.libcustom.LibVersion.isBigger;
import static io.github.jleblanc64.libcustom.functional.Functor.catchEx;
import static org.junit.jupiter.api.Assertions.*;

public class LibVersionTests {
    @Test
    public void test() {
        assertEquals(123456, byteBuddyVersionToInt("12.34.56"));
        assertEquals(1234, byteBuddyVersionToInt("12.34"));
        assertEquals(12, byteBuddyVersionToInt("12"));
        assertEquals("For input string: \"12a\"", catchEx(() -> byteBuddyVersionToInt("12a")));
    }

    @Test
    public void testIsBigger() {
        assertTrue(isBigger("12.34.56", "12.34"));
        assertFalse(isBigger("12.34.56", "12.34.56"));
        assertTrue(isBigger("12.35.1", "12.34.56"));
    }
}

