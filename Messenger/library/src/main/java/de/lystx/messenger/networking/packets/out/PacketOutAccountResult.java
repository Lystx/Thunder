package de.lystx.messenger.networking.packets.out;

import de.lystx.messenger.networking.connection.packet.Packet;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOutAccountResult extends Packet implements Serializable {

    private final VsonObject vsonObject;

}
