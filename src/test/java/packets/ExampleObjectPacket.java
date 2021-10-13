package packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import utils.ExampleObject;

@Getter @AllArgsConstructor
public class ExampleObjectPacket extends Packet {

    private ExampleObject exampleObject;

    @Override @SneakyThrows
    public void write(PacketBuffer buf) {


    }

    @Override @SneakyThrows
    public void read(PacketBuffer buf) {

        String string = buf.readString();

        System.out.println(string);
    }
}
