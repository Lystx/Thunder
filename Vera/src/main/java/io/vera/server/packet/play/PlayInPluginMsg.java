
package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.plugin.channel.PluginChannel;
import io.vera.plugin.channel.SimpleChannelListener;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.server.plugin.VeraPluginChannel;

import javax.annotation.concurrent.Immutable;
import java.io.ByteArrayOutputStream;

/**
 * Packet sent by the client after {@link PlayOutPlayerAbilities}
 * to confirm to the server the client brand.
 */
@Immutable
public final class PlayInPluginMsg extends PacketIn {
    public PlayInPluginMsg() {
        super(PlayInPluginMsg.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        VeraPlayer player = client.getPlayer();
        String channel = NetData.rstr(buf);

        buf.markReaderIndex();
        byte[] arr = NetData.arr(buf);
        buf.resetReaderIndex();

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : VeraPluginChannel.getListeners().values()) {
                listener.messageReceived(channel, player, arr);
            }
        });

        if ("MC|Brand".equals(channel)) {
            NetData.rstr(buf);
            return;
        }

        if (channel.equals(VeraPluginChannel.REGISTER)) {
            while (buf.isReadable()) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte b = buf.readByte();
                while (b != 0x00) {
                    stream.write(b);
                    if (!buf.isReadable()) {
                        break;
                    }

                    b = buf.readByte();
                }

                String name = new String(stream.toByteArray(), NetData.NET_CHARSET);
                PluginChannel c = VeraPluginChannel.getChannel(name, VeraPluginChannel::new);
                c.addRecipient(player);
            }
            return;
        }

        if (channel.equals(VeraPluginChannel.UNREGISTER)) {
            while (buf.isReadable()) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte b = buf.readByte();
                while (b != 0x00) {
                    stream.write(b);
                    if (!buf.isReadable()) {
                        break;
                    }

                    b = buf.readByte();
                }

                String name = new String(stream.toByteArray(), NetData.NET_CHARSET);
                PluginChannel c = VeraPluginChannel.get(name);
                if (c != null) {
                    c.closeFor(player.getUuid());
                }
            }
            return;
        }
    }
}