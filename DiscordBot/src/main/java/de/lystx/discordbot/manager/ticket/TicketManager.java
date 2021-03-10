package de.lystx.discordbot.manager.ticket;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.ChannelManager;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ContextException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;


public class TicketManager extends ChannelManager {

    private final List<String> usersInSupport;
    @Getter
    private final Map<String, Boolean> textChannels;


    public TicketManager() {
        this.usersInSupport = new LinkedList<>();
        this.textChannels = new HashMap<>();
    }

    public void createTicket(String name, String id, String description) {
        Ticket ticket = new Ticket(name, id, description);
        Discord.getInstance().getConfigManager().getTickets().add(ticket);
        Discord.getInstance().getConfigManager().reload();
        this.init();
    }

    public void init() {
        if (Discord.getInstance().getConfigManager().getTicketReactionId().equalsIgnoreCase("yourEmoteID")) {
            System.out.println("[Discord] Default EmoteID can't be used for TicketSystem!");
            return;
        }
        for (Ticket ticket : Discord.getInstance().getConfigManager().getTickets()) {
            if (Discord.getInstance().getGuild() == null) {
                Discord.getInstance().schedule(this::init, 20L);
                return;
            }
            TextChannel textChannel = Discord
                    .getInstance()
                    .getGuild()
                    .getTextChannelById(
                            ticket
                                    .getId()
                    );
            if (textChannel == null) {
                System.out.println("[Discord] Couldn't load Ticket " + ticket.getName() + " because TextChannel [" + ticket.getId() + "] doesn't exist (anymore?) !");
                continue;
            }
            List<Message> list = textChannel.getHistory().retrievePast(1).complete();
            if (list.isEmpty()) {
                textChannel.sendMessage(new EmbedBuilder()
                        .setTitle("Ticket | " + ticket.getName())
                        .setColor(Color.BLUE)
                        .setDescription(ticket.getDescription())
                        .setThumbnail("https://www.flaticon.com/premium-icon/icons/svg/1666/1666022.png")
                        .build()).queue(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) {
                        message.addReaction(Discord.getInstance().getGuild().getEmoteById(Discord.getInstance().getConfigManager().getTicketReactionId())).queue();
                    }
                });
                } else {
                for (Message message : list) {
                    for (MessageReaction reaction : message.getReactions()) {
                        message.removeReaction(reaction.getReactionEmote().getEmote()).queue();
                    }
                    message.addReaction(Discord.getInstance().getGuild().getEmoteById(Discord.getInstance().getConfigManager().getTicketReactionId())).queue();
                }
            }
        }
    }

    public void claimTicket(TextChannel textChannel, DiscordPlayer player) {

    }

    public Ticket getTicket(TextChannel textChannel) {
        return Discord.getInstance().getConfigManager().getTickets().stream().filter(ticket -> ticket.getId().equalsIgnoreCase(textChannel.getId())).findFirst().orElse(null);
    }

    public void closeTicket(TextChannel textChannel) {
        textChannel.delete().queue();
        String user = textChannel.getName().split("-")[2];
        usersInSupport.remove(user.toLowerCase());
    }

    public void openTicket(Ticket ticket, DiscordPlayer discordPlayer) {

        if (this.usersInSupport.contains(discordPlayer.getUser().getName().toLowerCase())) {
            discordPlayer.sendMessage(new EmbedBuilder().setThumbnail("https://media.tenor.com/images/e07c8c5689162933f77986271024ad62/tenor.gif").setColor(Color.RED).setTitle("Support | TicketSystem").setDescription("Bitte versuche nicht unnötig mehrere Tickets zu öffnen").build());
            return;
        }

        this.queueChannel(discordPlayer, "ticket-" + ticket.getName() + "-" + discordPlayer.getUser().getName(), Discord.getInstance().getConfigManager().getTicketCategory(), new Consumer<TextChannel>() {
            @Override
            public void accept(TextChannel textChannel) {
                List<Role> roles = new LinkedList<>(Discord.getInstance().getGuild().getRoles());
                roles.add(Discord.getInstance().getGuild().getPublicRole());

                for (Role role : roles) {
                    if (textChannel.getPermissionOverride(role) == null) {
                        textChannel.createPermissionOverride(role).setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ).queue();
                    } else {
                        if (!textChannel.getPermissionOverride(role).getInherit().contains(Permission.VIEW_CHANNEL)) {
                            textChannel.createPermissionOverride(role).setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_EMBED_LINKS).queue();
                        }
                    }
                }
                textChannels.put(textChannel.getId(), false);
                if (textChannels.containsKey(textChannel.getId()) && !textChannels.get(textChannel.getId())) {

                   Discord.getInstance().schedule(() -> warn(1, textChannel), 1200L);
                }
                if (textChannels.containsKey(textChannel.getId()) && !textChannels.get(textChannel.getId())) {
                    Discord.getInstance().schedule(() -> warn(2, textChannel), 2400L);
                }
                if (textChannels.containsKey(textChannel.getId()) && !textChannels.get(textChannel.getId())) {
                    Discord.getInstance().schedule(() -> warn(3, textChannel), 3600L);
                }
                if (textChannels.containsKey(textChannel.getId()) && !textChannels.get(textChannel.getId())) {
                    Discord.getInstance().schedule(() -> warn(4, textChannel), 4800L);
                }
                if (textChannels.containsKey(textChannel.getId()) && !textChannels.get(textChannel.getId())) {
                    Discord.getInstance().schedule(() -> closeTicket(textChannel), 6000L);
                }
                usersInSupport.add(discordPlayer.getUser().getName().toLowerCase());
                textChannel.sendMessage(new EmbedBuilder().setTitle("Support | TicketSystem").setDescription("Um das Ticket zu schließen, reagiere auf die Nachricht unten\nBitte habe einen Moment Geduld").setColor(Color.BLUE).build()).queue(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) {
                        textChannel.sendMessage(new MessageBuilder().setContent("Der " + Discord.getInstance().getGuild().getRoleById(Discord.getInstance().getConfigManager().getID("Support")).getAsMention() + " wird sich gleich um dich kümmern!").build()).queue();
                        message.addReaction(Discord.getInstance().getGuild().getEmoteById(Discord.getInstance().getConfigManager().getTicketReactionId())).queue();
                        message.addReaction(Discord.getInstance().getGuild().getEmoteById(Discord.getInstance().getConfigManager().getTicketClaimId())).queue();
                    }
                });

            }
        });
    }

    public boolean isTicket(TextChannel textChannel) {
        for (Ticket ticket : Discord.getInstance().getConfigManager().getTickets()) {
            if (textChannel.getId().equalsIgnoreCase(ticket.getId())) {
                return true;
            }
        }
        return false;
    }
    public void addUserToChannel(TextChannel channel, User user) {
        Guild guild = channel.getGuild();
        Member member = guild.getMember(user);
        channel.createPermissionOverride(member)
                .setAllow(Permission.VIEW_CHANNEL)
                .queue();
    }

    public void warn(int min, TextChannel textChannel) {
        if (textChannel == null) {
            return;
        }
        if (this.textChannels.containsKey(textChannel.getId()) && !this.textChannels.get(textChannel.getId())) {
            int remain = 5 - min;
            if (Discord.getInstance().getGuild().getTextChannelsByName(textChannel.getName(), true).isEmpty()) {
                return;
            }
            textChannel.sendMessage(new EmbedBuilder().setTitle("Ticket | Warning").setColor(Color.ORANGE).setDescription("Dieses Ticket wird sich in " + remain + " Minuten schließen, wenn keine Nachricht gesendet wird.").build()).queue();
        }
    }




}
