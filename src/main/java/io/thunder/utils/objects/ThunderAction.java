package io.thunder.utils.objects;


import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface ThunderAction<T> {

    /**
     * Performs the action of this
     * ThunderAction synchronously
     */
    void perform();

    /**
     * Performs the action of this
     * ThunderAction asynchronously
     */
    void performAsync();

    /**
     * Performs this action
     * but you can work with the given T
     * (async)
     *
     * @param consumer the consumer
     */
    void performAsync(Consumer<T> consumer);

    /**
     * Performs this action
     * but you can work with the given T
     * (sync)
     *
     * @param consumer the consumer
     */
    void perform(Consumer<T> consumer);

    /**
     * Returns the Value for the Consumer
     *
     * @return T value
     */
    T get();

    /**
     * Performs this action later
     *
     * @param time the time (e.g. "1")
     * @param timeUnit the unit (e.g. "Minute")
     */
    void performAfter(long time, TimeUnit timeUnit);
}
