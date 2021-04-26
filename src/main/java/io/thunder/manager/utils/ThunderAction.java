package io.thunder.manager.utils;


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
}
