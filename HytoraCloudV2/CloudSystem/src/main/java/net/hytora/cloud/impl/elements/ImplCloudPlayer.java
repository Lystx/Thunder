package net.hytora.cloud.impl.elements;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hytora.library.CloudAPI;
import net.hytora.library.elements.permission.PermissionGroup;
import net.hytora.library.elements.sender.CloudPlayer;
import net.hytora.library.elements.services.Service;
import net.hytora.library.manager.networking.packets.out.PacketOutSendMessage;

import java.util.UUID;

@Getter @RequiredArgsConstructor
@Setter
public class ImplCloudPlayer implements CloudPlayer {

    private final String name;
    private final UUID uniqueId;

    private Service proxy;
    private Service service;

    @Override
    public void sendMessage(String message) {
        CloudAPI.getInstance().getNettyConnection().sendPacket(new PacketOutSendMessage(this, message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public CloudPlayer get() {
        return this;
    }

    @Override
    public PermissionGroup getPermissionGroup() {
        return null;
    }
}
