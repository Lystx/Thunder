package io.lightning.manager.launcher;

import io.lightning.Lightning;
import io.lightning.elements.StatusHolder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Bootstrap {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("[Lightning] Please provide a Bot-Token!");
            System.exit(0);
            return;
        }
        try {
            JDABuilder api = JDABuilder.createDefault(args[0])
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .enableIntents(
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_BANS,
                            GatewayIntent.GUILD_MESSAGES
                    )
                    .setActivity(Activity.playing(" logging in..."));
            JDA jda = api.build();
            jda.addEventListener(new ReadyListener(jda));
        } catch (LoginException e) {
            System.out.println("[Lightning] Couldn't log in to LightningBot!");
        }
    }


    @RequiredArgsConstructor
    public static class ReadyListener extends ListenerAdapter {

        private final JDA jda;

        private boolean init;

        @Override
        public void onGuildReady(@NotNull GuildReadyEvent event) {
            if (!init) {
                init = true;
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                new Lightning(in, jda, event.getGuild());
                new Thread(StatusHolder::run).start();
            }
            Lightning.get().init(event.getGuild());
        }
    }
}
