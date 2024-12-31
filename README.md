# lib-custom

Lib Custom is a Java library for customizing any method at runtime, whether the method is part of your codebase or external.

### Maven pom.xml
```xml
<dependency>
    <groupId>io.github.jleblanc64</groupId>
    <artifactId>lib-custom</artifactId>
    <version>1.0.7</version>
</dependency>
```

### Load custom code


Use any of the below customizers, and then load the byte code using:
```java
LibCustom.load();
```

### Override static method

class: `A`, method name: `m`
```java
LibCustom.override(A.class, "m", args -> {
    return ...;
});
```
### Override dynamic method

class: `A`, method name: `m`
```java
LibCustom.overrideWithSelf(A.class, "m", x -> {
    Object[] args = x.args;
    A self = (A) x.self;
    return ...;
});
```
### Modify method return

class: `A`, method name: `m`
```java
LibCustom.modifyReturn(A.class, "m", x -> {
    Object[] args = x.args;
    
    var returned = x.returned;
    (... modify returned ...)
    return returned;
});
```
### Modify static method argument

class: `A`, method name: `m`, argument to modify index: `i`
```java
LibCustom.modifyArg(A.class, "m", i, args -> {
    var arg = args[i];
    (... modify arg ...)
    return arg;
});
```
### Modify dynamic method argument

class: `A`, method name: `m`, argument to modify index: `i`
```java
LibCustom.modifyArgWithSelf(A.class, "m", i, x -> {
    Object[] args = x.args;
    A self = (A) x.self;
    
    var arg = args[i];
    (... modify arg ...)
    return arg;
});
```

### Cancel all modifications
```java
LibCustom.reset();
```

### Full code demo: transform addition into multiplication
```java
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomAddTests {
    @Test
    void test() {
        assertEquals(5, A.add(2, 3));

        LibCustom.override(A.class, "add", args -> {
            var a = (int) args[0];
            var b = (int) args[1];
            return a * b;
        });
        LibCustom.load();

        assertEquals(6, A.add(2, 3));
    }

    static class A {
        static int add(int a, int b) {
            return a + b;
        }
    }
}
```