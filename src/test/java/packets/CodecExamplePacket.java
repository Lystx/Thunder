package packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CodecExamplePacket extends Packet {

    private boolean aBoolean;
    private int anInt;
    private String aString;

    @Override
    public void write(PacketBuffer buf) {
        buf.write(aBoolean, anInt, aString);
    }

    @Override
    public void read(PacketBuffer buf) {
        aBoolean = buf.readBoolean();
        anInt = buf.readInt();
        aString = buf.readString();
    }
}
