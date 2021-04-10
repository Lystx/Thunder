package io.lightning;

import java.awt.*;
import java.io.BufferedReader;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.lightning.elements.adapter.LightningMessageAdapter;
import io.lightning.elements.commands.*;
import io.lightning.listener.*;
import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandManager;
import io.lightning.manager.config.ConfigManager;
import io.lightning.manager.music.*;

import io.vson.annotation.other.Vson;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

@Getter @Setter
public class Lightning extends ListenerAdapter {
	
	private final BufferedReader in;
	private final JDA jda;
	private final Guild guild;
	private final ConfigManager configManager;

	private static Lightning instance;

	private String playListLink;

	private Map<String, CommandManager> commandManagers;

	public Lightning(BufferedReader in, JDA jda, Guild guild) {
		this.commandManagers = new HashMap<>();
		this.guild = guild;
		instance = this;
		this.in = in;
		this.jda = jda;

		Vson.get().registerAdapter(new LightningMessageAdapter());

		this.configManager = new ConfigManager();

		this.jda.addEventListener(new LightningOwnChannelListener());
		this.jda.addEventListener(new LightningMainListener());
		this.jda.addEventListener(new LightningCommandListener());
		this.jda.addEventListener(new LightningMusicListener());
		this.jda.addEventListener(new LightningMemberChangeListener());
		this.jda.addEventListener(new LightningMemeListener());

	}

	public static Lightning get() {
		return instance;
	}


	public Role createRole(Guild guild, String name, Color color, Consumer<Role> consumer) {

		final List<Role> lightning = guild.getRolesByName(name, true);
		if (lightning.isEmpty()) {
			final Role complete = guild.createRole().setColor(color).setName(name).complete();
			consumer.accept(complete);
			return complete;
		}
		consumer.accept(lightning.get(0));
		return lightning.get(0);
	}

	public void init(Guild guild) {

		this.commandManagers.put(guild.getId(), new CommandManager(this.configManager.getConfig(guild).getString("commandPrefix"), guild));
		this.getCommandManager(guild).registerCommand(new HelpCommand("help", "Shows you all commands", CommandCategory.GENERAL, "?"));
		this.getCommandManager(guild).registerCommand(new PingCommand("ping", "Shows Bot's ping", CommandCategory.OTHER, "ms"));
		this.getCommandManager(guild).registerCommand(new MusicCommand("music", "Manages the internal MusicBot", CommandCategory.FUN, guild, "dj"));
		this.getCommandManager(guild).registerCommand(new AdminCommand("admin", "Only available for Owner", CommandCategory.ADMINISTRATION));
		this.getCommandManager(guild).registerCommand(new PPCommand("pp", "Rates your pp", CommandCategory.FUN));
		this.getCommandManager(guild).registerCommand(new MemeCommand("meme", "Shows you a meme", CommandCategory.FUN));
		this.getCommandManager(guild).registerCommand(new HowgayCommand("howgay", "Rates how gay you are", CommandCategory.FUN));
		this.getCommandManager(guild).registerCommand(new SettingsCommand("settings", "Manages the settings", CommandCategory.ADMINISTRATION));
		this.getCommandManager(guild).registerCommand(new MuteCommand("mute", "Mutes People", CommandCategory.ADMINISTRATION));
		this.getCommandManager(guild).registerCommand(new UnmuteCommand("unmute", "Unmutes People", CommandCategory.ADMINISTRATION));
		this.getCommandManager(guild).registerCommand(new ConfigCommand("config", "Manages the config", CommandCategory.ADMINISTRATION));


		Member member = guild.getMember(jda.getSelfUser());
		if (member == null) {
			return;
		}

		List<Permission> permissions = new LinkedList<>(guild.getPublicRole().getPermissions());
		permissions.remove(Permission.MESSAGE_WRITE);

		this.createRole(guild, "Lightning", Color.ORANGE, role -> role.getManager().setMentionable(false).setHoisted(true).setPermissions(Permission.ALL_PERMISSIONS).queue());

		final Role role1 = this.createRole(guild, "LightningBot-Muted", Color.GRAY, role -> role.getManager().setMentionable(false).setHoisted(false).setPermissions(permissions).queue());
		permissions.clear();
		permissions.addAll(Arrays.asList(Permission.values()));
		Role manager = this.createRole(guild, "LightningBot-Manager", Color.MAGENTA, role -> role.getManager().setMentionable(false).setHoisted(true).setPermissions(guild.getPublicRole().getPermissions()).queue());
		Role dj = this.createRole(guild, "LightningBot-DJ", Color.CYAN, role -> role.getManager().setMentionable(false).setHoisted(true).setPermissions(guild.getPublicRole().getPermissions()).queue());
		Role admin = this.createRole(guild, "LightningBot-Admin", Color.YELLOW, role -> role.getManager().setMentionable(false).setHoisted(false).setPermissions(Arrays.asList(Permission.values())).queue());

		for (Category textChannel : guild.getCategories()) {
			if (textChannel.getPermissionOverrides().isEmpty()) {
				textChannel.createPermissionOverride(role1).setDeny(Permission.MESSAGE_WRITE).queue();
				continue;
			}
			for (PermissionOverride permissionOverride : textChannel.getPermissionOverrides()) {
				final EnumSet<Permission> denied = permissionOverride.getDenied();
				denied.add(Permission.MESSAGE_WRITE);
				textChannel.putPermissionOverride(role1).setDeny(denied).queue();
			}
		}
		guild.addRoleToMember(member, guild.getRolesByName("Lightning", true).get(0)).queue();

		permissions.add(Permission.MESSAGE_WRITE);
		final Role aDefault = this.createRole(guild, "Default", Color.GREEN, role -> role.getManager().setMentionable(true).setHoisted(true).setPermissions(permissions).queue());

		final VsonObject config = this.configManager.getConfig(guild);
		final VsonObject roles = config.getVson("roles", VsonSettings.OVERRITE_VALUES);
		roles.append("muted", role1.getId());
		roles.append("default", aDefault.getId());
		roles.append("dj", dj.getId());
		roles.append("*", admin.getId());
		roles.append("manager", manager.getId());
		config.append("roles", roles);
		config.save();

		for (Member guildMember : guild.getMembers()) {
			if (guildMember.getRoles().isEmpty()) {
				guild.addRoleToMember(guildMember, aDefault).queue();
			}
		}

		final VsonObject customChannel = config.getVson("customChannel");
		String category = customChannel.getString("category");
		String name = customChannel.getString("name");

		if (!category.equalsIgnoreCase("null") && !name.equalsIgnoreCase("null")) {
			if (guild.getVoiceChannelsByName(name, true).size() == 0) {
				guild.createVoiceChannel(name)
						.setParent(guild.getCategoryById(category))
						.setName(name)
						.complete();
			}
		}

	}

