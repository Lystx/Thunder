
package io.vera.server.packet;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import io.vera.server.net.NetClient;
import io.vera.server.packet.login.*;
import io.vera.server.packet.play.*;
import io.vera.server.util.Reference2IntOpenHashMap;
import io.vera.util.Int2ReferenceOpenHashMap;
import io.vera.server.packet.handshake.HandshakeIn;
import io.vera.server.packet.handshake.LegacyHandshakeIn;
import io.vera.server.packet.status.StatusInPing;
import io.vera.server.packet.status.StatusInRequest;
import io.vera.server.packet.status.StatusOutPong;
import io.vera.server.packet.status.StatusOutResponse;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;


@Immutable
public final class PacketRegistry {
    private static final Map<Class<? extends Packet>, ConstructorAccess<? extends Packet>> CTORS = new HashMap<>();
    private static final Reference2IntOpenHashMap<Class<? extends Packet>> PACKET_IDS = new Reference2IntOpenHashMap<>();
    private static final Int2ReferenceOpenHashMap<Class<? extends Packet>> PACKETS = new Int2ReferenceOpenHashMap<>();

    static {
        registerPacket(HandshakeIn.class, NetClient.NetState.HANDSHAKE, Packet.Bound.SERVER, 0x00);
        registerPacket(LegacyHandshakeIn.class, NetClient.NetState.HANDSHAKE, Packet.Bound.SERVER, 0xFE);

        registerPacket(StatusInRequest.class, NetClient.NetState.STATUS, Packet.Bound.SERVER, 0x00);
        registerPacket(StatusOutResponse.class, NetClient.NetState.STATUS, Packet.Bound.CLIENT, 0x00);
        registerPacket(StatusInPing.class, NetClient.NetState.STATUS, Packet.Bound.SERVER, 0x01);
        registerPacket(StatusOutPong.class, NetClient.NetState.STATUS, Packet.Bound.CLIENT, 0x01);

        registerPacket(LoginInStart.class, NetClient.NetState.LOGIN, Packet.Bound.SERVER, 0x00);
        registerPacket(LoginOutDisconnect.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0x00);
        registerPacket(LoginOutEncryptionRequest.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0x01);
        registerPacket(LoginInEncryptionResponse.class, NetClient.NetState.LOGIN, Packet.Bound.SERVER, 0x01);
        registerPacket(LoginOutSuccess.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0x02);
        registerPacket(LoginOutCompression.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0x03);

        registerPacket(PlayOutLightning.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x02);
        registerPacket(PlayOutSpawnPlayer.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x05);
        registerPacket(PlayOutAnimation.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x06);
        registerPacket(PlayOutBlockChange.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x0B);
        registerPacket(PlayOutBossBar.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x0C);
        registerPacket(PlayOutDifficulty.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x0D);
        registerPacket(PlayOutChat.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x0F);
        registerPacket(PlayOutWindowItems.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x14);
        registerPacket(PlayOutSlot.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x16);
        registerPacket(PlayOutPluginMsg.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x18);
        registerPacket(PlayOutDisconnect.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x1A);
        registerPacket(PlayOutUnloadChunk.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x1D);
        registerPacket(PlayOutGameState.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x1E);
        registerPacket(PlayOutKeepAlive.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x1F);
        registerPacket(PlayOutChunk.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x20);
        registerPacket(PlayOutJoinGame.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x23);
        registerPacket(PlayOutEntityRelativeMove.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x26);
        registerPacket(PlayOutEntityLookAndRelativeMove.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x27);
        registerPacket(PlayOutEntityLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x28);
        registerPacket(PlayOutPlayerAbilities.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x2C);
        registerPacket(PlayOutTabListItem.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x2E);
        registerPacket(PlayOutPosLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x2F);
        registerPacket(PlayOutDestroyEntities.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x32);
        registerPacket(PlayOutEntityHeadLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x36);
        registerPacket(PlayOutWorldBorder.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x38);
        registerPacket(PlayOutEntityMetadata.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x3C);
        registerPacket(PlayOutEquipment.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x3F);
        registerPacket(PlayOutSpawnPos.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x46);
        registerPacket(PlayOutTime.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x47);
        registerPacket(PlayOutTitle.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x48);
        registerPacket(PlayOutPlayerListHeaderAndFooter.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x4A);
        registerPacket(PlayOutTeleport.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x4C);

        registerPacket(PlayInTeleportConfirm.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x00);
        registerPacket(PlayInChat.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x02);
        registerPacket(PlayInClientStatus.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x03);
        registerPacket(PlayInClientSettings.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x04);
        registerPacket(PlayInCloseWindow.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x08);
        registerPacket(PlayInPluginMsg.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x09);
        registerPacket(PlayInUseEntity.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0A);
        registerPacket(PlayInKeepAlive.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0B);
        registerPacket(PlayInPlayer.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0C);
        registerPacket(PlayInPos.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0D);
        registerPacket(PlayInPosLook.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0E);
        registerPacket(PlayInLook.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0F);
        registerPacket(PlayInPlayerAbilities.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x13);
        registerPacket(PlayInPlayerDig.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x14);
        registerPacket(PlayInEntityAction.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x15);
        registerPacket(PlayInSetSlot.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x1A);
        registerPacket(PlayInCreativeInventoryAction.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x1B);
        registerPacket(PlayInAnimation.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x1D);
        registerPacket(PlayInBlockPlace.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x1F);
        registerPacket(PlayInUseItem.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x20);

        PACKETS.trim();
        PACKET_IDS.trim();
    }

    private static int shift(NetClient.NetState state, Packet.Bound bound, int id) {
        int identifier = id;
        identifier |= state.ordinal() << 27;
        identifier |= bound.ordinal() << 31;
        return identifier;
    }

    private static void registerPacket(Class<? extends Packet> cls, NetClient.NetState state, Packet.Bound bound, int id) {
        int identifier = shift(state, bound, id);
        PACKET_IDS.put(cls, identifier);

        if (bound == Packet.Bound.SERVER) {
            PACKETS.put(identifier, cls);
            CTORS.put(cls, ConstructorAccess.get(cls));
        }
    }

    private PacketRegistry() {
    }

    public static <T extends Packet> T make(Class<? extends Packet> cls) {
        return (T) CTORS.get(cls).newInstance();
    }

    @Nullable
    public static Class<? extends Packet> byId(NetClient.NetState state, Packet.Bound bound, int id) {
        return PACKETS.get(shift(state, bound, id));
    }

    public static int packetInfo(Class<? extends Packet> cls) {
        int identifier = PACKET_IDS.getInt(cls);
        if (identifier != -1) {
            return identifier;
        }

        throw new IllegalArgumentException(cls.getSimpleName() + " is not registered");
    }

    public static int idOf(int info) {
        return info & 0x7ffffff;
    }

    public static NetClient.NetState stateOf(int info) {
        int ordinal = info >> 27 & 0xf;
        return NetClient.NetState.values()[ordinal];
    }

    public static Packet.Bound boundOf(int info) {
        int ordinal = info >> 31 & 0x1;
        return Packet.Bound.values()[ordinal];
    }
}
