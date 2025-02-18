package io.github.jleblanc64.libcustom.meta;

public interface WithClass<T> {
    Class<T> monadClass();

    default boolean isSuperClassOf(Object o) {
        if (o == null)
            return false;

        var clazz = o instanceof Class ? ((Class) o) : o.getClass();
        return monadClass().isAssignableFrom(clazz);
    }
}
