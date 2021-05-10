package io.thunder.packet.object;

import io.thunder.connection.base.ThunderChannel;

public interface ObjectHandler<T> {

    /**
     * Called when the Channel has read an Object
     *
     * @param channel the channel
     * @param t the object
     * @param time the time it took
     */
    void readChannel(ThunderChannel channel, T t, long time);

    /**
     * Returns the Class of the Object
     * This is used to identify and seperate
     * the {@link ObjectHandler}s from eachother
     *
     * @return class
     */
    Class<T> getObjectClass();
}
