import io.thunder.annotation.PacketData;
import io.thunder.connection.ThunderConnection;
import io.thunder.manager.packet.BufferedPacket;
import io.thunder.manager.packet.PacketBuffer;
import io.thunder.manager.packet.PacketReader;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor

@PacketData(
        packet = TestBufferedPacket.class,
        id = 0x00,
        compress = true,
        usage = {"This packet is used to show an example of a buffered packet"}
)
public class TestBufferedPacket extends BufferedPacket {

    private String name;
    private int age;

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeString(name);
        buffer.writeInt(age);
    }

    @Override
    public void read(PacketReader reader) {
        name = reader.readString();
        age = reader.readInt();
    }


    @Override
    public void handle(ThunderConnection thunderConnection) {
        System.out.println("[Packet] BufferedPacket got handled by ThunderSession with ID " + thunderConnection.getSession().getUniqueId());
    }
}
