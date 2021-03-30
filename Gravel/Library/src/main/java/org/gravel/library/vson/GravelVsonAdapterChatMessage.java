package org.gravel.library.vson;

import io.vson.VsonValue;
import io.vson.annotation.other.VsonAdapter;
import io.vson.elements.object.VsonObject;
import io.vson.manage.vson.VsonWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.chatting.ChatMessage;

import java.util.UUID;

@Getter @AllArgsConstructor
public class GravelVsonAdapterChatMessage implements VsonAdapter<ChatMessage> {


    private final GravelAPI gravelAPI;

    @Override
    public VsonValue write(ChatMessage chatMessage, VsonWriter vsonWriter) {
        VsonObject vsonObject = new VsonObject();
        vsonObject.append("sender", chatMessage.getSender().getAccount().getUniqueId());
        vsonObject.append("content", chatMessage.getContent());
        vsonObject.append("date", chatMessage.getDate());
        return vsonObject;
    }

    @Override
    public ChatMessage read(VsonValue vsonValue) {
        VsonObject vsonObject = (VsonObject) vsonValue;
        return new ChatMessage(
                vsonObject.getString("content"),
                this.gravelAPI.getUserManager().getUser(UUID.fromString(vsonObject.getString("sender"))),
                vsonObject.getLong("date")
        );
    }

    @Override
    public Class<ChatMessage> getTypeClass() {
        return ChatMessage.class;
    }
}
