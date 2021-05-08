import io.thunder.connection.ThunderConnection;
import io.thunder.manager.packet.Packet;
import io.thunder.manager.packet.PacketBuffer;
import io.thunder.manager.packet.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class SamplePacket extends Packet {

    private String name;
    private int age;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(name);
        buf.writeInt(age);
    }

    @Override
    public void read(PacketBuffer buf) {
        name = buf.readString();
        age = buf.readInt();
    }

    @Override
    public void handle(ThunderConnection thunderConnection) {
        respond(ResponseStatus.SUCCESS, "Das");
    }
}
