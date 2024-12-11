package io.github.jleblanc64.libcustom.annotation;

public @interface MyAnnotation {
    int a() default 0;

    int b() default 1;
}
