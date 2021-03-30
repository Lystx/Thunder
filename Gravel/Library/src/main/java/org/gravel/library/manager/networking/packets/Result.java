package org.gravel.library.manager.networking.packets;

import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter @RequiredArgsConstructor
public class Result<R> implements Serializable {

    private final UUID uniqueId;
    private final R result;
    private Throwable throwable;


    public <T> T getAs(Class<T> tClass) {
        return ((VsonObject)getResult()).getAs(tClass);
    }

    public Result<R> onError(Consumer<Throwable> consumer) {
        if (this.throwable != null) {
            consumer.accept(this.throwable);
        }
        return this;
    }

    public Result<R> onResultSet(Consumer<Result<R>> consumer) {
        consumer.accept(this);
        return this;
    }

    public Result<R> onReceiveObject(Consumer<R> consumer) {
        consumer.accept(getResult());
        return this;
    }
}
