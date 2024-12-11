package io.github.jleblanc64.libcustom.annotation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.github.jleblanc64.libcustom.Reflection.mockAnnotation;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockAnnotationTests {
    @Test
    public void test() {
        var annotation = mockAnnotation(MyAnnotation.class, Map.of("a", 2));
        assertEquals(2, annotation.a());
        assertEquals(1, annotation.b());
    }
}
