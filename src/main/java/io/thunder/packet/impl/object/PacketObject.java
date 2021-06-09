package io.thunder.packet.impl.object;

import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.Serializer;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketObject<T extends Serializable> extends Packet {

    private T object;
    private long time;

    public PacketObject(T object, long time) {
        this.object = object;
        this.time = time;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeBytes(new Serializer<>(this.object).serialize());
        buf.writeLong(time);
    }

    @Override
    public void read(PacketBuffer buf) {
        byte[] b = buf.readBytes();
        object = (T) new Serializer<>().deserialize(b);
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
