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

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONArray;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static io.github.jleblanc64.libcustom.functional.Functor.tryF;

@NoArgsConstructor
@AllArgsConstructor
public class ListF<T> implements List<T> {
    private List<T> l = new ArrayList<>();

    public <U> ListF<U> map(Functor.ThrowingFunction<T, U> fun) {
        return f(stream().map(fun).collect(Collectors.toList()));
    }

    public boolean allMatch(Predicate<T> p) {
        return stream().allMatch(p);
    }

    public <U> ListF<U> map(Function<T, U> f) {
        return f(map(f::apply));
    }

    public <U> ListF<U> mapIdx(BiFunction<T, Integer, U> f) {
        List<U> result = new ArrayList<>();
        for (int i = 0; i < l.size(); i++)
            result.add(f.apply(l.get(i), i));

        return f(result);
    }

    // https://www.scala-lang.org/api/2.13.3/scala/collection/immutable/List.html#foldLeft[B](z:B)(op:(B,A)=%3EB):B
    public <B> B fold(B z, BiFunction<B, T, B> op) {
        B result = z;
        for (T t : l)
            result = op.apply(result, t);

        return result;
    }

    public ListF<T> sorted() {
        return f(stream().sorted().collect(Collectors.toList()));
    }

    public double sumD() {
        if (l.isEmpty())
            return 0.0;

        if (!(l.get(0) instanceof Double))
            throw new RuntimeException("Must be list of doubles");

        ListF<Double> doubles = map(t -> (Double) t);
        return doubles.fold(0.0, Double::sum);
    }

    public T duplicate() {
        Set<T> alreadySeen = new HashSet<>();
        for (T t : l) {
            if (alreadySeen.contains(t))
                return t;
            else
                alreadySeen.add(t);
        }
        return null;
    }

    public void forEachF(Functor.ThrowingConsumer<T> f) {
        l.forEach(f);
    }

    public T findSafe(Predicate<T> p) {
        return stream().filter(p).findFirst().orElse(null);
    }

    public int findIdx(Predicate<T> p) {
        for (int i = 0; i < l.size(); i++)
            if (p.test(l.get(i)))
                return i;

        return -1;
    }

    public ListF<T> removeF(T... ts) {
        for (T t : ts)
            l.remove(t);
        return this;
    }

    public ListF<T> merge(Collection<T> c) {
        l.addAll(c);
        return this;
    }

    public static <T> ListF<T> merge(Collection<T>... cs) {
        ListF<T> l0 = empty();
        for (Collection<T> c : cs)
            l0.merge(c);

        return l0;
    }

    public ListF<ListF<T>> partition(int size) {
        return f(Lists.partition(l, size)).map(ListF::f);
    }

    public ListF<T> firstN(int n) {
        ListF<T> result = empty();
        int howMany = Math.min(l.size(), n);
        for (int i = 0; i < howMany; i++)
            result.add(l.get(i));

        return result;
    }

    public String join(String delim) {
        if (l.isEmpty())
            return "";

        Function<T, String> stringify = allMatch(x -> x instanceof String) ? x -> (String) x : Object::toString;
        List<String> lString = map(stringify);
        return String.join(delim, lString);
    }

    // preserves oredering
    public <K> Map<K, T> toMap(Function<T, K> keyGetter) {
        Map<K, T> map = new LinkedHashMap<>();
        for (T t : l)
            map.put(keyGetter.apply(t), t);

        return map;
    }

    public <K, V> Map<K, V> toMap(Function<T, K> keyGetter, Function<T, V> valueMapper) {
        Map<K, V> map = new LinkedHashMap<>();
        for (T t : l) {
            K k = keyGetter.apply(t);
            if (k == null)
                continue;

            V v = valueMapper.apply(t);
            map.put(k, v);
        }

        return map;
    }

    public <K, V> Map<K, ListF<V>> groupBy(Function<T, K> keyGetter, Function<T, V> valueMapper) {
        Map<K, ListF<V>> map = new HashMap<>();
        for (T t : l) {
            K k = keyGetter.apply(t);
            if (k == null)
                continue;

            V v = valueMapper.apply(t);

            map.computeIfAbsent(k, x -> empty()).add(v);
        }

        return map;
    }

    public static <T> ListF<T> f(Collection<T> l) {
        if (l == null)
            return empty();

        if (l instanceof List)
            return new ListF<>((List<T>) l);

        return new ListF<>(new ArrayList<>(l));
    }

    public static <T> ListF<T> f(T[] l) {
        return new ListF<>(new ArrayList<>(Arrays.asList(l)));
    }

    public static ListF<Integer> fI(int[] l) {
        return f(Arrays.stream(l).boxed().toArray(Integer[]::new));
    }

    public static <T> ListF<T> f(JSONArray ja, Class<T> clazz) {
        ListF<T> l = ListF.empty();
        for (int i = 0; i < ja.length(); i++)
            l.add((T) ja.get(i));

        return l;
    }

    // new ArrayList<>() call so that list is mutable
    public static <T> ListF<T> of(T... ts) {
        return f(newArrayList(ts));
    }

    public static <T> ListF<T> empty() {
        return f(new ArrayList<>());
    }

    public T max(Comparator<? super T> comparator) {
        return maxOpt(comparator).get();
    }

    public Optional<T> maxOpt(Comparator<? super T> comparator) {
        return l.stream().max(comparator);
    }

    public T min(Comparator<? super T> comparator) {
        return l.stream().min(comparator).get();
    }

    public T last() {
        return l.get(l.size() - 1);
    }

    @Override
    public String toString() {
        return l.toString();
    }

    @Override
    public boolean equals(Object o) {
        return l.equals(o);
    }

    @Override
    public int hashCode() {
        return l.hashCode();
    }

    public <U> ListF<U> parallelMap(Function<T, U> f, int parallelism) throws ExecutionException, InterruptedException {
        Supplier<List<U>> supplier = () -> l.stream().parallel().map(f).collect(Collectors.toList());

        ForkJoinPool pool = null;
        try {
            pool = new ForkJoinPool(parallelism);
            return f(pool.submit(supplier::get).get());
        } finally {
            ForkJoinPool finalPool = pool;
            tryF(() -> finalPool.shutdown());
        }
    }

    @SneakyThrows
    public void parallelForEach(Consumer<T> f, int parallelism) {
        Runnable supplier = () -> l.stream().parallel().forEach(f);

        ForkJoinPool pool = null;
        try {
            pool = new ForkJoinPool(parallelism);
            pool.submit(supplier).get();
        } finally {
            ForkJoinPool finalPool = pool;
            tryF(() -> finalPool.shutdown());
        }
    }

    // boilerplate forward methods
    @Override
    public int size() {
        return l.size();
    }

    @Override
    public boolean isEmpty() {
        return l.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return l.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return l.iterator();
    }

    @Override
    public Object[] toArray() {
        return l.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return l.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return l.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return l.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return l.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return l.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return l.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return l.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return l.retainAll(c);
    }

    @Override
    public void clear() {
        l.clear();
    }

    @Override
    public T get(int index) {
        return l.get(index);
    }

    @Override
    public T set(int index, T element) {
        return l.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        l.add(index, element);
    }

    @Override
    public T remove(int index) {
        return l.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return l.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return l.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return l.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return l.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return l.subList(fromIndex, toIndex);
    }
}
