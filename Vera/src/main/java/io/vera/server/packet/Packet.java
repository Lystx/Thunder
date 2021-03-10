
package io.vera.server.packet;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Packet {

    public enum Bound {
        CLIENT,
        SERVER;

        public Bound of(Class<? extends Packet> cls) {
            if (cls.getSuperclass() == PacketIn.class) {
                return SERVER;
            } else {
                return CLIENT;
            }
        }
    }

    private final int id;

    public Packet(Class<? extends Packet> cls) {
        int info = PacketRegistry.packetInfo(cls);
        this.id = PacketRegistry.idOf(info);
    }

    public int id() {
        return this.id;
    }
}