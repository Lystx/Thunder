package packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.packet.impl.EmptyPacket;
import io.thunder.packet.impl.JsonPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class BasicExamplePacket extends JsonPacket {

    private final String name;
    private final int age;

    public BasicExamplePacket(String name, int age) {
        this.name = name;
        this.age = age;
    }

}
