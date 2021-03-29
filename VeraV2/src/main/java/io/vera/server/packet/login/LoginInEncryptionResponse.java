package io.vera.server.packet.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.ui.tablist.PlayerProperty;
import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class LoginInEncryptionResponse extends PacketIn {

    public LoginInEncryptionResponse() {
        super(LoginInEncryptionResponse.class);
    }

    public void read(ByteBuf buf, NetClient client) {
        MessageDigest md;
        int secretLen = NetData.rvint(buf);
        byte[] encryptedSecret = NetData.arr(buf, secretLen);
        int tokenLen = NetData.rvint(buf);
        byte[] encryptedToken = NetData.arr(buf, tokenLen);
        byte[] sharedSecret;
        if ((sharedSecret = client.getCryptoModule().begin(encryptedSecret, encryptedToken)) == null) {
            client.disconnect("Crypto error");
            return;
        }
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(sharedSecret);
        md.update(client.getCryptoModule().kp().getPublic().getEncoded());
        String hash = toHexStringTwosComplement(md.digest());
        Mojang.req("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s", client.getName(), hash).callback(resp -> {
            if (resp == null) {
                client.disconnect("Auth error");
                return;
            }
            VsonObject obj = resp.asVsonObject();
            String id = obj.get("id").asString();
            String name = obj.get("name").asString();
            VsonObject tex = obj.get("properties").asArray().get(0).asVsonObject();
            VsonValue signature = tex.get("signature");
            PlayerProperty textures = new PlayerProperty(tex.get("name").asString(), tex.get("value").asString(), (signature != null) ? signature.asString() : null);
            UUID uuid = Login.convert(name, id);
            LoginOutSuccess success = new LoginOutSuccess(client, uuid, name);
            client.sendPacket(success).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                }
            });
        });
    }

    private static String toHexStringTwosComplement(byte[] bys) {
        boolean negative = ((bys[0] & 0x80) != 0);
        if (negative) {
            boolean carry = true;
            for (int i = bys.length - 1; i >= 0; i--) {
                bys[i] = (byte)((bys[i] ^ 0xFFFFFFFF) & 0xFF);
                if (carry) {
                    carry = ((bys[i] & 0xFF) == 255);
                    bys[i] = (byte)(bys[i] + 1);
                }
            }
        }
        StringBuilder sb = new StringBuilder(bys.length * 2);
        if (negative)
            sb.append("-");
        boolean skipZeroes = true;
        for (byte by : bys) {
            if (by == 0) {
                if (!skipZeroes)
                    sb.append("00");
            } else if ((by & 0xF0) == 0) {
                if (!skipZeroes)
                    sb.append("0");
                sb.append("0123456789abcdef".charAt(by & 0xF));
                skipZeroes = false;
            } else {
                sb.append("0123456789abcdef".charAt((by & 0xF0) >> 4));
                sb.append("0123456789abcdef".charAt(by & 0xF));
                skipZeroes = false;
            }
        }
        return sb.toString();
    }
}
