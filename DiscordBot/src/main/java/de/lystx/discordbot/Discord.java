package de.lystx.discordbot;

import de.lystx.discordbot.commands.*;
import de.lystx.discordbot.elements.Acceptor;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.handler.MessageHandler;
import de.lystx.discordbot.handler.TicketHandler;
import de.lystx.discordbot.manager.config.ConfigManager;
import de.lystx.discordbot.manager.ban.BanManager;
import de.lystx.discordbot.manager.command.CommandManager;
import de.lystx.discordbot.manager.ticket.TicketManager;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public class Discord {

    @Getter
    private static Discord instance;

    private final ConfigManager configManager;
    private final CommandManager commandManager;
    private final BanManager banManager;
    private final TicketManager ticketManager;
    private JDA internal;

    public Discord() {
        instance = this;

        System.out.println("[Discord] Loading Managers....");
        this.configManager = new ConfigManager("config.vson");
        this.commandManager = new CommandManager();
        this.banManager = new BanManager();
        this.ticketManager = new TicketManager();
    }

    public void bootstrap() {
        this.configManager.init();

        System.out.println("[Discord] Building Discord JDA");
        JDABuilder api = JDABuilder.createDefault(this.configManager.getToken())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new MessageHandler())
                //.addEventListeners(new ActionsHandler())
                .addEventListeners(new TicketHandler())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.playing(" auf Valane"));
        System.out.println("[Discord] Logging in to DiscordBot...");
        try {
            this.internal = api.build();
            System.out.println("[Discord] Done!");
        } catch (LoginException e) {
            System.out.println("[Discord] Failed!");
        }

        this.commandManager.registerCommand(new StopCommand());
        this.commandManager.registerCommand(new HowgayCommand());
        this.commandManager.registerCommand(new AdminCommand());
        this.commandManager.registerCommand(new HelpCommand());
        this.commandManager.registerCommand(new PingCommand());
        this.commandManager.registerCommand(new UserInfoCommand());

        this.ticketManager.init();
    }

    public void soNicht(DiscordPlayer executor, TextChannel textChannel) {
        textChannel.sendMessage(new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle("Soooooooo nicht!")
                .setThumbnail("https://pbs.twimg.com/media/EOQTPtyWsAAD3Df?format=jpg&name=medium")
                .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                .build()).queue();
    }

    public String toString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    public void shutdown() {
        System.out.println("[Discord] Shutting down....");
        this.schedule(() -> {
            internal.shutdown();
            System.exit(0);
        }, 20L);
    }

    public void schedule(Runnable runnable, long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay * 50);
    }

    public Member getMember(String tag) {
        try {
            return this.getGuild().getMember(this.internal.getUserByTag(tag));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Guild getGuild() {
        return this.internal.getGuildById(this.configManager.getGuildID());
    }

    public void sendMessageBy(String id, String message, Color color, Acceptor<TextChannel, Message> acceptor) {
        for (TextChannel textChannel : this.internal.getTextChannels()) {
            if (textChannel.getId().contains(id)) {
                textChannel.sendMessage(new EmbedBuilder().setColor(color).setDescription(message).build()).queue(message1 -> acceptor.submit(textChannel, message1));
                return;
            }
        }
    }

    public void sendMessage(String id, String message, Color color, Acceptor<TextChannel, EmbedBuilder> acceptor) {
        for (TextChannel textChannel : this.internal.getTextChannels()) {
            if (textChannel.getId().contains(id)) {
                acceptor.submit(textChannel, new EmbedBuilder().setColor(color).setDescription(message));
                return;
            }
        }
    }

    public Emote getEmote(String name) {
        return this.internal.getEmotes().stream().filter(emote -> emote.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void reload() {
        this.configManager.reload();
    }
}
