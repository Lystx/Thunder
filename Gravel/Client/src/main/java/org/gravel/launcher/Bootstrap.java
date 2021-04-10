package org.gravel.launcher;

import org.gravel.GravelClient;

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
            new GravelClient(host, port);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("[Client] Some of the arguments was null or not found!");
        }
    }
}
