package io.github.jleblanc64.libcustom.retry;

import org.junit.jupiter.api.Test;

import static io.github.jleblanc64.libcustom.functional.Functor.print;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRetryTests extends TestRetry {
    @Test
    public void test() {
        assertEquals(1, 1);

//        assertEquals(1, 2);

        print("success");
    }
}

