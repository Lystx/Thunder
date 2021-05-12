package io.thunder.impl.other;

import io.thunder.Thunder;
import io.thunder.utils.ThunderAction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProvidedThunderAction<T> implements ThunderAction<T> {

    private final Consumer<T> consumer;
    private final T t;

    /**
     * Creates a new {@link ThunderAction}
     *
     * @param consumer the action it should do
     * @param t the object it should work with
     * @return the created ThunderACtion
     */
    public static <T> ThunderAction<T> newInstance(Consumer<T> consumer, T t) {
        return new ProvidedThunderAction<>(consumer, t);
    }

    /**
     * Executes the given Action
     */
    public void perform() {
        this.perform(t -> {});
    }

    /**
     * Executes the given Action asynchronously
     */
    public void performAsync() {
        Thunder.EXECUTOR_SERVICE.execute(this::perform);
    }

    /**
     * Executes the given Action asynchronously
     * but accepts a consumer to work with if done
     *
     * @param consumer the consumer
     */
    public void performAsync(Consumer<T> consumer) {
        Thunder.EXECUTOR_SERVICE.execute(() -> perform(consumer));
    }

    /**
     * Executes the given Action synchronously
     * but accepts a consumer to work with if done
     *
     * @param consumer the consumer
     */
    public void perform(Consumer<T> consumer) {
        this.consumer.accept(t);
        consumer.accept(this.t);
    }

    /**
     * Returns the Object of the Consumer
     *
     * @return object
     */
    public T get() {
        return this.t;
    }
}
