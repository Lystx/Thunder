package io.vera.ui.chat;

import io.vson.elements.object.VsonObject;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ClickEvent {
    private final ClickAction action;

    private final String value;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ClickEvent))
            return false;
        ClickEvent other = (ClickEvent)o;
        if (!other.canEqual(this))
            return false;
        Object this$action = getAction(), other$action = other.getAction();
        if ((this$action == null) ? (other$action != null) : !this$action.equals(other$action))
            return false;
        Object this$value = getValue(), other$value = other.getValue();
        return !((this$value == null) ? (other$value != null) : !this$value.equals(other$value));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClickEvent;
    }

    public String toString() {
        return "ClickEvent(action=" + getAction() + ", value=" + getValue() + ")";
    }

    private ClickEvent(ClickAction action, String value) {
        this.action = action;
        this.value = value;
    }

    public static ClickEvent of(ClickAction action, String text) {
        return new ClickEvent(action, text);
    }

    public static ClickEvent fromJson(VsonObject json) {
        return of(ClickAction.valueOf(json.get("action").asString().toUpperCase()), json.get("value").asString());
    }

    public ClickAction getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public VsonObject asJson() {
        VsonObject obj = new VsonObject();
        obj.append("action", this.action.name().toLowerCase());
        obj.append("value", this.value);
        return obj;
    }
}
