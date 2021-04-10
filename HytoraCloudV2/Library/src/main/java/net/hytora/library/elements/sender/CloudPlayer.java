package net.hytora.library.elements.sender;


import net.hytora.library.elements.permission.PermissionGroup;
import net.hytora.library.elements.services.Service;

public interface CloudPlayer extends CloudSender<CloudPlayer> {

    Service getService();

    Service getProxy();

    PermissionGroup getPermissionGroup();
}
