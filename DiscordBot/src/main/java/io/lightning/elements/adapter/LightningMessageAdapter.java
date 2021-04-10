package io.lightning.elements.adapter;

import io.lightning.utils.Utils;
import io.vson.VsonValue;
import io.vson.annotation.other.VsonAdapter;
import io.vson.elements.object.VsonObject;
import io.vson.manage.vson.VsonWriter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LightningMessageAdapter implements VsonAdapter<MessageEmbed> {

    @Override
    public VsonValue write(MessageEmbed embed, VsonWriter vsonWriter) {
        VsonObject vsonObject = new VsonObject();
        vsonObject.append("content", embed.getDescription());
        if (embed.getColor() != null) {
            vsonObject.append("color", Utils.colorToString(embed.getColor()));
        } else {
            vsonObject.append("color", "null");
        }
        vsonObject.append("title", embed.getTitle());
        if (embed.getThumbnail() != null && embed.getThumbnail().getUrl() != null) {
            vsonObject.append("thumbnail", embed.getThumbnail().getUrl());
        } else {
            vsonObject.append("thumbnail", "null");
        }
        vsonObject.append("footer", new VsonObject().append("url", embed.getFooter().getIconUrl()).append("text", embed.getFooter().getText()));
        return vsonObject;
    }


    @Override
    public MessageEmbed read(VsonValue vsonValue) {
        VsonObject vsonObject = (VsonObject) vsonValue;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!vsonObject.getString("color").equalsIgnoreCase("null")) {
            embedBuilder.setColor(Utils.stringToColor(vsonObject.getString("color")));
        }
        if (vsonObject.getString("title") != null) {
            embedBuilder.setTitle(vsonObject.getString("title"));
        }
        if (vsonObject.getString("thumbnail") != null) {
            embedBuilder.setThumbnail(vsonObject.getString("thumbnail"));
        }

        VsonObject footer = vsonObject.getVson("footer");
        if (footer.getString("url") != null) {
            embedBuilder.setFooter(footer.getString("url"), vsonObject.getString("text"));
        } else if (footer.getString("text") != null) {
            embedBuilder.setFooter(vsonObject.getString("text"));
        }
        embedBuilder.setDescription(vsonObject.getString("content"));
        return embedBuilder.build();
    }

    @Override
    public Class<MessageEmbed> getTypeClass() {
        return MessageEmbed.class;
    }
}
