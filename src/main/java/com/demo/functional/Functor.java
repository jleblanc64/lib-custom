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
package com.demo.functional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.*;

import static com.demo.functional.OptionF.emptyO;
import static com.demo.functional.OptionF.o;

public class Functor {
    @FunctionalInterface
    public interface ThrowingFunction<T, U> extends Function<T, U> {
        @Override
        default U apply(T elem) {
            try {
                return applyThrows(elem);
            } catch (Exception e) {
                throw toRuntime(e);
            }
        }

        U applyThrows(T elem) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T> extends Consumer<T> {
        @Override
        default void accept(T elem) {
            try {
                acceptThrows(elem);
            } catch (Exception e) {
                throw toRuntime(e);
            }
        }

        void acceptThrows(T elem) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingBiConsumer<T, U> extends BiConsumer<T, U> {
        @Override
        default void accept(T t, U u) {
            try {
                acceptThrows(t, u);
            } catch (Exception e) {
                throw toRuntime(e);
            }
        }

        void acceptThrows(T t, U u) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, V> extends BiFunction<T, U, V> {
        @Override
        default V apply(T t, U u) {
            try {
                return applyThrows(t, u);
            } catch (Exception e) {
                throw toRuntime(e);
            }
        }

        V applyThrows(T t, U u) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingRunnable extends Runnable {
        @Override
        default void run() {
            try {
                runThrows();
            } catch (Exception e) {
                throw toRuntime(e);
            }
        }

        void runThrows() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> extends Supplier<T> {
        @Override
        default T get() {
            try {
                return getThrows();
            } catch (Exception e) {
                throw toRuntime(e);
            }
        }

        T getThrows() throws Exception;
    }

    public static RuntimeException toRuntime(Exception e) {
        if (e instanceof RuntimeException)
            return (RuntimeException) e;

        return new RuntimeException(e);
    }

    public static <T, U> U getSafeF(T t, Function<T, U> get) {
        return t != null ? get.apply(t) : null;
    }

    public static <T> void remove(List<T> l, Predicate<T> p) {
        int i = 0;
        while (i < l.size()) {
            T t = l.get(i);
            if (p.test(t))
                l.remove(i);
            else
                i++;
        }
    }

    public static <T> Set<T> minus(Set<T> s, Collection<T> c) {
        Set<T> cSet = new HashSet<>(c);
        Set<T> diff = new HashSet<>();
        for (T t : s)
            if (!cSet.contains(t))
                diff.add(t);
        return diff;
    }

    public static <T> OptionF<T> tryF(ThrowingSupplier<T> f) {
        try {
            return o(f.get());
        } catch (Exception ignored) {
            return emptyO();
        }
    }

    public static void tryF(ThrowingRunnable f) {
        try {
            f.run();
        } catch (Exception ignored) {
        }
    }

    public static String catchEx(ThrowingRunnable f) {
        try {
            f.run();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static void print(Object o) {
        System.out.println(o);
    }

    public static BigDecimal bd(int i) {
        return BigDecimal.valueOf(i);
    }

    public static BigDecimal bd(double d) {
        return BigDecimal.valueOf(d);
    }

    public static BigDecimal bd(String s) {
        return new BigDecimal(s);
    }

    public static Double toDouble(BigDecimal b) {
        return b != null ? b.doubleValue() : null;
    }

    public static Optional<Double> toDoubleO(BigDecimal b) {
        return b != null ? Optional.of(b.doubleValue()) : Optional.empty();
    }

    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
}
