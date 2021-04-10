
import io.lightning.network.packet.Packet;

import java.util.function.Consumer;

public class ExamplePacket extends Packet {

    private final String name;
    private final int alter;

    public ExamplePacket(String name, int alter) {
        this.name = name;
        this.alter = alter;
    }

    public String getName() {
        return name;
    }

    public int getAlter() {
        return alter;
    }
}
