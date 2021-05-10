package packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ResponseExamplePacket extends Packet {

    private int a;
    private int b;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeInt(a);
        buf.writeInt(b);
    }

    @Override
    public void read(PacketBuffer buf) {
        a = buf.readInt();
        b = buf.readInt();
    }
}
