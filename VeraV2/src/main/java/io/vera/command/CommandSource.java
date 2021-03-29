
package io.vera.command;

import io.vera.entity.permission.Permissible;
import io.vera.ui.chat.ChatComponent;


public interface CommandSource extends Permissible {

    void runCommand(String command);

    void sendMessage(ChatComponent text);

    CommandSourceType getCmdType();

}
