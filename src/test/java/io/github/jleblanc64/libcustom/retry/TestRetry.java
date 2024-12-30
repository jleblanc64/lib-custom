package io.github.jleblanc64.libcustom.retry;

import io.github.jleblanc64.libcustom.custom.test.TestRetryDefault;

import static io.github.jleblanc64.libcustom.functional.Functor.print;

public class TestRetry extends TestRetryDefault {
    static {
        MAX_ATTEMPTS = 5;
        DISABLE = TestRetry::isIntelliJ;
        retryTests();
    }

    static boolean isIntelliJ() {
        var javaCmd = System.getProperty("sun.java.command", "").toLowerCase();

        var isIntelliJ = javaCmd.contains("intellij");
        if (isIntelliJ)
            print("Tests run from IntelliJ");

        return isIntelliJ;
    }
}
