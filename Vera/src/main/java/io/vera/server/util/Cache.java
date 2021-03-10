
package io.vera.server.util;

import io.vera.util.Tuple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;


@ThreadSafe
public class Cache<T, M> {

    private static final int MAX_EVICTION_ITERATIONS = 100;
    private static final BiFunction NOP = (a, b) -> false;
    private final ConcurrentHashMap<T, Tuple<M, Long>> cache = new ConcurrentHashMap<>();
    private final long timeout;
    private final BiFunction<T, M, Boolean> expire;

    public Cache(long timeout) {
        this(timeout, NOP);
    }


    public Cache(long timeout, BiFunction<T, M, Boolean> expire) {
        this.timeout = timeout;
        this.expire = expire;
    }

    @Nonnull
    public M get(T key, Supplier<M> loader) {
        this.scan();
        Tuple<M, Long> instance = this.cache.get(key);

        if (instance == null) {
            return this.cache.computeIfAbsent(key, t -> new Tuple<>(loader.get(), System.currentTimeMillis())).getA();
        } else {
            if (System.currentTimeMillis() - instance.getB() > this.timeout) {
                this.cache.computeIfPresent(key, (k, v) -> this.expire.apply(key, instance.getA()) ? null : instance);
                return this.cache.computeIfAbsent(key, t -> new Tuple<>(loader.get(), System.currentTimeMillis())).getA();
            }

            return instance.getA();
        }
    }

    public M compute(T key, BiFunction<T, M, M> loader) {
        Tuple<M, Long> compute = this.cache.compute(key, (k, v) -> {
            if (v == null) {
                M apply = loader.apply(k, null);
                return new Tuple<>(apply, System.currentTimeMillis());
            } else {
                M apply = loader.apply(k, v.getA());
                return apply == null ? null : new Tuple<>(apply, System.currentTimeMillis());
            }
        });

        return compute == null ? null : compute.getA();
    }

    @Nullable
    public M get(T key) {
        this.scan();
        Tuple<M, Long> instance = this.cache.get(key);

        if (instance == null) {
            return null;
        }

        if (System.currentTimeMillis() - instance.getB() > this.timeout) {
            Tuple<M, Long> tuple = this.cache.computeIfPresent(key, (k, v) -> this.expire.apply(key, instance.getA()) ? null : instance);
            return tuple == null ? null : tuple.getA();
        }

        return instance.getA();
    }

    public void put(T key, M value) {
        this.scan();
        this.cache.put(key, new Tuple<>(value, System.currentTimeMillis()));
    }

    private void scan() {
        long time = System.currentTimeMillis();

        int rounds = 0;
        for (Iterator<Map.Entry<T, Tuple<M, Long>>> it =
             this.cache.entrySet().
                stream().
                sorted(Comparator.comparingLong(o -> o.getValue().getB())).
                filter(e -> time - e.getValue().getB() > this.timeout).
                iterator();
             it.hasNext() && rounds < MAX_EVICTION_ITERATIONS;
             rounds++) {
            Map.Entry<T, Tuple<M, Long>> e = it.next();

            this.cache.computeIfPresent(e.getKey(), (k, v) -> this.expire.apply(e.getKey(), e.getValue().getA()) ? null : e.getValue());
        }
    }
}