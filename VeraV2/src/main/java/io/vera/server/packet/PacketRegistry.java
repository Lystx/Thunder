package io.vera.server.packet;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import io.vera.server.net.NetClient;
import io.vera.server.packet.handshake.HandshakeIn;
import io.vera.server.packet.handshake.LegacyHandshakeIn;
import io.vera.server.packet.login.LoginInEncryptionResponse;
import io.vera.server.packet.login.LoginInStart;
import io.vera.server.packet.login.LoginOutCompression;
import io.vera.server.packet.login.LoginOutDisconnect;
import io.vera.server.packet.login.LoginOutEncryptionRequest;
import io.vera.server.packet.login.LoginOutSuccess;
import io.vera.server.packet.play.PlayInAnimation;
import io.vera.server.packet.play.PlayInBlockPlace;
import io.vera.server.packet.play.PlayInChat;
import io.vera.server.packet.play.PlayInClientSettings;
import io.vera.server.packet.play.PlayInClientStatus;
import io.vera.server.packet.play.PlayInCloseWindow;
import io.vera.server.packet.play.PlayInCreativeInventoryAction;
import io.vera.server.packet.play.PlayInEntityAction;
import io.vera.server.packet.play.PlayInKeepAlive;
import io.vera.server.packet.play.PlayInLook;
import io.vera.server.packet.play.PlayInPlayer;
import io.vera.server.packet.play.PlayInPlayerAbilities;
import io.vera.server.packet.play.PlayInPlayerDig;
import io.vera.server.packet.play.PlayInPluginMsg;
import io.vera.server.packet.play.PlayInPos;
import io.vera.server.packet.play.PlayInPosLook;
import io.vera.server.packet.play.PlayInSetSlot;
import io.vera.server.packet.play.PlayInTeleportConfirm;
import io.vera.server.packet.play.PlayInUseEntity;
import io.vera.server.packet.play.PlayInUseItem;
import io.vera.server.packet.play.PlayOutAnimation;
import io.vera.server.packet.play.PlayOutBlockChange;
import io.vera.server.packet.play.PlayOutBossBar;
import io.vera.server.packet.play.PlayOutChat;
import io.vera.server.packet.play.PlayOutChunk;
import io.vera.server.packet.play.PlayOutDestroyEntities;
import io.vera.server.packet.play.PlayOutDifficulty;
import io.vera.server.packet.play.PlayOutDisconnect;
import io.vera.server.packet.play.PlayOutEntityHeadLook;
import io.vera.server.packet.play.PlayOutEntityLook;
import io.vera.server.packet.play.PlayOutEntityLookAndRelativeMove;
import io.vera.server.packet.play.PlayOutEntityMetadata;
import io.vera.server.packet.play.PlayOutEntityRelativeMove;
import io.vera.server.packet.play.PlayOutEquipment;
import io.vera.server.packet.play.PlayOutGameState;
import io.vera.server.packet.play.PlayOutJoinGame;
import io.vera.server.packet.play.PlayOutKeepAlive;
import io.vera.server.packet.play.PlayOutLightning;
import io.vera.server.packet.play.PlayOutPlayerAbilities;
import io.vera.server.packet.play.PlayOutPlayerListHeaderAndFooter;
import io.vera.server.packet.play.PlayOutPluginMsg;
import io.vera.server.packet.play.PlayOutPosLook;
import io.vera.server.packet.play.PlayOutSlot;
import io.vera.server.packet.play.PlayOutSpawnPlayer;
import io.vera.server.packet.play.PlayOutSpawnPos;
import io.vera.server.packet.play.PlayOutTabListItem;
import io.vera.server.packet.play.PlayOutTeleport;
import io.vera.server.packet.play.PlayOutTime;
import io.vera.server.packet.play.PlayOutTitle;
import io.vera.server.packet.play.PlayOutUnloadChunk;
import io.vera.server.packet.play.PlayOutWindowItems;
import io.vera.server.packet.play.PlayOutWorldBorder;
import io.vera.server.packet.status.StatusInPing;
import io.vera.server.packet.status.StatusInRequest;
import io.vera.server.packet.status.StatusOutPong;
import io.vera.server.packet.status.StatusOutResponse;
import io.vera.server.util.Reference2IntOpenHashMap;
import io.vera.util.Int2ReferenceOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PacketRegistry {

    private static final Map<Class<? extends Packet>, ConstructorAccess<? extends Packet>> CTORS = new HashMap<>();
    private static final Reference2IntOpenHashMap<Class<? extends Packet>> PACKET_IDS = new Reference2IntOpenHashMap<>();
    private static final Int2ReferenceOpenHashMap<Class<? extends Packet>> PACKETS = new Int2ReferenceOpenHashMap<>();

    static {
        registerPacket(HandshakeIn.class, NetClient.NetState.HANDSHAKE, Packet.Bound.SERVER, 0);
        registerPacket(LegacyHandshakeIn.class, NetClient.NetState.HANDSHAKE, Packet.Bound.SERVER, 254);
        registerPacket(StatusInRequest.class, NetClient.NetState.STATUS, Packet.Bound.SERVER, 0);
        registerPacket(StatusOutResponse.class, NetClient.NetState.STATUS, Packet.Bound.CLIENT, 0);
        registerPacket(StatusInPing.class, NetClient.NetState.STATUS, Packet.Bound.SERVER, 1);
        registerPacket(StatusOutPong.class, NetClient.NetState.STATUS, Packet.Bound.CLIENT, 1);
        registerPacket(LoginInStart.class, NetClient.NetState.LOGIN, Packet.Bound.SERVER, 0);
        registerPacket(LoginOutDisconnect.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0);
        registerPacket(LoginOutEncryptionRequest.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 1);
        registerPacket(LoginInEncryptionResponse.class, NetClient.NetState.LOGIN, Packet.Bound.SERVER, 1);
        registerPacket(LoginOutSuccess.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 2);
        registerPacket(LoginOutCompression.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 3);
        registerPacket(PlayOutLightning.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 2);
        registerPacket(PlayOutSpawnPlayer.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 5);
        registerPacket(PlayOutAnimation.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 6);
        registerPacket(PlayOutBlockChange.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 11);
        registerPacket(PlayOutBossBar.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 12);
        registerPacket(PlayOutDifficulty.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 13);
        registerPacket(PlayOutChat.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 15);
        registerPacket(PlayOutWindowItems.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 20);
        registerPacket(PlayOutSlot.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 22);
        registerPacket(PlayOutPluginMsg.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 24);
        registerPacket(PlayOutDisconnect.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 26);
        registerPacket(PlayOutUnloadChunk.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 29);
        registerPacket(PlayOutGameState.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 30);
        registerPacket(PlayOutKeepAlive.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 31);
        registerPacket(PlayOutChunk.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 32);
        registerPacket(PlayOutJoinGame.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 35);
        registerPacket(PlayOutEntityRelativeMove.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 38);
        registerPacket(PlayOutEntityLookAndRelativeMove.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 39);
        registerPacket(PlayOutEntityLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 40);
        registerPacket(PlayOutPlayerAbilities.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 44);
        registerPacket(PlayOutTabListItem.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 46);
        registerPacket(PlayOutPosLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 47);
        registerPacket(PlayOutDestroyEntities.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 50);
        registerPacket(PlayOutEntityHeadLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 54);
        registerPacket(PlayOutWorldBorder.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 56);
        registerPacket(PlayOutEntityMetadata.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 60);
        registerPacket(PlayOutEquipment.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 63);
        registerPacket(PlayOutSpawnPos.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 70);
        registerPacket(PlayOutTime.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 71);
        registerPacket(PlayOutTitle.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 72);
        registerPacket(PlayOutPlayerListHeaderAndFooter.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 74);
        registerPacket(PlayOutTeleport.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 76);
        registerPacket(PlayInTeleportConfirm.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0);
        registerPacket(PlayInChat.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 2);
        registerPacket(PlayInClientStatus.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 3);
        registerPacket(PlayInClientSettings.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 4);
        registerPacket(PlayInCloseWindow.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 8);
        registerPacket(PlayInPluginMsg.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 9);
        registerPacket(PlayInUseEntity.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 10);
        registerPacket(PlayInKeepAlive.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 11);
        registerPacket(PlayInPlayer.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 12);
        registerPacket(PlayInPos.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 13);
        registerPacket(PlayInPosLook.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 14);
        registerPacket(PlayInLook.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 15);
        registerPacket(PlayInPlayerAbilities.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 19);
        registerPacket(PlayInPlayerDig.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 20);
        registerPacket(PlayInEntityAction.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 21);
        registerPacket(PlayInSetSlot.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 26);
        registerPacket(PlayInCreativeInventoryAction.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 27);
        registerPacket(PlayInAnimation.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 29);
        registerPacket(PlayInBlockPlace.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 31);
        registerPacket(PlayInUseItem.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 32);
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

    public static <T extends Packet> T make(Class<? extends Packet> cls) {
        return (T)((ConstructorAccess)CTORS.get(cls)).newInstance();
    }

    @Nullable
    public static Class<? extends Packet> byId(NetClient.NetState state, Packet.Bound bound, int id) {
        return PACKETS.get(shift(state, bound, id));
    }

    public static int packetInfo(Class<? extends Packet> cls) {
        int identifier = PACKET_IDS.getInt(cls);
        if (identifier != -1)
            return identifier;
        throw new IllegalArgumentException(cls.getSimpleName() + " is not registered");
    }

    public static int idOf(int info) {
        return info & 0x7FFFFFF;
    }

    public static NetClient.NetState stateOf(int info) {
        int ordinal = info >> 27 & 0xF;
        return NetClient.NetState.values()[ordinal];
    }

    public static Packet.Bound boundOf(int info) {
        int ordinal = info >> 31 & 0x1;
        return Packet.Bound.values()[ordinal];
    }
}
