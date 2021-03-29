package io.vera.ui.chat;

import io.vera.inventory.Item;
import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import javax.annotation.concurrent.Immutable;

@Immutable
public class HoverEvent {
    private final HoverAction action;

    private final VsonValue value;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof HoverEvent))
            return false;
        HoverEvent other = (HoverEvent)o;
        if (!other.canEqual(this))
            return false;
        Object this$action = getAction(), other$action = other.getAction();
        if ((this$action == null) ? (other$action != null) : !this$action.equals(other$action))
            return false;
        Object this$value = getValue(), other$value = other.getValue();
        return !((this$value == null) ? (other$value != null) : !this$value.equals(other$value));
    }

    protected boolean canEqual(Object other) {
        return other instanceof HoverEvent;
    }

    public String toString() {
        return "HoverEvent(action=" + getAction() + ", value=" + getValue() + ")";
    }

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
        return (VsonValue)obj;
    }
}
