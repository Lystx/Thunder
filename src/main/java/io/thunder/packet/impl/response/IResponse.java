package io.thunder.packet.impl.response;

import java.util.UUID;

/**
 * This class is mainly used to return a {@link Response}
 * with a given Object to return but you can also get the {@link ResponseStatus}
 * and / or the {@link UUID} and some infos in a {@link String}-Array
 * @param <T>
 */
public interface IResponse<T> {

    /**
     * Returns the Object
     * @return object
     */
    T get();

    /**
     * Returns the ResponseStatus
     * @return status
     */
    ResponseStatus getStatus();

    /**
     * Returns the {@link UUID} of this Response
     * @return uuid
     */
    UUID getUniqueId();

    /**
     * Returns info for this response
     * @return string-array
     */
    String getMessage();

    /**
     * Returns the raw {@link Response}
     * @return response
     */
    Response raw();
}
