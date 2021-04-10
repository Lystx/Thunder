package org.gravel.library.manager.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.manager.user.GravelUser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter @AllArgsConstructor
public class ChatMessage implements Serializable {

    private final String content;
    private final GravelUser sender;
    private final long date;


    public String format() {
        return "[" + new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(new Date(this.date)) + "] " + sender.getAccount().getName() + " > " + content;
    }
}
