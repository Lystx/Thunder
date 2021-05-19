package packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor @Getter
public class NullSafeExamplePacket extends Packet {

    private UUID uuid;
    private String name;

    @Override
    public void write(PacketBuffer buf) {
        buf.nullSafe().writeUUID(uuid);
        buf.nullSafe().writeString(name);
    }

    @Override
    public void read(PacketBuffer buf) {
        uuid = buf.nullSafe().readUUID();
        name = buf.nullSafe().readString();
    }
}
