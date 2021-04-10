package org.gravel.library.vson;

import io.vson.VsonValue;
import io.vson.annotation.other.VsonAdapter;
import io.vson.elements.object.VsonObject;
import io.vson.manage.vson.VsonWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.library.GravelAPI;
import org.gravel.library.manager.account.Account;
import org.gravel.library.manager.chatting.Chat;
import org.gravel.library.manager.chatting.ChatMessage;
import org.gravel.library.manager.user.GravelUser;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor
public class GravelVsonAdapterChat implements VsonAdapter<Chat> {


    @Override
    public VsonValue write(Chat chat, VsonWriter vsonWriter) {

        VsonObject vsonObject = new VsonObject();
        vsonObject.append("uniqueId", chat.getUniqueId());
        vsonObject.append("name", chat.getName());
        List<UUID> uuids = new LinkedList<>();
        for (Account user : chat.getUsers()) {
            uuids.add(user.getUniqueId());
        }
        vsonObject.append("users", uuids);
        vsonObject.append("messages", chat.getMessages());
        return vsonObject;
    }

    @Override
    public Chat read(VsonValue vsonValue) {
        VsonObject vsonObject = (VsonObject) vsonValue;
        List<Account> users = new LinkedList<>();
        for (UUID uuid : vsonObject.getList("users", UUID.class)) {
            users.add(GravelAPI.getInstance().getAccountManager().getAccount(uuid));
        }

        return new Chat(
                UUID.fromString(vsonObject.getString("uniqueId")),
                vsonObject.getString("name"),
                users,
                vsonObject.getList("messages", ChatMessage.class)
        );
    }

    @Override
    public Class<Chat> getTypeClass() {
        return Chat.class;
    }
}
