package io.lightning.elements.commands;

import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import io.lightning.utils.StringCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class HowgayCommand extends CommandHandler {

    public HowgayCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {


        int i = new Random().nextInt(100);

        List<User> users = raw.getMentionedUsers();

        if (executor.getUser().getId().equalsIgnoreCase("704419523922493542")) {
            i = ThreadLocalRandom.current().nextInt(100, 200);
        }
        if (users.isEmpty()) {
            channel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Lightning | Gay-Rating")
                            .setColor(Color.PINK)
                            .setDescription("You are " + i + "% :rainbow_flag:")
                            .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                            .build()
            ).queue();
        } else {
            final User user = users.get(0);
            if (user.getId().equalsIgnoreCase("704419523922493542")) {
                i = ThreadLocalRandom.current().nextInt(100, 200);
            } else {
                i = ThreadLocalRandom.current().nextInt(0, 100);
            }
            channel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Lightning | Gay-Rating")
                            .setColor(Color.PINK)
                            .setDescription(user.getAsMention() + " is " + i + "% :rainbow_flag:")
                            .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                            .build()
            ).queue();
        }
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
