package io.github.jleblanc64.libcustom.retry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// should fail when run directly in IntelliJ
public class TestRetryTests {
    static int count = 0;

    @Test
    public void test() {
        count++;
        if (count == 3)
            return;

        assertEquals(1, 2);
    }
}

