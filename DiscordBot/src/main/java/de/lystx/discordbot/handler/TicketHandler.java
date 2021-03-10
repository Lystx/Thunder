package de.lystx.discordbot.handler;

import de.lystx.discordbot.Discord;
import de.lystx.discordbot.elements.DiscordPlayer;
import de.lystx.discordbot.manager.ticket.Ticket;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class TicketHandler extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser() != null && event.getUser().isBot()) {
            return;
        }
        for (Ticket ticket : Discord.getInstance().getConfigManager().getTickets()) {
            if (ticket.getId().equalsIgnoreCase(event.getChannel().getId())) {
                if (event.getUser() == null) {
                    System.out.println("[DiscordBot] User for TicketSupport wasn't found");
                    return;
                }
                try {
                    if (event.getReactionEmote().getEmote().getId().equalsIgnoreCase(Discord.getInstance().getConfigManager().getTicketReactionId())) {
                        event.getReaction().removeReaction(event.getUser()).queue();
                        Discord.getInstance().getTicketManager().openTicket(ticket, new DiscordPlayer(event.getUser()));
                    } else {
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }
                } catch (IllegalStateException e) {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
                break;
            } else if (event.getTextChannel().getName().startsWith("ticket-") && !Discord.getInstance().getTicketManager().isTicket(event.getTextChannel())) {
                if (event.getReactionEmote().getEmote().getId().equalsIgnoreCase(Discord.getInstance().getConfigManager().getTicketReactionId())) {
                    Discord.getInstance().getTicketManager().closeTicket(event.getTextChannel());
                    break;
                } else if (event.getReactionEmote().getEmote().getId().equalsIgnoreCase(Discord.getInstance().getConfigManager().getTicketClaimId())) {
                    Discord.getInstance().getTicketManager().claimTicket(event.getTextChannel(), new DiscordPlayer(event.getUser()));
                }
            }
        }
    }


    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getChannel().getName().startsWith("ticket-") && !Discord.getInstance().getTicketManager().isTicket(event.getChannel())) {
            Discord.getInstance().getTicketManager().getTextChannels().put(event.getChannel().getId(), true);
        }
    }
}
