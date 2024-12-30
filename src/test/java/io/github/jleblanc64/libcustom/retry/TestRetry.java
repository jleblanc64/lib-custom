package io.github.jleblanc64.libcustom.retry;

import io.github.jleblanc64.libcustom.custom.test.TestRetryDefault;

public class TestRetry extends TestRetryDefault {
    static {
        MAX_ATTEMPTS = 5;
        DISABLE = TestRetry::isIntelliJ;
        retryTests();
    }

    static boolean isIntelliJ() {
        var javaCmd = System.getProperty("sun.java.command", "").toLowerCase();
        return javaCmd.contains("intellij");
    }
}
