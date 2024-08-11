/*
 * Copyright 2024 - Charles Dabadie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jleblanc64.libcustom.functional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

public class OptionF<T> {
    public List<T> l;

    public OptionF(Optional<T> o) {
        l = o == null || o.isEmpty() ? List.of() : List.of(o.get());
    }

    public OptionF(List<T> l) {
        checkSize(l);
        this.l = l;
    }

    public OptionF<T> o() {
        return new OptionF<>(l);
    }

    public static void checkSize(List<?> l) {
        if (l != null && l.size() > 1)
            throw new RuntimeException("Not supported");
    }

    private Optional<T> opt() {
        return l == null || l.isEmpty() ? Optional.empty() : ofNullable(l.get(0));
    }

    public boolean isPresent(){
        return opt().isPresent();
    }

    public <U> OptionF<U> map(Function<T, U> f) {
        return new OptionF<>(opt().map(f));
    }

    public <U> OptionF<U> flatMap(Function<T, OptionF<U>> f) {
        return new OptionF<>(opt().flatMap(t -> f.apply(t).opt()));
    }

    public <U> OptionF<U> flatMapO(Function<T, Optional<U>> f) {
        return new OptionF<>(opt().flatMap(f));
    }

    public <U> U fold(Function<T, U> f, U u) {
        return opt().map(f).orElse(u);
    }

    public <U> U foldGet(Function<T, U> f, Supplier<U> u) {
        return opt().map(f).orElseGet(u);
    }

    public T orElse(T t) {
        return opt().orElse(t);
    }

    public T orElseGet(Supplier<T> t) {
        return opt().orElseGet(t);
    }

    public T get() {
        return orElse(null);
    }

    public static <T> OptionF<T> o(T t) {
        return new OptionF<>(ofNullable(t));
    }

    public static <T> OptionF<T> emptyO() {
        return o(null);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OptionF))
            return false;

        return Objects.equals(l, ((OptionF) o).l);
    }

    @Override
    public int hashCode() {
        if (l == null)
            return 0;

        return l.hashCode();
    }
}
