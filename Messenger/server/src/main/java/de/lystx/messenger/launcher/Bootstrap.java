package de.lystx.messenger.launcher;

import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.Server;

public class Bootstrap {

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("host=")) {
                String host = arg.split("host=")[1];
                MessageAPI.getInstance().setHost(host.trim());
            }
        }
        new Server();
    }
}
