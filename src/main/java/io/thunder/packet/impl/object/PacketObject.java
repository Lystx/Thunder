package io.thunder.packet.impl.object;

import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PacketObject<T> extends Packet {

    private T object;
    private long time;

    public PacketObject(T object, long time) {
        this.object = object;
        this.time = time;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(new Serializer<>(this.object).serialize());
        buf.writeLong(time);
    }

    @Override
    public void read(PacketBuffer buf) {
        String s = buf.readString();
        object = (T) new Serializer<>().deserialize(s);
        time = buf.readLong();
    }


    @Override
    public void handle(ThunderConnection thunderConnection) {
        for (ObjectHandler<?> objectHandler : thunderConnection.getObjectHandlers()) {
            if (objectHandler.getObjectClass().equals(this.object.getClass())) {
                ObjectHandler<T> tObjectHandler = (ObjectHandler<T>) objectHandler;
                tObjectHandler.readChannel(this.channel, this.object, (System.currentTimeMillis() - this.time));
            }
        }
    }
}
