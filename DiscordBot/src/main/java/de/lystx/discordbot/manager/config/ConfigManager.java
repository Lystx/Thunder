package de.lystx.discordbot.manager.config;

import de.lystx.discordbot.manager.ticket.Ticket;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class ConfigManager {

    private final Map<String, Object> values;
    private final File file;

    private VsonObject vsonObject;

    private String token, guildID, commandPrefix, banCategory, ticketReactionId, ticketClaimId, ticketCategory;
    private VsonObject roles;
    private List<Ticket> tickets;

    public ConfigManager(String file) {
        this.file = new File(file);
        this.values = new HashMap<>();
        this.tickets = new LinkedList<>();
    }

    public void init() {
        System.out.println("[Discord] Loading ConfigManager....");
        try {
            this.vsonObject = new VsonObject(this.file, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);

            this.token = vsonObject.getString("token", "yourToken");
            this.guildID = vsonObject.getString("guildID", "yourGuildID");
            this.commandPrefix = vsonObject.getString("commandPrefix", "!");
            this.banCategory = vsonObject.getString("banCategory", "yourID");
            this.roles = vsonObject.getVson("roles", new VsonObject());
            this.tickets = vsonObject.has("tickets") ? vsonObject.getList("tickets", Ticket.class) : vsonObject.append("tickets", new LinkedList<>()).getList("tickets", Ticket.class);
            this.ticketCategory = vsonObject.getString("ticketCategory", "yourTicketCategory");
            this.ticketReactionId = vsonObject.getString("ticketReactionId", "yourEmoteID");
            this.ticketClaimId = vsonObject.getString("ticketClaimId", "yourEmoteID");

            this.vsonObject.save();
        } catch (IOException e) {
            System.out.println("[Discord] Config could not be loaded!");
            e.printStackTrace();
        }
    }

    public void reload() {
        this.vsonObject.append("roles", this.roles);
        this.vsonObject.append("tickets", this.tickets);
        this.vsonObject.save();
    }

    public Object valueOf(String key, Object defaultValue) {
        if (!this.values.containsKey(key)) {
            this.values.put(key, defaultValue);
            this.vsonObject.append("values", this.values);
            this.vsonObject.save();
        }
        return this.values.get(key);
    }

    public String getID(String name) {
        return this.roles.getString(name);
    }
}
