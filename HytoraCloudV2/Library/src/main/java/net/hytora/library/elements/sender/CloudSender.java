package net.hytora.library.elements.sender;

import net.hytora.library.elements.base.CloudObject;

public interface CloudSender<T> extends CloudObject<T> {

    void sendMessage(String message);

    boolean hasPermission(String permission);
}
