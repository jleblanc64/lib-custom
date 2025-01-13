package io.github.jleblanc64.libcustom.listener;

import io.github.jleblanc64.libcustom.custom.test.TestRetry;
import org.junit.platform.launcher.TestExecutionListener;

public class RunnerExtension extends TestRetry implements TestExecutionListener {
    public RunnerExtension() {
        MAX_ATTEMPTS = 5;
        DISABLE = RunnerExtension::isIntelliJ;
        retryTests();
    }

    private static boolean isIntelliJ() {
        var javaCmd = System.getProperty("sun.java.command", "").toLowerCase();
        return javaCmd.contains("intellij");
    }
}