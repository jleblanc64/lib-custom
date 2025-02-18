package io.github.jleblanc64.libcustom.custom.hibernate.duplicate;

import lombok.SneakyThrows;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

// https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/reflect/TypeUtils.java#L137
public class ParameterizedTypeImpl {
    @SneakyThrows
    public static ParameterizedType of(Class<?> rawType, Type typeArg, Type ownerType) {

        var clazz = Class.forName("org.apache.commons.lang3.reflect.TypeUtils$ParameterizedTypeImpl");
        var constructor = clazz.getDeclaredConstructor(Class.class, Type.class, Array.newInstance(Type.class, 0).getClass());
        constructor.setAccessible(true);

        return (ParameterizedType) constructor.newInstance(rawType, ownerType, new Type[]{typeArg});
    }
}