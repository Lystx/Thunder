package packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import utils.ExampleObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor
public class ExampleObjectPacket extends Packet {

    private ExampleObject exampleObject;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(exampleObject.getName());
        buf.writeUUID(exampleObject.getUniqueId());
        buf.writeLong(exampleObject.getStamp());

        buf.writeInt(exampleObject.getData().size());
        for (String datum : exampleObject.getData()) {
            buf.writeString(datum);
        }
    }

    @Override
    public void read(PacketBuffer buf) {
        String name = buf.readString();
        UUID uniqueId = buf.readUUID();
        long stamp = buf.readLong();
        int size = buf.readInt();
        List<String> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            data.add(buf.readString());
        }
        exampleObject = new ExampleObject(name, uniqueId, stamp, data);
    }
}
