
package io.vera.ui.chat;

import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import lombok.Data;
import io.vera.inventory.Item;

import javax.annotation.concurrent.Immutable;

@Data
@Immutable
public class HoverEvent {

    private final HoverAction action;
    private final VsonValue value;

    private HoverEvent(HoverAction action, VsonValue value) {
        this.action = action;
        this.value = value;
    }

    public static HoverEvent text(String text) {
        return text(ChatComponent.text(text));
    }

    public static HoverEvent text(ChatComponent chat) {
        return new HoverEvent(HoverAction.SHOW_TEXT, chat.asJson());
    }

    public static HoverEvent achievement(String achievement) {
        return new HoverEvent(HoverAction.SHOW_ACHIEVEMENT, VsonValue.valueOf(achievement));
    }

    public static HoverEvent item(Item item) {
        VsonObject json = new VsonObject();
        json.append("id", item.getSubstance().toString().replaceAll("minecraft:", ""));
        json.append("Damage", item.getDamage());
        json.append("Count", item.getCount());
        json.append("tag", "{}");

        String string = json.toString().replaceAll("\"", "");
        return new HoverEvent(HoverAction.SHOW_ITEM, VsonValue.valueOf(string));
    }


    public static HoverEvent fromJson(VsonValue json) {
        return new HoverEvent(HoverAction.valueOf(json.asVsonObject().get("action").asString().toUpperCase()), json.asVsonObject().get("value"));
    }

    public HoverAction getAction() {
        return this.action;
    }


    public VsonValue getValue() {
        return this.value;
    }


    public VsonValue asJson() {
        VsonObject obj = new VsonObject();
        obj.append("action", this.action.name().toLowerCase());
        obj.append("value", this.value);
        return obj;
    }
}
