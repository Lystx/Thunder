package io.lightning.elements.commands;

import io.lightning.Lightning;
import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SettingsCommand extends CommandHandler {

    Guild guild;

    public SettingsCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return Lightning.get().hasPermission(member);
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {
        this.guild = guild;
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("mute")) {
                final List<Role> mentionedRoles = raw.getMentionedRoles();
                if (mentionedRoles.isEmpty()) {
                    raw.delete().queue();
                    channel.sendMessage("Please mention the Mute-Role!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                    return;
                }
                if (mentionedRoles.size() != 1) {
                    raw.delete().queue();
                    channel.sendMessage("Please only mention one Mute-Role!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                    return;
                }
                final Role role = mentionedRoles.get(0);

                final VsonObject config = Lightning.get().getConfigManager().getConfig(guild);
                final VsonObject roles = config.getVson("roles", VsonSettings.OVERRITE_VALUES);
                roles.append("muted", role.getId());
                config.append("roles", roles);
                config.save();
                raw.delete().queue();
                channel.sendMessage("Changed Mute-Role to " + role.getAsMention() + "!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
            } else if (args[0].equalsIgnoreCase("welcome")) {
                final List<TextChannel> mentionedChannels = raw.getMentionedChannels();
                if (mentionedChannels.isEmpty()) {
                    raw.delete().queue();
                    channel.sendMessage("Please mention the WelcomeChannel!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                    return;
                }
                final TextChannel textChannel = mentionedChannels.get(0);

                final VsonObject config = Lightning.get().getConfigManager().getConfig(guild);
                final VsonObject welcome = config.getVson("welcome", VsonSettings.OVERRITE_VALUES);
                welcome.append("channel", textChannel.getId());
                config.save();
                raw.delete().queue();
                channel.sendMessage("Changed WelcomeChannel to " + textChannel.getAsMention() + "!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
            } else if (args[0].equalsIgnoreCase("prefix")) {

                String prefix = args[1];
                Lightning.get().getCommandManager(guild).setPrefix(prefix);

                final VsonObject config = Lightning.get().getConfigManager().getConfig(guild);
                config.append("commandPrefix", prefix);
                config.save();
                raw.delete().queue();
                channel.sendMessage("Changed prefix to '" + prefix + "' !").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
            }
        } else {
            syntax("", channel, executor.getUser());
        }
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {
        final VsonObject config = Lightning.get().getConfigManager().getConfig(guild);

        Role role;
        String welcome;
        if (config.getVson("roles").getString("muted").equalsIgnoreCase("roleID")) {
            role = null;
        } else {
            role = Lightning.get().getGuild().getRoleById(config.getVson("roles").getString("muted"));
        }
        if (config.getVson("welcome").getString("channel") == null || config.getVson("welcome").getString("channel").equalsIgnoreCase("null")) {
            welcome = "Not set";
        } else {
            welcome = this.guild.getTextChannelById( config.getVson("welcome").getString("channel")).getAsMention();
        }

        String p = Lightning.get().getCommandManager(guild).getPrefix();

        channel.sendMessage(
                new EmbedBuilder()
                .setTitle("Lightning | Settings")
                .setThumbnail("https://p.kindpng.com/picc/s/543-5439191_configuration-control-gear-preferences-repair-setting-gear-and.png")
                .appendDescription(p + "settings mute <mention-role> | Sets Mute-Role\n")
                .appendDescription(p + "settings prefix <prefix> | Sets CommandPrefix\n")
                .appendDescription(p + "settings welcome <channel> | Sets WelcomeChannel\n")
                .addField("Prefix", "» '" + config.getString("commandPrefix") + "'", true)
                .addField("MuteRole", "» " + (role == null ? "Not defined" : role.getAsMention())  , true)
                .addField("WelcomeChannel", "» " + welcome, true)
                .setColor(Color.ORANGE)
                .setFooter("Executor | " + executor.getAsTag(), executor.getEffectiveAvatarUrl())
                .build()
        ).queue();
    }
}
