package io.github.jleblanc64.libcustom.meta;

import java.util.List;

public interface MetaList<T> extends WithClass<T> {
    T fromJava(List l);

    List toJava(T t);

    BagProvider<? extends T> bag();
}
