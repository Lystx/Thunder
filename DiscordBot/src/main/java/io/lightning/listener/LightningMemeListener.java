package io.lightning.listener;

import io.lightning.Lightning;
import io.lightning.elements.commands.MemeCommand;
import io.lightning.utils.StringCreator;
import io.vson.elements.object.VsonObject;
import jdk.nashorn.api.scripting.URLReader;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LightningMemeListener extends ListenerAdapter {

    @Override @SneakyThrows
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (!event.getChannelType().equals(ChannelType.TEXT)) {
            return;
        }

        Message message = event.getTextChannel().retrieveMessageById(event.getMessageId()).complete();
        MessageReaction.ReactionEmote reaction = event.getReactionEmote();
        if (!reaction.isEmoji()) {
            return;
        }

        String emote = reaction.getEmoji();

        if (!message.getAuthor().equals(Lightning.get().getJda().getSelfUser())) {
            return;
        }

        if (event.getUser().equals(Lightning.get().getJda().getSelfUser())) {
            return;
        }

        switch (emote) {
            case "\uD83D\uDD01": {
                if (MemeCommand.CURRENT_SUB_REDDIT.isEmpty()) {
                    return;
                }
                final String subreddit = MemeCommand.CURRENT_SUB_REDDIT.getString("subreddit");

                StringCreator stringCreator = new StringCreator();
                BufferedReader bufferedReader = new BufferedReader(new URLReader(new URL("http://meme-api.herokuapp.com/gimme/" + subreddit)));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringCreator.append(line);
                }

                VsonObject vsonObject = new VsonObject(stringCreator.toString());

                EmbedBuilder embedBuilder = new EmbedBuilder(message.getEmbeds().get(0))
                        .setImage(vsonObject.getString("url"))
                        .setTitle(vsonObject.getString("title"))
                        .setFooter("Subreddit | " + vsonObject.getString("subreddit"), event.getUser().getEffectiveAvatarUrl());

                message.editMessage(embedBuilder.build()).queue();
                message.removeReaction(emote, event.getUser()).queue();
                break;
            }
        }

    }
}
