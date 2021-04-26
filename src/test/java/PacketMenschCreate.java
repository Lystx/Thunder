import io.thunder.manager.packet.BufferedPacket;
import io.thunder.manager.packet.PacketBuffer;
import io.thunder.manager.packet.PacketReader;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter @AllArgsConstructor
public class PacketMenschCreate extends BufferedPacket {

    private Mensch mensch;

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeObject(mensch);
    }

    @Override
    public void read(PacketReader reader) {
        mensch = reader.readObject(Mensch.class);
    }



}
