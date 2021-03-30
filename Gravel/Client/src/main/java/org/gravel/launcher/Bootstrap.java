package org.gravel.launcher;

import org.gravel.Gui;

public class Bootstrap {

    public static void main(String[] args) {

        String host = "localhost";
        String nickname = "YourNickName";
        int port = 1570;

        for (String arg : args) {
            if (arg.startsWith("host=")) {
                host = arg.split("host=")[1];
            }
            if (arg.startsWith("nickname=")) {
                nickname = arg.split("nickname=")[1];
            }
            if (arg.startsWith("port=")) {
                port = Integer.parseInt(arg.split("port=")[1]);
            }
        }
        try {
            Gui.start();
            //new ClientGui(host, port, nickname);
        } catch (NullPointerException e) {
            System.out.println("[Client] Some of the arguments was null or not found!");
        }
    }
}
