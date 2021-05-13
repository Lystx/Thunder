package io.thunder.packet.impl.object;

import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketObject<T> extends Packet {

    private T object;
    private long time;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(object.getClass().getSimpleName());
        buf.write(object);
        buf.writeLong(time);
    }

    @Override
    public void read(PacketBuffer buf) {
        object = (T) buf.read(buf.readString());
        time = buf.readLong();
    }


    @Override
    public void handle(ThunderConnection thunderConnection) {
        for (ObjectHandler<?> objectHandler : thunderConnection.getObjectHandlers()) {
            if (objectHandler.getObjectClass().equals(this.object.getClass())) {
                ObjectHandler<T> tObjectHandler = (ObjectHandler<T>) objectHandler;
                tObjectHandler.readChannel(this.channel, this.object, System.currentTimeMillis() - this.time);
            }
        }
    }
}
