package io.lightning.listener;

import io.lightning.Lightning;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LightningMainListener extends ListenerAdapter {


    @Override
    public void onReady(@NotNull ReadyEvent event) {

        StringBuilder stringBuilder = new StringBuilder();
        for (Guild guild : Lightning.get().getJda().getGuilds()) {
            stringBuilder.append(guild.getName()).append(", ");
        }

        System.out.println("\n" +
                "  _      _       _     _         _             ____        _   \n" +
                " | |    (_)     | |   | |       (_)           |  _ \\      | |  \n" +
                " | |     _  __ _| |__ | |_ _ __  _ _ __   __ _| |_) | ___ | |_ \n" +
                " | |    | |/ _` | '_ \\| __| '_ \\| | '_ \\ / _` |  _ < / _ \\| __|\n" +
                " | |____| | (_| | | | | |_| | | | | | | | (_| | |_) | (_) | |_ \n" +
                " |______|_|\\__, |_| |_|\\__|_| |_|_|_| |_|\\__, |____/ \\___/ \\__|\n" +
                "            __/ |                         __/ |                \n" +
                "           |___/                         |___/                 ");
        System.out.println("------------------------------------------");
        System.out.println("[INFO] Developer : Lystx");
        System.out.println("[INFO] Version : V1");
        System.out.println("[INFO] Active Guilds : " + stringBuilder.toString() + "?");
        System.out.println("[INFO] System Time : " + new SimpleDateFormat("dd.MM.yyyy - hh:mm:ss").format(new Date()));

    }
}
