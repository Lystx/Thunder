package io.betterbukkit.provider.player;

import io.betterbukkit.elements.Datable;

public interface CommandSender extends Datable {

    void sendMessage(String message);

    boolean hasPermission(String permission);
}
