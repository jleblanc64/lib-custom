package io.github.jleblanc64.libcustom;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Reflection {
    // getAllMethods()
    public static Set<Method> getAllMethods(final Class<?> type) {
        Set<Method> result = Sets.newHashSet();
        for (Class<?> t : getAllSuperTypes(type)) {
            result.addAll(getMethods(t));
        }
        return result;
    }

    private static Set<Class<?>> getAllSuperTypes(final Class<?> type) {
        Set<Class<?>> result = Sets.newLinkedHashSet();
        if (type != null && !type.equals(Object.class)) {
            result.add(type);
            for (Class<?> supertype : getSuperTypes(type)) {
                result.addAll(getAllSuperTypes(supertype));
            }
        }
        return result;
    }

    private static Set<Class<?>> getSuperTypes(Class<?> type) {
        Set<Class<?>> result = new LinkedHashSet<>();
        Class<?> superclass = type.getSuperclass();
        Class<?>[] interfaces = type.getInterfaces();
        if (superclass != null && !superclass.equals(Object.class)) result.add(superclass);
        if (interfaces != null && interfaces.length > 0) result.addAll(Arrays.asList(interfaces));
        return result;
    }

    private static Set<Method> getMethods(Class<?> t, Predicate<? super Method>... predicates) {
        return filter(t.isInterface() ? t.getMethods() : t.getDeclaredMethods(), predicates);
    }

    private static <T> Set<T> filter(final T[] elements, Predicate<? super T>... predicates) {
        return isEmpty(predicates) ? Sets.newHashSet(elements) :
                Sets.newHashSet(Iterables.filter(Arrays.asList(elements), Predicates.and(predicates)));
    }

    private static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    // getAllFields()
    public static Set<Field> getAllFields(final Class<?> type) {
        Set<Field> result = Sets.newHashSet();
        for (Class<?> t : getAllSuperTypes(type)) result.addAll(getFields(t));
        return result;
    }

    private static Set<Field> getFields(Class<?> type, Predicate<? super Field>... predicates) {
        return filter(type.getDeclaredFields(), predicates);
    }
}
