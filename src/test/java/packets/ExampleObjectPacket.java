package packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.vson.annotation.other.Vson;
import io.thunder.utils.vson.elements.object.VsonObject;
import io.thunder.utils.vson.tree.VsonTree;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import utils.ExampleObject;

@Getter @AllArgsConstructor
public class ExampleObjectPacket extends Packet {

    private ExampleObject exampleObject;

    @Override @SneakyThrows
    public void write(PacketBuffer buf) {


        String exampleO = VsonTree.newTree(exampleObject).toVson().toString();

        buf.writeString(exampleO);
    }

    @Override @SneakyThrows
    public void read(PacketBuffer buf) {

        String string = buf.readString();

        System.out.println(string);

        exampleObject = VsonTree.newTree(ExampleObject.class).from(new VsonObject(string), ExampleObject.class);
    }
}
