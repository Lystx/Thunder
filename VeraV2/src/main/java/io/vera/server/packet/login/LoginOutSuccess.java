package io.vera.server.packet.login;

import io.netty.buffer.ByteBuf;
import io.vera.server.VeraServer;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.ui.tablist.PlayerProperty;
import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.concurrent.Immutable;

@Immutable @Getter
public final class LoginOutSuccess extends PacketOut {

    private final NetClient client;
    private final UUID uuid;
    private final String name;
    private final PlayerProperty textures;

    public LoginOutSuccess(NetClient client) {
        super(LoginOutSuccess.class);
        String tempUuid;
        this.client = client;
        this.name = client.getName();
        try {
            tempUuid = Mojang.<String>req("https://api.mojang.com/users/profiles/minecraft/%s", new String[] { this.name }).callback(vsonValue -> {
                vsonValue.asVsonObject().get("id").asString();
            }).onException(s -> null).get().get();
        } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (tempUuid == null) {
            this.uuid = UUID.randomUUID();
            this.textures = null;
        } else {
            this.uuid = Login.convert(this.name, tempUuid);
            try {
                VsonObject tex = Mojang.<VsonObject>req("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", new String[] { tempUuid }).callback((Consumer<VsonValue>)  e -> e.asVsonObject().get("properties").asArray().get(0).asVsonObject() ).onException(s -> {
                    VeraServer.getInstance().getLogger().error("Login cannot be completed due to HTTPS error");
                    return null;
                }).get().get();
                if (tex == null) {
                    this.textures = new PlayerProperty("", "", "");
                } else {
                    VsonValue signature = tex.get("signature");
                    this
                            .textures = new PlayerProperty(tex.get("name").asString(), tex.get("value").asString(), (signature != null) ? signature.asString() : null);
                }
            } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        client.enableCompression();
    }

    public LoginOutSuccess(NetClient client, UUID uuid, String name) {
        super(LoginOutSuccess.class);
        this.client = client;
        this.uuid = uuid;
        this.name = name;
        this.textures = null;
        client.enableCompression();
    }

    public void write(ByteBuf buf) {
        NetData.wstr(buf, this.uuid.toString());
        NetData.wstr(buf, this.name);
        this.client.setState(NetClient.NetState.PLAY);
    }
}
