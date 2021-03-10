
package io.vera.server.config;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

@ThreadSafe
public class ConcurrentLinkedStringMap<V> {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final LinkedHashMap<String, V> map = new LinkedHashMap<>();

    public void put(String key, V value) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            this.map.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    public V remove(String key) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            return this.map.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public V get(String key) {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return this.map.get(key);
        } finally {
            lock.unlock();
        }
    }

    public boolean containsKey(String key) {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return this.map.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    public Set<String> keySet() {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return this.map.keySet();
        } finally {
            lock.unlock();
        }
    }

    public Collection<V> values() {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return this.map.values();
        } finally {
            lock.unlock();
        }
    }

    public void forEach(BiConsumer<String, V> consumer) {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            this.map.forEach(consumer);
        } finally {
            lock.unlock();
        }
    }

    public Set<Map.Entry<String, V>> entrySet() {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return this.map.entrySet();
        } finally {
            lock.unlock();
        }
    }
}