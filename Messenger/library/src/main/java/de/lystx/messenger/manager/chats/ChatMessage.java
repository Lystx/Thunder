package de.lystx.messenger.manager.chats;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.account.Account;
import de.lystx.messenger.manager.console.Console;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter @AllArgsConstructor
public class ChatMessage implements Serializable {

    private final int chatID;
    private final String sender;
    private final String content;
    private final long date;


    public void display(Console console) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy hh:mm:ss");
        console.sendMessage(this.sender, this.content + " §8[§e" + dateFormat.format(new Date(this.date)) + "§8]");
    }

    public Account getSender() {
        return MessageAPI.getInstance().getAccount(sender);
    }
}
