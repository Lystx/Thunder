
package io.vera.ui.chat;

import io.vson.elements.object.VsonObject;
import lombok.Data;

import javax.annotation.concurrent.Immutable;

@Data
@Immutable
public class ClickEvent {

    private final ClickAction action;
    private final String value;

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
