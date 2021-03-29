package io.vera.server.packet.play;

import io.netty.buffer.ByteBuf;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.server.ui.tablist.PlayerProperty;
import io.vera.server.ui.tablist.TabListElement;
import io.vera.ui.chat.ChatComponent;
import io.vera.world.opt.GameMode;
import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

@Immutable
public abstract class PlayOutTabListItem extends PacketOut {
  private final ActionType action;
  
  private PlayOutTabListItem(ActionType action) {
    super(PlayOutTabListItem.class);
    this.action = action;
  }
  
  public void write(ByteBuf buf) {
    NetData.wvint(buf, this.action.ordinal());
    NetData.wvint(buf, getActionCount());
  }
  
  public static AddPlayer addPlayerPacket() {
    return new AddPlayer();
  }
  
  public static RemovePlayer removePlayerPacket() {
    return new RemovePlayer();
  }
  
  public static UpdateDisplayName updatePlayerPacket() {
    return new UpdateDisplayName();
  }
  
  public static UpdateGameMode updateGamemodePacket() {
    return new UpdateGameMode();
  }
  
  public static UpdateLatency updateLatencyPacket() {
    return new UpdateLatency();
  }
  
  public abstract int getActionCount();
  
  @NotThreadSafe
  public static class AddPlayer extends PlayOutTabListItem {
    private final Collection<PlayerData> additions = new HashSet<>();
    
    public AddPlayer() {
      super(ActionType.ADD_PLAYER);
    }
    
    public void addPlayer(UUID uuid, String name, GameMode gameMode, int ping, ChatComponent displayName) {
      addPlayer(uuid, name, gameMode, ping, displayName, null);
    }
    
    public void addPlayer(UUID uuid, String name, GameMode gameMode, int ping, ChatComponent displayName, List<PlayerProperty> properties) {
      PlayerData playerData = new PlayerData(uuid, name, gameMode, ping, displayName, properties);
      this.additions.add(playerData);
    }
    
    public void addPlayer(TabListElement element) {
      PlayerData playerData = new PlayerData(element.getUuid(), element.getName(), element.getGameMode(), element.getPing(), element.getDisplayName(), element.getProperties());
      this.additions.add(playerData);
    }
    
    public void write(ByteBuf buf) {
      super.write(buf);
      this.additions.forEach(data -> {
            buf.writeLong(data.uuid.getMostSignificantBits());
            buf.writeLong(data.uuid.getLeastSignificantBits());
            NetData.wstr(buf, data.name);
            NetData.wvint(buf, (data.properties != null) ? data.properties.size() : 0);
            if (data.properties != null)
              data.properties.forEach(()); 
            NetData.wvint(buf, data.gameMode.asInt());
            NetData.wvint(buf, data.ping);
            buf.writeBoolean((data.displayName != null));
            if (data.displayName != null)
              NetData.wstr(buf, data.displayName.toString()); 
          });
    }
    
    public int getActionCount() {
      return this.additions.size();
    }
    
    @Immutable
    private class PlayerData {
      private final UUID uuid;
      
      private final String name;
      
      private final GameMode gameMode;
      
      private final int ping;
      
      private final ChatComponent displayName;
      
      private final List<PlayerProperty> properties;
      
      public boolean equals(Object o) {
        if (o == this)
          return true; 
        if (!(o instanceof PlayerData))
          return false; 
        PlayerData other = (PlayerData)o;
        if (!other.canEqual(this))
          return false; 
        Object this$uuid = this.uuid, other$uuid = other.uuid;
        return !((this$uuid == null) ? (other$uuid != null) : !this$uuid.equals(other$uuid));
      }
      
      protected boolean canEqual(Object other) {
        return other instanceof PlayerData;
      }
      
      public int hashCode() {
        int PRIME = 59;
        result = 1;
        Object $uuid = this.uuid;
        return result * 59 + (($uuid == null) ? 43 : $uuid.hashCode());
      }
      
