package io.vera.ui.chat;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum ClickAction {
    OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND;
}
