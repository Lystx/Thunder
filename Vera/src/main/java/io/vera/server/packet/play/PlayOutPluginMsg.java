
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Plugin message packet, used to send the brand after the
 * {@link PlayOutJoinGame} packet has been sent.
 */
@Immutable
public final class PlayOutPluginMsg extends PacketOut {
    /**
     * The branding packet
     */
    public static final PlayOutPluginMsg BRAND =
            new PlayOutPluginMsg("MC|Brand", "tridentsdk".getBytes(NetData.NET_CHARSET));
    /**
     * The channel name
     */
    private final String channel;
    /**
     * The message payload
     */
    private final byte[] data;

    public PlayOutPluginMsg(String channel, byte[] data) {
        super(PlayOutPluginMsg.class);
        if (data.length >= Short.MAX_VALUE) {
            throw new ArrayIndexOutOfBoundsException("Data must have len < Short.MAX_VALUE");
        }

        this.channel = channel;
        this.data = data;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wstr(buf, this.channel);
        buf.writeBytes(this.data);
    }
}
