package de.lystx.discordbot.bootstrap;

import de.lystx.discordbot.Discord;

public class Bootstrap {

    public static void main(String[] args) {
        System.out.println("\n" +
                "  _____    _                                   _ \n" +
                " |  __ \\  (_)                                 | |\n" +
                " | |  | |  _   ___    ___    ___    _ __    __| |\n" +
                " | |  | | | | / __|  / __|  / _ \\  | '__|  / _` |\n" +
                " | |__| | | | \\__ \\ | (__  | (_) | | |    | (_| |\n" +
                " |_____/  |_| |___/  \\___|  \\___/  |_|     \\__,_|\n" +
                "                                                 \n" +
                "                                                 ");
        System.out.println("--------------------------------------");
        Discord discord = new Discord();
        discord.bootstrap();
    }
}
