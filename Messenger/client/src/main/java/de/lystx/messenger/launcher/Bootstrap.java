package de.lystx.messenger.launcher;

import de.lystx.messenger.Client;
import de.lystx.messenger.MessageAPI;

public class Bootstrap {

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("host=")) {
                String host = arg.split("host=")[1];
                MessageAPI.getInstance().setHost(host.trim());
            }
        }
        new Client();
    }
}
