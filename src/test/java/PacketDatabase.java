import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketDatabase extends Packet {

    private String database;
    private UUID uuid;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(database);
        buf.writeUUID(uuid);
    }

    @Override
    public void read(PacketBuffer buf) {
        database = buf.readString();
        uuid = buf.readUUID();
    }
}
