
package io.vera.server.command;

import io.vera.world.other.Position;
import io.vera.inventory.Substance;
import io.vera.doc.Debug;
import io.vera.server.net.NetClient;
import io.vera.server.ui.BossBar;
import io.vera.server.ui.Title;
import io.vera.server.world.Chunk;
import io.vera.ui.bossbar.BossBarColor;
import io.vera.ui.bossbar.BossBarDivision;
import io.vera.ui.chat.ChatColor;
import io.vera.ui.chat.ChatComponent;
import io.vera.ui.chat.HoverEvent;
import io.vera.command.Command;
import io.vera.command.CommandListener;
import io.vera.command.CommandSource;
import io.vera.command.CommandSourceType;
import io.vera.command.annotation.AllowedSourceTypes;
import io.vera.command.annotation.PermissionRequired;
import io.vera.inventory.Item;
import io.vera.server.packet.play.PlayOutChunk;
import io.vera.server.packet.play.PlayOutDestroyEntities;
import io.vera.server.packet.play.PlayOutTabListItem;
import io.vera.server.player.RecipientSelector;
import io.vera.server.player.VeraPlayer;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;

@Immutable
@Debug
public class DebugCommand implements CommandListener {

    @Command(name = "debug", help = "/debug <chunks|bossbars|title|cleartitle|chat|rain|change>", desc = "Secret debug command for devs")
    @AllowedSourceTypes(CommandSourceType.PLAYER)
    @PermissionRequired("trident.debug")
    public void debug(CommandSource source, String[] args, String mode) {
        VeraPlayer player = (VeraPlayer) source;
        NetClient client = player.net();

        if (mode.equals("chunks")) {
            Position playerPosition = player.getPosition();
            int chunkLoadRadius = 3;

            for (int x = playerPosition.getChunkX() - chunkLoadRadius; x <= playerPosition.getChunkX() + chunkLoadRadius; x++) {
                for (int z = playerPosition.getChunkZ() - chunkLoadRadius; z <= playerPosition.getChunkZ() + chunkLoadRadius; z++) {
                    Chunk chunk = (Chunk) playerPosition.getWorld().getChunkAt(x, z);
                    client.sendPacket(new PlayOutChunk(chunk));
                }
            }
        } else if (mode.equals("bossbars")) {
            int i = 0;
            for (String word : "I hate my life".split(" ")) {
                BossBar bb = new BossBar();

                bb.setTitle(ChatComponent.text(word).setColor(ChatColor.of((char) ('a' + i))));
                bb.setColor(BossBarColor.values()[i]);
                bb.setDivision(BossBarDivision.values()[i++]);
                bb.setHealth(i * .25f);
                bb.setDarkenSky(false);
                bb.setDragonBar(false);

                player.addBossBar(bb);
            }
        } else if (mode.equals("title")) {
            Title title = new Title();

            title.setHeader(ChatComponent.create().setColor(ChatColor.AQUA).setText("henlo player"));
            title.setSubtitle(ChatComponent.create().setColor(ChatColor.GOLD).setText("hello u STINKY PLAYER"));
            title.setFadeIn(0);
            title.setStay(600);
            title.setFadeOut(0);

            player.sendTitle(title);
        } else if (mode.equals("cleartitle")) {
            player.resetTitle();
        } else if (mode.equals("chat")) {
            player.sendMessage(ChatComponent.create().setText("What is this").setHoverEvent(
                    HoverEvent.item(Item.newItem(Substance.STONE, 30, (byte) 1))));
        } else if (mode.equals("rain")) {
            player.getWorld().getWeather().beginRaining();
            player.getWorld().getWeather().beginThunder();
        } else if (mode.equals("change")) {
            PlayOutTabListItem.RemovePlayer removePlayer = PlayOutTabListItem.removePlayerPacket();
            removePlayer.removePlayer(player.getUuid());

            PlayOutTabListItem.AddPlayer addPlayer = PlayOutTabListItem.addPlayerPacket();
            addPlayer.addPlayer(player.getUuid(), "Im_&*!@#$``~", player.getGameMode(), 0, player.getTabListName(),
                    Collections.emptyList());

            RecipientSelector.whoCanSee(player, false, new PlayOutDestroyEntities(Collections.singletonList(player)),
                    addPlayer);
            RecipientSelector.whoCanSee(player, true, player.getSpawnPacket());
        }
    }
}