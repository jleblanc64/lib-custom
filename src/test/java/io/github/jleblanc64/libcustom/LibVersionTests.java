package io.github.jleblanc64.libcustom;

import org.junit.jupiter.api.Test;

import static io.github.jleblanc64.libcustom.LibVersion.isBigger;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibVersionTests {
    @Test
    public void testIsBigger() {
        assertTrue(isBigger("12.34.56", "12.34"));
        assertFalse(isBigger("12.34.56", "12.34.56"));
        assertTrue(isBigger("12.35.1", "12.34.56"));
    }
}

