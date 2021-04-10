package org.gravel.elements.gui.main.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.elements.gui.screen.MainGUi;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.chatting.ChatMessage;
import org.gravel.library.manager.networking.packets.in.PacketInSendMessage;
import org.gravel.library.manager.networking.packets.out.PacketTyping;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

@Getter @AllArgsConstructor
public class GuiKeyListener extends KeyAdapter {

    private final MainGUi gui;
    
    @Override
    public void keyPressed(KeyEvent e) {
        GravelAPI.getInstance().sendPacket(new PacketTyping(gui.getGravelClient().getUser(), GravelAPI.getInstance().getCurrentChat()));
        if (e.getKeyCode() == 10) {
            String cmd = this.gui.getMessageInput().getText();
            if (cmd.trim().isEmpty()) {
                return;
            }
            if (!this.gui.getMessageInput().getText().equals("")) {
                this.gui.getHistory().add(cmd);
                this.gui.setCurrenthistory(this.gui.getHistory().size());
            }

            Chat chat = GravelAPI.getInstance().getCurrentChat();
            if (chat == null) {
                return;
            }
            final ChatMessage chatMessage = new ChatMessage(
                    cmd,
                    this.gui.getGravelClient().getUser(),
                    new Date().getTime()
            );

            GravelAPI.getInstance().sendPacket(new PacketInSendMessage(chat.getUniqueId(), chatMessage));
            chat.getMessages().add(chatMessage);

            GravelAPI.getInstance().updateChat(chat, MainGUi.playerLabel, MainGUi.textField3, this.gui.getGravelClient().getUser());

            this.gui.getMessageInput().setText("");
        } else if (e.getKeyCode() == 38) {
            this.gui.setCurrenthistory(this.gui.getCurrenthistory() - 1);
            if (this.gui.getCurrenthistory() >= 0) {
                this.gui.getMessageInput().setText(this.gui.getHistory().get(this.gui.getCurrenthistory()));
            } else {
                this.gui.setCurrenthistory(this.gui.getCurrenthistory() + 1);
            }
        } else if (e.getKeyCode() == 40) {
            this.gui.setCurrenthistory(this.gui.getCurrenthistory() + 1);
            if (this.gui.getCurrenthistory() < this.gui.getHistory().size()) {
                this.gui.getMessageInput().setText(this.gui.getHistory().get(this.gui.getCurrenthistory()));
            } else {
                this.gui.setCurrenthistory(this.gui.getCurrenthistory() - 1);
            }
        }
    }
}
