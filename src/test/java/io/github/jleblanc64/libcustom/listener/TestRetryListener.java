package io.github.jleblanc64.libcustom.listener;

import io.github.jleblanc64.libcustom.custom.test.TestRetry;
import org.junit.platform.launcher.TestExecutionListener;

// setup in src/test/resources/META-INF/services/org.junit.platform.launcher.TestExecutionListener
public class TestRetryListener extends TestRetry implements TestExecutionListener {
    public TestRetryListener() {
        MAX_ATTEMPTS = 5;
        DISABLE = TestRetryListener::isIntelliJ;
        retryTests();
    }

    private static boolean isIntelliJ() {
        var javaCmd = System.getProperty("sun.java.command", "").toLowerCase();
        return javaCmd.contains("intellij");
    }
}