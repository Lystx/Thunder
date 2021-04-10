package io.lightning.network.connection.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

@Getter @AllArgsConstructor
public class ClientPacketHandler implements CompletionHandler<Integer, ByteBuffer> {

    private final LightningClient lightningClient;

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        LightningClient.DIRECT_BUFFER_POOL.give(buffer);

        synchronized (lightningClient.getOutgoingPackets()) {
            ByteBuffer payload = lightningClient.getPacketsToFlush().poll();

            if (payload == null) {
                lightningClient.getWriteInProgress().set(false);
                return;
            }

            lightningClient.getChannel().write(payload, payload, this);
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer buffer) {

        LightningClient.DIRECT_BUFFER_POOL.give(buffer);

        synchronized (lightningClient.getOutgoingPackets()) {
            ByteBuffer discard;

            while ((discard = lightningClient.getPacketsToFlush().poll()) != null) {
                LightningClient.DIRECT_BUFFER_POOL.give(discard);
            }
        }

        lightningClient.getWriteInProgress().set(false);
    }
}
