
package io.vera.meta.entity.vehicle;

import io.vera.ui.chat.ChatComponent;



public interface CommandMinecartMeta extends MinecartMeta {

    String getCommand();

    void setCommand(String command);

    ChatComponent getLastOutput();

    void setLastOutput(ChatComponent component);

}
