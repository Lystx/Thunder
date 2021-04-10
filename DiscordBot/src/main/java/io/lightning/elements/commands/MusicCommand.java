package io.lightning.elements.commands;

import io.lightning.manager.audioplayer.discord.player.AudioPlayer;
import io.lightning.manager.audioplayer.discord.track.AudioTrack;
import io.lightning.Lightning;
import io.lightning.manager.command.CommandCategory;
import io.lightning.manager.command.CommandHandler;
import io.lightning.manager.music.GuildMusicManager;
import io.lightning.manager.music.PlayerManager;
import io.lightning.manager.music.TrackInQueue;
import io.lightning.manager.music.TrackScheduler;
import io.lightning.utils.StringCreator;
import io.lightning.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class MusicCommand extends CommandHandler {

    public static Map<Integer, EmbedBuilder> EMBED_MESSAGES = new HashMap<>();
    public static List<Message> MESSAGES_TO_DELETE = new LinkedList<>();
    public static Message CURRENT_DJ_INFO = null;
    public static Member EXECUTOR = null;
    public static Integer CURRENT_PAGE = 1;

    private final Guild guild;

    public MusicCommand(String name, String description, CommandCategory category, Guild guild, String... aliases) {
        super(name, description, category, aliases);
        this.guild = guild;
    }

    @Override
    public boolean hasPermission(Member member) {
        String id = Lightning.get().getConfigManager().getConfig(this.guild).getVson("roles").getString("dj");
        String id2 = Lightning.get().getConfigManager().getConfig(this.guild).getVson("roles").getString("manager");
        if (id != null && !id.equalsIgnoreCase("null") && !id.equalsIgnoreCase("roleID")) {
            if (member.getRoles().contains(this.guild.getRoleById(id))) {
                return true;
            }
        }
        if (id2 != null && !id2.equalsIgnoreCase("null") && !id2.equalsIgnoreCase("roleID")) {
            if (member.getRoles().contains(this.guild.getRoleById(id2))) {
                return true;
            }
        }
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel, Guild guild) {
        AudioManager audioManager = guild.getAudioManager();

        GuildVoiceState memberVoiceState = executor.getVoiceState();
        VoiceChannel voiceChannel = memberVoiceState.getChannel();
        Member selfMember = guild.getSelfMember();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("join")) {
                if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                    channel.sendMessageFormat("I cant join %s because I do not have the permission " + Permission.VOICE_CONNECT.name() + "!", voiceChannel).queue();
                    return;
                }
                if (!audioManager.isConnected()) {
                    if (!memberVoiceState.inVoiceChannel()) {
                        channel.sendMessage("You aren't in a VoiceChannel that " + selfMember.getAsMention() + " could join!").queue();
                        return;
                    }
                    audioManager.openAudioConnection(voiceChannel);
                    channel.sendMessage(selfMember.getAsMention() + " joined " + voiceChannel.getName() + "!").queue();
                } else {
                    channel.sendMessage("I'm already in a VoiceChannel!").queue();
                }

            } else if (args[0].equalsIgnoreCase("leave")) {
                if (audioManager.isConnected()) {
                    if (memberVoiceState.getChannel() == null || !memberVoiceState.getChannel().getName().equalsIgnoreCase(selfMember.getVoiceState().getChannel().getName())) {
                        channel.sendMessage("You aren't in the same VoiceChannel as " + selfMember.getAsMention() + " to make him leave!").queue();
                        return;
                    }

                    PlayerManager manager = PlayerManager.getInstance();
                    manager.getGuildMusicManager(guild).player.destroy();
                    for (TrackInQueue track : manager.getQueue()) {
                        if (track.getChannel().getGuild().getIdLong() == guild.getIdLong()) {
                            manager.getQueue().remove();
                        }

                    }
                    manager.getGuildMusicManager(guild).scheduler.getQueue().clear();
                    audioManager.closeAudioConnection();
                    channel.sendMessage(selfMember.getAsMention() + " left " + voiceChannel.getName() + "!").queue();
                } else {
                    channel.sendMessage("I'm not even in a VoiceChannel!").queue();
                }
            } else if (args[0].equalsIgnoreCase("playlist")) {

                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }

                PlayerManager manager = PlayerManager.getInstance();
                GuildMusicManager guildMusicManager = manager.getGuildMusicManager(guild);
                if (guildMusicManager.scheduler.getQueueCopy().isEmpty()) {
                    channel.sendMessage(new EmbedBuilder()
                            .setTitle("Lightning | Playlist")
                            .setDescription("The current Playlist does not contain any tracks!")
                            .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                            .build()).queue();
                    return;
                }
                StringCreator sb = new StringCreator();
                int pos = 1;
                int currentpos = guildMusicManager.scheduler.getCurrentPosition() + 1;
                for (AudioTrack each : guildMusicManager.scheduler.getQueueCopy()) {
                    sb.append((pos == currentpos ? "**" : "") +"#" + pos + " > " + each.getInfo().title + " | " + each.getInfo().author + (pos == currentpos ? "**" : ""));
                    pos++;
                }
                AtomicLong total = new AtomicLong(0);
                for (AudioTrack each : guildMusicManager.scheduler.getQueueCopy()) {
                    total.addAndGet(each.getInfo().length);
                }

                AtomicLong current = new AtomicLong(0);
                int position = 1;
                for (AudioTrack each : guildMusicManager.scheduler.getQueueCopy()) {
                    if (position == currentpos) {
                        current.addAndGet(each.getPosition());
                        break;
                    } else {
                        current.addAndGet(each.getInfo().length);
                    }
                    position++;
                }

                String totalString = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(total.get()),
                        TimeUnit.MILLISECONDS.toSeconds(total.get()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total.get()))
                );
                String currentString = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(current.get()),
                        TimeUnit.MILLISECONDS.toSeconds(current.get()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(current.get()))
                );

                String leftString = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(total.get() - current.get()),
                        TimeUnit.MILLISECONDS.toSeconds(total.get() - current.get()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total.get() - current.get()))
                );

                sb.append("Duration total: " + currentString + "/" + totalString + " (" + leftString + " left");
                sb.append("Repeating Mode: " + (guildMusicManager.scheduler.isRepeat() ? "**ON**" : "**OFF**"));

                String content = sb.toString();
                final List<String> messageToSend = Utils.getMessageToSend(content, 1024);

                int page = 1;
                int max = messageToSend.size();

                EMBED_MESSAGES = new HashMap<>();

                for (String s : messageToSend) {
                    EMBED_MESSAGES.put(page, new EmbedBuilder()
                            .setTitle("PlayList | [" + page + "/" + max + "]")
                            .setColor(Color.YELLOW)
                            .setThumbnail("http://simpleicon.com/wp-content/uploads/playlist.png")
                            .setDescription(s)
                            .setFooter("Click reaction to delete message", executor.getUser().getEffectiveAvatarUrl())
                            );
                    page++;
                }
                channel.sendMessage(EMBED_MESSAGES.get(1).build()).queue(message -> {
                    message.addReaction("⬅").queue();
                    message.addReaction("❌").queue();
                    message.addReaction("➡").queue();
                    MESSAGES_TO_DELETE.add(message);
                });

            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }
                PlayerManager manager = PlayerManager.getInstance();
                AudioPlayer player = manager.getGuildMusicManager(guild).player;
                player.setPaused(!player.isPaused());
                channel.sendMessage(player.isPaused() ? "Paused the music player!" : "Resumed the music player!").queue();
            } else if (args[0].equalsIgnoreCase("skip")) {
                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }
                PlayerManager manager = PlayerManager.getInstance();
                TrackScheduler scheduler = manager.getGuildMusicManager(guild).scheduler;
                if (scheduler.getQueueCopy().isEmpty()) {
                    channel.sendMessage("There is no track currently playing!").queue();
                    return;
                }
                scheduler.getCurrentpos().incrementAndGet();
                scheduler.nextTrack();
                channel.sendMessage("Skipping to the next track!").queue();
            } else if (args[0].equalsIgnoreCase("shuffle")) {
                PlayerManager manager = PlayerManager.getInstance();
                TrackScheduler scheduler = manager.getGuildMusicManager(guild).scheduler;
                if (scheduler.getQueueCopy().isEmpty()) {
                    channel.sendMessage("There is no track currently playing!").queue();
                    return;
                }
                Collections.shuffle(scheduler.getQueue());
                channel.sendMessage("Positions of the tracks in the current playlist got randomized!").queue();
            } else if (args[0].equalsIgnoreCase("repeat")) {

                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }

                PlayerManager manager = PlayerManager.getInstance();
                GuildMusicManager guildMusicManager = manager.getGuildMusicManager(guild);
                boolean value = !guildMusicManager.scheduler.isRepeat();
                guildMusicManager.scheduler.setRepeat(value);

                channel.sendMessage("Repeating mode is now turned " + (value ? "on" : "off")).queue();
            } else if (args[0].equalsIgnoreCase("info")) {

                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not connected to a voice channel").queue();
                    return;
                }

                PlayerManager manager = PlayerManager.getInstance();
                TrackScheduler scheduler = manager.getGuildMusicManager(guild).scheduler;
                if (scheduler.getQueueCopy().isEmpty()) {
                    channel.sendMessage("There is no track currently playing!").queue();
                    return;
                }
                String currentPos = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getPosition()),
                        TimeUnit.MILLISECONDS.toSeconds(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getPosition()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getPosition()))
                );
                String finalPos = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getDuration()),
                        TimeUnit.MILLISECONDS.toSeconds(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getDuration()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getDuration()))
                );

                MESSAGES_TO_DELETE.clear();


                AtomicReference<AudioTrack> audioTrack = new AtomicReference<>(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()));

                Message message = channel.sendMessage(this.format(audioTrack.get(), executor, currentPos, finalPos, scheduler.getCurrentPosition())).complete();
                message.addReaction("🛑").queue(response -> message.addReaction("⏮️").queue(response2 -> message.addReaction("⏸️").queue(response3 -> message.addReaction("⏭️").queue(response4 -> message.addReaction("🔀").queue(response6 -> message.addReaction("\uD83D\uDD01").queue(response5 -> message.addReaction("❌").queue()))))));
                MESSAGES_TO_DELETE.add(message);
                CURRENT_DJ_INFO = message;
                EXECUTOR = executor;
                new Thread(() -> {
                    while (!scheduler.getQueueCopy().isEmpty()) {
                        try {
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException ignore) {
                                //IGNORE
                            }

                            try {
                                audioTrack.set(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()));
                                message.editMessage(
                                        this.format(
                                                audioTrack.get(),
                                                executor,
                                                String.format("%02d:%02d",
                                                    TimeUnit.MILLISECONDS.toMinutes(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getPosition()),
                                                    TimeUnit.MILLISECONDS.toSeconds(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getPosition()) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getPosition()))
                                                ),
                                                String.format("%02d:%02d",
                                                    TimeUnit.MILLISECONDS.toMinutes(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getDuration()),
                                                    TimeUnit.MILLISECONDS.toSeconds(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getDuration()) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(scheduler.getQueueCopy().get(scheduler.getCurrentPosition()).getDuration()))
                                                ),
                                                scheduler.getCurrentPosition()
                                        )
                                ).complete(false);
                            } catch (RateLimitedException ignore) {}
                        } catch (Exception e) {
                            break;
                        }
                    }
                    if (message == null || Lightning.get().getJda().getSelfUser() == null) {
                        return;
                    }
                    try {
                        message.editMessage("Track is over!").queue();
                        message.removeReaction("⏸️", Lightning.get().getJda().getSelfUser()).queue();
                        message.removeReaction("⏭️", Lightning.get().getJda().getSelfUser()).queue();
                        message.removeReaction("🛑", Lightning.get().getJda().getSelfUser()).queue();
                        message.removeReaction("⏮️", Lightning.get().getJda().getSelfUser()).queue();
                        message.removeReaction("\uD83D\uDD01", Lightning.get().getJda().getSelfUser()).queue();
                        message.removeReaction("🔀", Lightning.get().getJda().getSelfUser()).queue();
                        message.removeReaction("❌", Lightning.get().getJda().getSelfUser()).queue();
                    } catch (Exception e) {
                        //IGNORING
                    }
                }).start();
            } else {
                syntax("", channel, executor.getUser());
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("play")) {
                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }
                PlayerManager manager = PlayerManager.getInstance();
                manager.loadAndPlay(channel, args[1]);
                int volume = manager.getGuildMusicManager(guild).player.getVolume();
                manager.getGuildMusicManager(guild).player.setVolume(volume);

            } else if (args[0].equalsIgnoreCase("save")) {

                List<Member> mentionList = raw.getMentionedMembers();
                String userId = mentionList.isEmpty() ? args[1] : mentionList.get(0).getUser().getId();
                File folder = new File("local/playlists");
                folder.mkdirs();
                File file = new File(folder, userId + ".txt");
                if (Lightning.get().getPlayListLink() != null) {
                    channel.sendMessage(mentionList.isEmpty() ? "Saved whole playlist into playlist with name " + args[1] + "!" : "Saved whole playlist into your personal playlist!").queue();

                    try {
                        PrintWriter writer = new PrintWriter(new FileOutputStream(file, false));
                        writer.println(Lightning.get().getPlayListLink());
                        writer.flush();
                        writer.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    List<AudioTrack> queue = PlayerManager.getInstance().getGuildMusicManager(guild).scheduler.getQueueCopy();
                    try {
                        PrintWriter writer = new PrintWriter(new FileOutputStream(file, false));
                        queue.forEach(song -> writer.println(song.getInfo().uri));
                        writer.flush();
                        writer.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    channel.sendMessage(mentionList.isEmpty() ? "Saved " + queue.size() + " songs into playlist with name " + args[1] + "!" : "Saved " + queue.size() + " songs into your personal playlist!").queue();
                }

            } else if (args[0].equalsIgnoreCase("promote")) {
                if (!executor.hasPermission(Permission.ADMINISTRATOR)) {
                    channel.sendMessage("This action may only performed by an Administrator!").queue();
                    return;
                }
                final List<User> mentionedUsers = raw.getMentionedUsers();

                if (mentionedUsers.isEmpty()) {
                    channel.sendMessage("Please provide a valid user!").queue(message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
                    return;
                }
                final User user = mentionedUsers.get(0);
                Member member = guild.getMember(user);

                String id = Lightning.get().getConfigManager().getConfig(this.guild).getVson("roles").getString("dj");
                guild.addRoleToMember(member, this.guild.getRoleById(id)).queue();

                channel.sendMessage(user.getAsMention() + " is now able to control the MusicBot!").queue(message -> message.delete().queueAfter(2, TimeUnit.SECONDS));
                raw.delete().queue();
            } else if (args[0].equalsIgnoreCase("load")) {

                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }
                List<Member> mentionList = raw.getMentionedMembers();
                String userId = mentionList.isEmpty() ? args[1] : mentionList.get(0).getUser().getId();
                File file = new File(new File("local/playlists"), userId + ".txt");
                if (file.exists()) {
                    PlayerManager manager = PlayerManager.getInstance();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        for (String link; (link = br.readLine()) != null;) {
                            manager.loadAndPlay(channel, link);
                        }
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    channel.sendMessageFormat(mentionList.isEmpty() ? "There is no playlist with the name " + args[1]  + " yet!" : mentionList.get(0).getAsMention() + " got no personal playlist yet!").queue();
                }
            } else if (args[0].equalsIgnoreCase("volume")) {

                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }
                PlayerManager manager = PlayerManager.getInstance();
                int volume = 100;
                try {
                    volume = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    channel.sendMessage("Please provide a valid Number!").queue();
                }
                manager.getGuildMusicManager(guild).player.setVolume(volume);
                channel.sendMessage("Changed the volume to " + volume).queue();
            } else if (args[0].equalsIgnoreCase("skip")) {
                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }
                try {
                    PlayerManager manager = PlayerManager.getInstance();
                    TrackScheduler scheduler = manager.getGuildMusicManager(guild).scheduler;
                    if (scheduler.getQueueCopy().isEmpty()) {
                        channel.sendMessage("There is no track currently playing!").queue();
                        return;
                    }
                    int pos = Integer.parseInt(args[1]) - 1;
                    if (pos >= 0 || pos < scheduler.getQueue().size()) {
                        scheduler.getCurrentpos().set(pos);
                        scheduler.nextTrack();
                        channel.sendMessage("Skipping to the track " + (pos + 1) + "!").queue();
                    } else {
                        channel.sendMessage("Track position is out of range!").queue();
                    }
                } catch (NumberFormatException e) {
                    channel.sendMessage("Invalid input! Integer expected!").queue();
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!audioManager.isConnected()) {
                    channel.sendMessage("I'm not even connected to a VoiceChannel!").queue();
                    return;
                }
                try {
                    PlayerManager manager = PlayerManager.getInstance();
                    TrackScheduler scheduler = manager.getGuildMusicManager(guild).scheduler;
                    if (scheduler.getQueueCopy().isEmpty()) {
                        channel.sendMessage("There is no track currently playing!").queue();
                        return;
                    }
                    AudioTrack track = scheduler.getQueue().remove(Integer.parseInt(args[1]) - 1);
                    channel.sendMessage("Removed track " + track.getInfo().title + " from queue!").queue();
                } catch (Exception e) {
                    channel.sendMessage("Invalid arguments!").queue();
                }
            } else {
                syntax("", channel, executor.getUser());
            }
        } else {
            syntax("", channel, executor.getUser());
        }
    }




    public static MessageEmbed format(AudioTrack audioTrack, Member executor, String currentPos1, String finalPos1, int pos) {
        StringCreator stringCreator = new StringCreator();
        stringCreator.append("Title : " + audioTrack.getInfo().title);
        stringCreator.append("Author : " + audioTrack.getInfo().author);
        stringCreator.append("> " + currentPos1 + "/" + finalPos1);

        return new EmbedBuilder()
                .setTitle("Track | (Playlist Position #" + (pos + 1) + ")")
                .setColor(Color.YELLOW)
                .setThumbnail("https://img.youtube.com/vi/" + audioTrack.getInfo().uri.split("v=")[1] + "/maxresdefault.jpg")
                .setDescription(stringCreator.toString())
                .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                .build();
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

        StringCreator creator = new StringCreator();


        String p = Lightning.get().getCommandManager(this.guild).getPrefix();

        creator.append("  » " + p + "music play <url> | Plays a song from youtube");
        creator.append("  » " + p + "music promote <user> | Promotes a user");
        creator.append("  » " + p + "music volume <volume> | Changes the volume of the bot");
        creator.append("  » " + p + "music skip <position> | Skips the current song / to a position");
        creator.append("  » " + p + "music toggle | Pauses/Resumes the current song");
        creator.append("  » " + p + "music info | Shows the interactive DJ Manager");
        creator.append("  » " + p + "music repeat | Toggles repeation mode");
        creator.append("  » " + p + "music remove <index> | Removes something from the queue");
        creator.append("  » " + p + "music join | Joins your current channel");
        creator.append("  » " + p + "music leave | Leaves your current channel");
        creator.append("  » " + p + "music shuffle | Shuffles (if playlist)");
        creator.append("  » " + p + "music save | Saves current songs into personal playlist");
        creator.append("  » " + p + "music load | Plays songs into personal playlist");

        channel.sendMessage(

                new EmbedBuilder()
                        .setTitle("Lightning | Music")
                        .setDescription(creator.toString())
                        .setColor(Color.YELLOW)
                        .setThumbnail("https://image.similarpng.com/very-thumbnail/2020/07/Music-icon-gradient-vector-PNG.png")
                        .setFooter("Executor | " + executor.getAsTag(), executor.getEffectiveAvatarUrl())
                        .build()
        ).queue();
    }
}
