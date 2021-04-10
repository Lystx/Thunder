package io.lightning.elements.commands;

import io.lightning.Lightning;
import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.List;

public class HelpCommand extends CommandHandler {

    public HelpCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {

        StringBuilder sb = new StringBuilder();

        for (CommandCategory value : CommandCategory.values()) {
            sb.append("**").append(value.name()).append("**").append("\n");
            final List<CommandHandler> commands = Lightning.get().getCommandManager(guild).getCommands(value);
            if (!commands.isEmpty()) {
                for (CommandHandler command : commands) {
                    sb.append("  » " + command.getName() + " | " + command.getDescription()).append("\n");
                }
            } else {
                sb.append("  » No commands for this category!").append("\n");
            }
        }

        channel.sendMessage(

            new EmbedBuilder()
                .setTitle("Lightning | Help")
                .setDescription(sb.toString())
                .setColor(Color.YELLOW)
                .setThumbnail("https://www.nicepng.com/png/full/368-3685578_lucky-block-bike-pixel-art.png")
                .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
            .build()
        ).queue();

    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
