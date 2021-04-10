package io.lightning.elements.commands;

import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import io.lightning.utils.StringCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class PPCommand extends CommandHandler {


    public PPCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {

        final int i = new Random().nextInt(20);

        StringCreator creator = new StringCreator();
        creator.setNewLine(false);
        creator.append("8");
        for (int i1 = 0; i1 < i; i1++) {
            creator.append("=");
        }
        creator.append("D");


        List<User> users = raw.getMentionedUsers();

        if (users.isEmpty()) {
            channel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Lightning | PP Rating")
                            .setColor(Color.PINK)
                            .setDescription("Your PP: " + creator.toString())
                            .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                            .build()
            ).queue();
        } else {
            final User user = users.get(0);
            channel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Lightning | PP Rating")
                            .setColor(Color.PINK)
                            .setDescription("PP of " + user.getAsMention() + " : " + creator.toString())
                            .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                            .build()
            ).queue();
        }
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
