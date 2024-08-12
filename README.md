# lib-custom

Lib Custom is a Java library for easily customizing third-party library code.
It simplifies Byte Buddy usage for common use cases.

### Maven pom.xml
```xml
<dependency>
    <groupId>io.github.jleblanc64</groupId>
    <artifactId>lib-custom</artifactId>
    <version>1.0.0</version>
</dependency>
```

Use any of the below customizers, and then load the byte code using:
```java
LibCustom.load();
```

### Override static function

class: A, function name: f
```java
LibCustom.override(A.class, "f", args -> {
    return ...;
});
```
### Override dynamic function

class: A, function name: f
```java
LibCustom.overrideWithSelf(A.class, "f", x -> {
    var args = x.args;
    var self = x.self;
    return ...;
});
```
### Modify function return

class: A, function name: f
```java
LibCustom.modifyReturn(A.class, "f", x -> {
    var args = x.args;
    
    var returned = x.returned;
    (... modify returned ...)
    return returned;
});
```
### Modify static function argument

class: A, function name: f, argument to modify index: i
```java
LibCustom.modifyArg(A.class, "f", i, args -> {
    var arg = args[i];
    (... modify arg ...)
    return arg;
});
```
### Modify dynamic function argument

class: A, function name: f, argument to modify index: i
```java
LibCustom.modifyArgWithSelf(A.class, "f", i, x -> {
    var args = x.args;
    var self = x.self;
    
    var arg = args[i];
    (... modify arg ...)
    return arg;
});
```