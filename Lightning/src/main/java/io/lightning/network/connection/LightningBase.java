
package io.lightning.network.connection;

import io.lightning.network.packet.Packet;
import io.lightning.network.packet.PacketAdapter;
import io.lightning.network.packet.PacketHandler;
import io.lightning.network.utility.exposed.cryptography.CryptographicFunction;
import io.lightning.network.utility.other.LightningSettings;
import lombok.Getter;

import javax.crypto.Cipher;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public abstract class LightningBase<T> {

    public static final int BUFFER_SIZE = 8_192;

    protected final Collection<T> connectListeners;
    protected final Collection<T> preDisconnectListeners;
    protected final Collection<T> postDisconnectListeners;
    protected final List<LightningSettings> settings;
    protected final PacketAdapter packetAdapter;


    protected boolean encryptionNoPadding;
    protected Cipher decryptionCipher;
    protected Cipher encryptionCipher;
    protected CryptographicFunction decryptionFunction;
    protected CryptographicFunction encryptionFunction;


    public LightningBase() {
        this.packetAdapter = new PacketAdapter();
        this.settings = new LinkedList<>();
        this.connectListeners = new CopyOnWriteArrayList<>();
        this.preDisconnectListeners = new CopyOnWriteArrayList<>();
        this.postDisconnectListeners = new CopyOnWriteArrayList<>();
    }

    public LightningBase<T> option(LightningSettings settings) {
        this.settings.add(settings);
        return this;
    }

    public abstract LightningBase<?> build(String host, int port, int threads);
    public abstract LightningBase<?> build(String host, int port);

    public abstract LightningBase<T> queue(Packet packet);

    public LightningBase<T> registerHandler(PacketHandler packetHandler) {
        this.packetAdapter.registerHandler(packetHandler);
        return this;
    }

    public LightningBase<T> unregisterAdapter(PacketHandler packetHandler) {
        this.packetAdapter.unregisterHandler(packetHandler);
        return this;
    }

    public LightningBase<T> onConnect(T listener) {
        connectListeners.add(listener);
        return this;
    }

    public abstract void flush();
}
