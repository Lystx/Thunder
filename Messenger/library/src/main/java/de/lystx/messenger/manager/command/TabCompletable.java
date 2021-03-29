package de.lystx.messenger.manager.command;


import de.lystx.messenger.MessageAPI;

import java.util.List;

public interface TabCompletable {

    List<String> onTabComplete(MessageAPI messageAPI, String[] args);

}