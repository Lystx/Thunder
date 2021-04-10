package io.lightning.manager.config;

import io.lightning.Lightning;
import io.lightning.utils.Utils;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Getter
public class ConfigManager {

    public ConfigManager() {
        new File("local/guilds/").mkdirs();
    }

    @SneakyThrows
    public VsonObject getConfig(Guild guild) {
        new File("local/guilds/" + guild.getId() + "/").mkdirs();
        VsonObject vsonObject = new VsonObject(new File("local/guilds/" + guild.getId() + "/settings.vson"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        if (vsonObject.isEmpty()) {
            vsonObject = load(guild, vsonObject);
            vsonObject.save();
        }
        return vsonObject;
    }

    public void setConfig(VsonObject config, Guild guild) {
        config.save(new File("local/guilds/" + guild.getId() + "/settings.vson"));
        Lightning.get().getCommandManager(guild).setPrefix(config.getString("commandPrefix"));
    }


    @SneakyThrows
    public void save(Guild guild, Member member) {
        new File("local/guilds/" + guild.getId() + "/members/").mkdirs();


        List<String> roles = Utils.toStringList(member.getRoles(), "getIdLong");

        VsonObject vsonObject = new VsonObject(new File("local/guilds/" + guild.getId() + "/members/" + member.getUser().getId() + ".data"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        vsonObject.append("roles", roles);
        vsonObject.append("data",
                new VsonObject(VsonSettings.OVERRITE_VALUES)
                .append("id", member.getId())
                .append("nickname", member.getNickname())
        );
        vsonObject.save();
    }

    @SneakyThrows
    public VsonObject load(Guild guild, Member member) {
        return new VsonObject(new File("local/guilds/" + guild.getId() + "/members/" + member.getUser().getId() + ".data"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
    }

    public VsonObject load(Guild guild, VsonObject input) {

        input.getString("commandPrefix", "!");
        input.getVson("roles", new VsonObject(VsonSettings.OVERRITE_VALUES)
                .append("muted", "roleID")
                .append("default", "roleID")
                .append("manager", "roleID")
                .append("*", "roleID")
                .append("dj", "roleID")
        );
        input.getVson("customChannel",
                new VsonObject(VsonSettings.OVERRITE_VALUES)
                .append("category", "null")
                .append("name", "Join for custom channel")
        );
        input.getVson("moderation",
                new VsonObject()
                    .append("allowLinks", false)
                    .append("allowCaps", false)
                   .append("insults", Arrays.asList("fuck", "nigga", "asshole", "bitch"))
        );
        input.getBoolean("allowDiscordLinks", false);
        input.getBoolean("allowCaps", false);
        input.getVson("welcome",
                new VsonObject()
                    .append("channel", "null")
                    .append("message",
                            new VsonObject(VsonSettings.OVERRITE_VALUES)
                            .append("title", "Lightning | Welcome")
                            .append("color", "ORANGE")
                            .append("image", "https://i.pinimg.com/originals/cd/f0/9b/cdf09b00aea778cb509aafc4cccc4e77.png")
                            .append("content", "")
                            .append("footer",
                                    new VsonObject()
                                            .append("text", "Requested by | %member%")
                                            .append("url", "%avatar_of_member%")
                            ).append("thumbnail", "https://png.pngtree.com/png-clipart/20190924/original/pngtree-lightning-button-icon-design-png-image_4816773.jpg")
                    )
        );
        return input;
    }
}
