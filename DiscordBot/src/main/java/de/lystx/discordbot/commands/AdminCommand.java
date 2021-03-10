package de.lystx.discordbot.commands;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.command.base.Command;
import io.vson.elements.object.VsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Arrays;

public class AdminCommand {

    @Command(name = "admin", description = "Manages Admin command", permission = Permission.ADMINISTRATOR)
    public void execute(DiscordPlayer player, String[] args, TextChannel textChannel, Message message) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                Discord.getInstance().reload();
                textChannel.sendMessage(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Valane | Reload")
                        .setThumbnail("https://findicons.com/files/icons/2443/bunch_of_cool_bluish_icons/512/reload.png")
                        .setDescription("Reloaded DiscordBot!")
                        .setFooter("Executor | " + player.getUser().getAsTag(), player.getUser().getEffectiveAvatarUrl())
                        .build()).queue();
            } else {
                help(player, textChannel);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("ban")) {
                String cat = args[1];
                Discord.getInstance().getConfigManager().getVsonObject().append("banCategory", cat);
                Discord.getInstance().getConfigManager().reload();
                textChannel.sendMessage(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Valane | Category")
                        .setThumbnail("https://www.clipartmax.com/png/full/179-1795386_patient-success-success-icon-png.png")
                        .setDescription("Auftrag erfolgreich!")
                        .setFooter("Executor | " + player.getUser().getAsTag(), player.getUser().getEffectiveAvatarUrl())
                        .build()).queue();

            } else {
                help(player, textChannel);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("addRole")) {
                String name = args[1];
                String id = args[2];
                Discord.getInstance().getConfigManager().getRoles().append(name, id);
                Discord.getInstance().getConfigManager().reload();
                textChannel.sendMessage(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Valane | Adding Role")
                        .setThumbnail("https://www.clipartmax.com/png/full/179-1795386_patient-success-success-icon-png.png")
                        .setDescription("Rolle " + name + " wurde erfolgreich die ID " + id + " zugewiesen!")
                        .setFooter("Executor | " + player.getUser().getAsTag(), player.getUser().getEffectiveAvatarUrl())
                        .build()).queue();
            } else {
                help(player, textChannel);
            }
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("addTicket")) {

            String name = args[1];
            String id = args[2];
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }
            Discord.getInstance().getTicketManager().createTicket(name, id, stringBuilder.toString());
            textChannel.sendMessage(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle("Valane | TicketSystem")
                    .setThumbnail("https://www.clipartmax.com/png/full/179-1795386_patient-success-success-icon-png.png")
                    .setDescription("Ticket " + name + " wurde erfolgreich für Channel mit ID " + id + " erstellt!")
                    .setFooter("Executor | " + player.getUser().getAsTag(), player.getUser().getEffectiveAvatarUrl())
                    .build()).queue();
        } else {
            help(player, textChannel);
        }
    }

    public void help(DiscordPlayer player, TextChannel textChannel) {
        textChannel.sendMessage(new EmbedBuilder()
            .setTitle("Admin | Hilfe")
                .setColor(Color.RED)

                .setDescription(Discord.getInstance().toString(
                        Arrays.asList(
                                "admin reload | Lädt den Bot neu",
                                "admin ban <category> | Setzt die Ban Kategorie",
                                "admin addTicket <name> <channel> <beschreibung> | Erstellt ein Ticket",
                                "admin addRole <name> <id> | Fügt eine Rolle hinzu",
                                "-----------------------",
                                "admin custom <key> <value> | Setzt einen Config Wert"
                        )
                ))
                .setFooter("Developer | Lystx", player.getUser().getEffectiveAvatarUrl())
            .build()
        ).queue();
    }
}
