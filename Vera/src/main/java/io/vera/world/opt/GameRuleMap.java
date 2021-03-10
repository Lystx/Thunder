
package io.vera.world.opt;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ThreadSafe
public final class GameRuleMap {
    private final Map<GameRule<?>, Object> changes = new ConcurrentHashMap<>();

    @Nonnull
    public <T> T get(GameRule<T> key) {
        T t = (T) this.changes.get(key);
        if (t == null) {
            return key.getDefault();
        }

        return t;
    }

    public <T> void set(@Nonnull GameRule<T> key, T value) {
        if (!key.getDefault().equals(value)) {
            this.changes.put(key, value);
        }
    }

    public <T> boolean isSet(GameRule<T> key) {
        return this.changes.containsKey(key);
    }

    public <T> void reset(GameRule<T> key) {
        this.changes.remove(key);
    }

    public void resetAll() {
        this.changes.clear();
    }

    public void copyTo(GameRuleMap map) {
        map.changes.putAll(this.changes);
    }
}