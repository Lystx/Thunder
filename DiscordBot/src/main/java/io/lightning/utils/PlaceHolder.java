package io.lightning.utils;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class PlaceHolder {


    private Member member;
    private Guild guild;
    private TextChannel textChannel;

    private String content;

    public PlaceHolder(Member member) {
        this(member, null, null);
    }

    public PlaceHolder(Guild guild) {
        this(null, guild, null);
    }

    public PlaceHolder(TextChannel textChannel) {
        this(null, null, textChannel);
    }

    public PlaceHolder(Member member, Guild guild, TextChannel textChannel) {
        this.member = member;
        this.guild = guild;
        this.textChannel = textChannel;

        this.content = "";
    }


    public PlaceHolder setContent(String content) {
        this.content = content;
        return this;
    }

    public void update(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public void update(Member member) {
        this.member = member;
    }

    public void update(Guild guild) {
        this.guild = guild;
    }

    public String transform() {
        if (this.member != null) {
            this.content = this.content.replace("%avatar_of_member%", member.getUser().getEffectiveAvatarUrl());
            this.content = this.content.replace("%member_mentioned%", member.getUser().getAsMention());
            this.content = this.content.replace("%member%", member.getEffectiveName());
            this.content = this.content.replace("%member_tag%", member.getUser().getAsTag());
            this.content = this.content.replace("%member_id%", member.getId());
        }
        if (this.guild != null) {
            this.content = this.content.replace("%guild_members%", guild.getMembers().size() + "");
            this.content = this.content.replace("%guild_name%", guild.getName());
            this.content = this.content.replace("%guild_description%", guild.getDescription() == null ? "No Description" : guild.getDescription());
        }
        if (this.textChannel != null) {
            this.content = this.content.replace("%textChannel_name%", this.textChannel.getName());
            this.content = this.content.replace("%textChannel_mentioned%", this.textChannel.getAsMention());
        }

        return content;
    }
}