      @ConstructorProperties({"uuid", "name", "gameMode", "ping", "displayName", "properties"})
      public PlayerData(UUID uuid, String name, GameMode gameMode, int ping, ChatComponent displayName, List<PlayerProperty> properties) {
        this.uuid = uuid;
        this.name = name;
        this.gameMode = gameMode;
        this.ping = ping;
        this.displayName = displayName;
        this.properties = properties;
      }
    }
  }
  
  @NotThreadSafe
  public static class RemovePlayer extends PlayOutTabListItem {
    private final Collection<UUID> removals = new HashSet<>();
    
    public RemovePlayer() {
      super(ActionType.REMOVE_PLAYER);
    }
    
    public void removePlayer(UUID uuid) {
      this.removals.add(uuid);
    }
    
    public void write(ByteBuf buf) {
      super.write(buf);
      this.removals.forEach(uuid -> {
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
          });
    }
    
    public int getActionCount() {
      return this.removals.size();
    }
  }
  
  @NotThreadSafe
  public static class UpdateGameMode extends PlayOutTabListItem {
    private final Map<UUID, GameMode> updates = new HashMap<>();
    
    public UpdateGameMode() {
      super(ActionType.UPDATE_GAMEMODE);
    }
    
    public void update(UUID uuid, GameMode gameMode) {
      this.updates.put(uuid, gameMode);
    }
    
    public void write(ByteBuf buf) {
      super.write(buf);
      this.updates.forEach((uuid, gameMode) -> {
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
            NetData.wvint(buf, gameMode.asInt());
          });
    }
    
    public int getActionCount() {
      return this.updates.size();
    }
  }
  
  @NotThreadSafe
  public static class UpdateLatency extends PlayOutTabListItem {
    private final Map<UUID, Integer> updates = new HashMap<>();
    
    public UpdateLatency() {
      super(ActionType.UPDATE_LATENCY);
    }
    
    public void update(UUID uuid, int latency) {
      this.updates.put(uuid, Integer.valueOf(latency));
    }
    
    public void write(ByteBuf buf) {
      super.write(buf);
      this.updates.forEach((uuid, latency) -> {
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
            NetData.wvint(buf, latency.intValue());
          });
    }
    
    public int getActionCount() {
      return this.updates.size();
    }
  }
  
  @NotThreadSafe
  public static class UpdateDisplayName extends PlayOutTabListItem {
    private final Collection<PlayerData> updates = new HashSet<>();
    
    public UpdateDisplayName() {
      super(ActionType.UPDATE_DISPLAY_NAME);
    }
    
    public void update(UUID uuid, ChatComponent displayName) {
      PlayerData data = new PlayerData(uuid, displayName);
      this.updates.add(data);
    }
    
    public void write(ByteBuf buf) {
      super.write(buf);
      this.updates.forEach(data -> {
            buf.writeLong(data.uuid.getMostSignificantBits());
            buf.writeLong(data.uuid.getLeastSignificantBits());
            buf.writeBoolean((data.displayName != null));
            if (data.displayName != null)
              NetData.wstr(buf, data.displayName.toString()); 
          });
    }
    
    public int getActionCount() {
      return this.updates.size();
    }
    
    @Immutable
    private final class PlayerData {
      private final UUID uuid;
      
      private final ChatComponent displayName;
      
      public boolean equals(Object o) {
        if (o == this)
          return true; 
        if (!(o instanceof PlayerData))
          return false; 
        PlayerData other = (PlayerData)o;
        Object this$uuid = this.uuid, other$uuid = other.uuid;
        return !((this$uuid == null) ? (other$uuid != null) : !this$uuid.equals(other$uuid));
      }
      
      public int hashCode() {
        int PRIME = 59;
        result = 1;
        Object $uuid = this.uuid;
        return result * 59 + (($uuid == null) ? 43 : $uuid.hashCode());
      }
      
      @ConstructorProperties({"uuid", "displayName"})
      public PlayerData(UUID uuid, ChatComponent displayName) {
        this.uuid = uuid;
        this.displayName = displayName;
      }
    }
  }
  
  @Immutable
  public enum ActionType {
    ADD_PLAYER, UPDATE_GAMEMODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER;
  }
}
