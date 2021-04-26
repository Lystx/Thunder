package io.thunder.manager.utils;

import io.thunder.Thunder;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor
public class ThunderAction<T> {

    private final Consumer<T> consumer;
    private final T t;


    public void perform() {
        this.perform(t -> {});
    }

    public void performAsync() {
        Thunder.EXECUTOR_SERVICE.execute(this::perform);
    }

    public void performAsync(Consumer<T> consumer) {
        Thunder.EXECUTOR_SERVICE.execute(() -> perform(consumer));
    }

    public void perform(Consumer<T> consumer) {
        this.consumer.accept(t);
        consumer.accept(this.t);
    }

    public T get() {
        return this.t;
    }
}
