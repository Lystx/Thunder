package org.gravel.library.manager.networking.connection.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum PacketState {

    SUCCESS("§a"),
    FAILED("§c"),
    NULL("§4"),
    RETRY("§6");

    private final String color;

    public String toString() {
        return this.color + name();
    }
}
