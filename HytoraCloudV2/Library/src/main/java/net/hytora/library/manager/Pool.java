package net.hytora.library.manager;

import net.hytora.library.elements.base.CloudObject;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Pool<T> {

    List<T> getAll();

    T getCached(String name);

    T getCached(UUID uniqueId);

    T getSync(String name);

    T getSync(UUID uniqueId);

    void getAsync(String name, Consumer<T> consumer);

    void getAsync(UUID uniqueId, Consumer<T> consumer);


    default T filter(String name) {
        for (T t : this.getAll()) {
            if (t instanceof CloudObject<?>) {
                CloudObject<T> cloudObject = (CloudObject<T>) t;
                if (cloudObject.getName().equalsIgnoreCase(name)) {
                    return cloudObject.get();
                }
            }
        }
        return null;
    }
    default T filter(UUID uniqueId) {
        for (T t : this.getAll()) {
            if (t instanceof CloudObject<?>) {
                CloudObject<T> cloudObject = (CloudObject<T>) t;
                if (cloudObject.getUniqueId().equals(uniqueId)) {
                    return cloudObject.get();
                }
            }
        }
        return null;
    }
}
