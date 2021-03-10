
package io.vera.server.packet.login;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetClient;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketIn;
import io.vera.server.player.VeraPlayer;
import io.vera.server.ui.tablist.PlayerProperty;
import io.vson.elements.object.VsonObject;

import io.vson.VsonValue;

import javax.annotation.concurrent.Immutable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static io.vera.server.net.NetData.arr;


@Immutable
public final class LoginInEncryptionResponse extends PacketIn {

    private static final String HEX = "0123456789abcdef";
    private static final String MOJANG_SERVER = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";

    public LoginInEncryptionResponse() {
        super(LoginInEncryptionResponse.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int secretLen = NetData.rvint(buf);
        byte[] encryptedSecret = NetData.arr(buf, secretLen);
        int tokenLen = NetData.rvint(buf);
        byte[] encryptedToken = NetData.arr(buf, tokenLen);

        byte[] sharedSecret;
        if ((sharedSecret = client.getCryptoModule().begin(encryptedSecret, encryptedToken)) == null) {
            client.disconnect("Crypto error");
            return;
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        md.update(sharedSecret);
        md.update(client.getCryptoModule().kp().getPublic().getEncoded());

        String hash = toHexStringTwosComplement(md.digest());
        Mojang.req(MOJANG_SERVER, client.getName(), hash).callback(resp -> {
            if (resp == null) {
                client.disconnect("Auth error");
                return;
            }

            VsonObject obj = resp.asVsonObject();
            String id = obj.get("id").asString();
            String name = obj.get("name").asString();
            VsonObject tex = obj.get("properties").asArray().get(0).asVsonObject();

            VsonValue signature = tex.get("signature");
            PlayerProperty textures = new PlayerProperty(tex.get("name").asString(),
                    tex.get("value").asString(), signature != null ? signature.asString() : null);

            UUID uuid = Login.convert(name, id);
            LoginOutSuccess success = new LoginOutSuccess(client, uuid, name);
            client.sendPacket(success).addListener(future -> VeraPlayer.spawn(client, name, uuid, textures));
        }).get();
    }

    private static String toHexStringTwosComplement(byte[] bys) {
        boolean negative = (bys[0] & 0x80) != 0;
        if (negative) {
            boolean carry = true;
            for (int i = bys.length - 1; i >= 0; i--) {
                bys[i] = (byte) (~bys[i] & 0xFF);
                if (carry) {
                    carry = (bys[i] & 0xFF) == 0xFF;
                    bys[i]++;
                }
            }
        }
        StringBuilder sb = new StringBuilder(bys.length * 2);
        if (negative) {
            sb.append("-");
        }
        boolean skipZeroes = true;
        for (byte by : bys) {
            if (by == 0) {
                if (!skipZeroes) {
                    sb.append("00");
                }
            } else if ((by & 0xF0) == 0) {
                if (!skipZeroes) {
                    sb.append("0");
                }
                sb.append(HEX.charAt(by & 0x0F));
                skipZeroes = false;
            } else {
                sb.append(HEX.charAt((by & 0xF0) >> 4));
                sb.append(HEX.charAt(by & 0x0F));
                skipZeroes = false;
            }
        }
        return sb.toString();
    }
}
