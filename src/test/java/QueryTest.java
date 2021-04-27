import io.thunder.Thunder;
import io.thunder.connection.ThunderConnection;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.manager.packet.Packet;
import io.thunder.manager.packet.PacketBuffer;
import io.thunder.manager.packet.PacketReader;
import io.thunder.manager.packet.ThunderPacket;
import io.thunder.manager.packet.featured.Query;
import io.thunder.manager.packet.featured.QueryPacket;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.function.Consumer;

public class QueryTest {

    public static void main(String[] args) {
        ThunderServer thunderServer = ThunderServer.newInstance();
        ThunderClient thunderClient = ThunderClient.newInstance();

        thunderServer.start(1401).perform();
        thunderClient.connect("localhost", 1401).perform(new Consumer<ThunderClient>() {
            @Override
            public void accept(ThunderClient thunderClient) {
                final Query query = thunderClient.sendQuery(new QueryTextPacket("Name", 35));

                query.handleResult(new Consumer<VsonObject>() {
                    @Override
                    public void accept(VsonObject vsonObject) {
                        System.out.println(vsonObject);
                    }
                });
            }
        });
    }



    @Getter @AllArgsConstructor
    public static class QueryTextPacket extends QueryPacket {

        private String name;
        private int age;

        @Override
        public void handleQuery(VsonObject source) {
            source.append("allow", true);
            source.append("text", "Hello world");
        }


        @Override
        public void read(PacketReader packetReader) {
            super.read(packetReader);
            name = packetReader.readString();
            age = packetReader.readInt();
        }


        @Override
        public void write(PacketBuffer buffer) {
            super.write(buffer);
            buffer.writeString(name);
            buffer.writeInt(age);
        }
    }
}
