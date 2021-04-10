
package io.lightning.network.bufferpools;

import java.nio.Buffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;


public abstract class AbstractBufferPool<T extends Buffer> implements AutoCloseable {

    private static final int HEURISTIC = 3;

    private final NavigableMap<Integer, Deque<T>> buffers = new TreeMap<>();

    protected abstract T allocate(int capacity);

    public T take(int capacity) {
        Optional<T> maybeBuffer;

        synchronized (buffers) {
            maybeBuffer = buffers.tailMap(capacity, true)
                .values()
                .stream()
                .map(Deque::poll)
                .filter(Objects::nonNull)
                .findAny();
        }

        return maybeBuffer.map((T buffer) -> {
            buffer.clear().limit(capacity);
            return buffer;
        }).orElseGet(() -> allocate(capacity));
    }

    public void give(T buffer) {
        synchronized (buffers) {
            buffers.computeIfAbsent(buffer.capacity(), capacity -> new ArrayDeque<>(HEURISTIC)).offer(buffer);
        }
    }

    @Override
    public void close() {
        synchronized (buffers) {
            buffers.clear();
        }
    }
}