	public CommandManager getCommandManager(Guild guild) {
		if (!this.commandManagers.containsKey(guild.getId())) {
			this.commandManagers.put(guild.getId(), new CommandManager(this.configManager.getConfig(guild).getString("commandPrefix"), guild));
		}
		return commandManagers.get(guild.getId());
	}


	public boolean hasPermission(Member member) {
		String id2 = this.configManager.getConfig(this.guild).getVson("roles").getString("manager");
		if (id2 != null && !id2.equalsIgnoreCase("null") && !id2.equalsIgnoreCase("roleID")) {
			if (member.getRoles().contains(this.guild.getRoleById(id2))) {
				return true;
			}
		}
		return member.hasPermission(Permission.ADMINISTRATOR);
	}

	public void schedule(Runnable runnable, int interval, TimeUnit timeUnit) {
		new Timer()
			.schedule(new TimerTask() {
				@Override
				public void run() {
					runnable.run();
				}
			}, timeUnit.toMillis(interval));
	}

	@SneakyThrows
	public void shutdown() {
		System.out.println("[INFO] Stopping LightningBot...");
		System.out.println("[INFO] Deleting " + MusicCommand.MESSAGES_TO_DELETE.size() + " Messages...");
		for (Message message : MusicCommand.MESSAGES_TO_DELETE) {
			message.delete().queue();
		}
		for (Guild guild : this.jda.getGuilds()) {
			System.out.println("[INFO] Closing MusicBot for Guild with ID " + guild.getId() + "!");
			AudioManager audioManager = guild.getAudioManager();
			if (!audioManager.isConnected()) {
				continue;
			}
			PlayerManager.getInstance().getGuildMusicManager(guild).player.destroy();
			audioManager.closeAudioConnection();
		}
		this.jda.shutdownNow();
		System.out.println("[INFO] Exiting....");
		this.schedule(() -> System.exit(0), 1, TimeUnit.SECONDS);
	}

}
