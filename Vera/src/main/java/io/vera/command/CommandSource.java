
package io.vera.command;

import io.vera.entity.permission.Permissible;
import io.vera.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public interface CommandSource extends Permissible {

    void runCommand(String command);

    void sendMessage(ChatComponent text);

    CommandSourceType getCmdType();

    @Override
    boolean hasPermission(String permission);
}
