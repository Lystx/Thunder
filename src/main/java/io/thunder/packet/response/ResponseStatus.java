package io.thunder.packet.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * Status of a packet process which can be either positive or negative<br>
 * Similar to the HTTP responses
 */
@AllArgsConstructor @Getter
public enum ResponseStatus {

    SUCCESS(0x00),
    FAILED(0x40),
    FORBIDDEN(0x42),
    CONFLICT(0x43),
    NOT_FOUND(0x44),
    INTERNAL_ERROR(0x81, true),
    BAD_REQUEST(0x82, true);

    @Getter
    private final int id;
    private final boolean critically;

    ResponseStatus(int id) {
        this(id, false);
    }

}
