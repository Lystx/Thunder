import io.thunder.manager.packet.BufferedPacket;
import io.thunder.manager.packet.PacketBuffer;
import io.thunder.manager.packet.PacketReader;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class TestBufferedObjectPacket extends BufferedPacket {

    private ExampleObject exampleObject;

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeObject(exampleObject);
    }

    @Override
    public void read(PacketReader reader) {
        exampleObject = reader.readObject(ExampleObject.class);
    }

    @Getter @AllArgsConstructor
    public static class ExampleObject {

        private final String name;
        private final UUID uniqueId;
        private final long time;

    }
}
