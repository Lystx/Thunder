package io.thunder.packet.response;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This packet is for respond to anything. Can be used as response to every packet
 */
@NoArgsConstructor @Getter
public class PacketRespond extends Packet {

    /**
     * The message of the respond (can be a list of data (serialized to String) or just plain messages)
     */
    private String message;

    /**
     * The status of the response (similar to http)
     */
    private ResponseStatus status;

    public PacketRespond(String message, ResponseStatus status) {
        this.message = message;
        this.status = status;
    }

    public PacketRespond(ResponseStatus status) {
        this("No message provided for this status", status);
    }

    @Override
    public void read(PacketBuffer buf) {
        this.message = buf.readString();
        this.status = buf.readEnum(ResponseStatus.class);
    }

    @Override
    public void write(PacketBuffer buf)  {
        buf.writeString(message);
        buf.writeEnum(status);
    }

}
