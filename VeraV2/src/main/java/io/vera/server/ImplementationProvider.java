package io.vera.server;

import io.vera.Impl;
import io.vera.entity.living.Player;
import io.vera.inventory.Inventory;
import io.vera.inventory.InventoryType;
import io.vera.inventory.Item;
import io.vera.inventory.Substance;
import io.vera.logger.LogHandler;
import io.vera.logger.Logger;
import io.vera.meta.ItemMeta;
import io.vera.plugin.channel.PluginChannel;
import io.vera.plugin.channel.SimpleChannelListener;
import io.vera.server.inventory.VeraInventory;
import io.vera.server.inventory.VeraItem;
import io.vera.server.logger.InfoLogger;
import io.vera.server.logger.LoggerHandlers;
import io.vera.server.logger.PipelinedLogger;
import io.vera.server.player.VeraPlayer;
import io.vera.server.plugin.VeraPluginChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ImplementationProvider implements Impl.Provider {
  private final PipelinedLogger head;
  
  private final LoggerHandlers handlers;
  
  public ImplementationProvider(PipelinedLogger head) {
    this.head = head;
    for (PipelinedLogger logger = head; logger.next() != null; logger = logger.next()) {
      if (logger.getClass().equals(LoggerHandlers.class)) {
        this.handlers = (LoggerHandlers)logger;
        return;
      } 
    } 
    throw new IllegalStateException("No handler found");
  }
  
  public VeraServer getServer() {
    return VeraServer.getInstance();
  }
  
  public Logger newLogger(String s) {
    return InfoLogger.get(this.head, s);
  }
  
  public void attachHandler(Logger logger, LogHandler handler) {
    if (logger == null) {
      this.handlers.handlers().add(handler);
    } else {
      InfoLogger info = (InfoLogger)logger;
      info.handlers().add(handler);
    } 
  }
  
  public boolean removeHandler(Logger logger, LogHandler handler) {
    if (logger == null)
      return this.handlers.handlers().remove(handler); 
    InfoLogger info = (InfoLogger)logger;
    return info.handlers().remove(handler);
  }
  
  public Inventory newInventory(InventoryType type, int slots) {
    return (Inventory)new VeraInventory(type, slots);
  }
  
  public Item newItem(Substance substance, int count, byte damage, ItemMeta meta) {
    return (Item)new VeraItem(substance, count, damage, meta);
  }
  
  @Nonnull
  public Map<String, Player> findByName(String name) {
    String top = (String)VeraPlayer.getPlayerNames().ceilingKey(name.toUpperCase());
    StringBuilder last = new StringBuilder(name.toLowerCase());
    for (int i = 0; i < 16 - name.length(); i++)
      last.append('_'); 
    String bot = (String)VeraPlayer.getPlayerNames().floorKey(last.toString());
    if (top == null || bot == null)
      return Collections.emptyMap(); 
    if (top.equals(bot))
      return Collections.singletonMap(top, (Player)VeraPlayer.getPlayerNames().get(top)); 
    return Collections.unmodifiableMap(VeraPlayer.getPlayerNames().subMap(bot, true, top, true));
  }
  
  @Nonnull
  public Map<String, Player> findByNameFuzzy(String filter) {
    return (Map<String, Player>)VeraPlayer.getPlayerNames().entrySet().stream()
      .filter(p -> {
          String f = filter;
          String n = (String)p.getKey();
          while (n.length() >= f.length()) {
            if (f.isEmpty() || n.isEmpty())
              return true; 
            int index = n.indexOf(f.charAt(0));
            if (index < 0)
              break; 
            n = n.substring(index + 1);
            f = f.substring(1);
          } 
          return false;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
  
  @Nullable
  public Player getByUuid(UUID uuid) {
    return (Player)VeraPlayer.getPlayers().get(uuid);
  }
  
  @Nullable
  public Player getByName(String name) {
    return (Player)VeraPlayer.getPlayerNames().get(name);
  }
  
  public PluginChannel open(String name, Player... targets) {
    if (name.length() > 20)
      throw new IllegalArgumentException("Cannot have channel name > 20"); 
    PluginChannel channel = VeraPluginChannel.getChannel(name, VeraPluginChannel::new);
    channel.addRecipient(targets);
    return channel;
  }
  
  public PluginChannel open(String name, Collection<? extends Player> players) {
    if (name.length() > 20)
      throw new IllegalArgumentException("Cannot have channel name > 20"); 
    PluginChannel channel = VeraPluginChannel.getChannel(name, VeraPluginChannel::new);
    channel.addRecipient(players);
    return channel;
  }
  
  public PluginChannel openAll(String name) {
    if (name.length() > 20)
      throw new IllegalArgumentException("Cannot have channel name > 20"); 
    return VeraPluginChannel.getChannel(name, io.vera.server.plugin.PluginAllChannel::new);
  }
  
  public PluginChannel tryOpen(String name) {
    if (name.length() > 20)
      throw new IllegalArgumentException("Cannot have channel name > 20"); 
    Map.Entry<String, VeraPlayer> entry = VeraPlayer.getPlayerNames().firstEntry();
    if (entry == null)
      return null; 
    return open(name, new Player[] { (Player)entry.getValue() });
  }
  
  public PluginChannel get(String name) {
    return VeraPluginChannel.get(name);
  }
  
  public void register(SimpleChannelListener listener) {
    VeraPluginChannel.register(listener);
  }
  
  public boolean unregister(Class<? extends SimpleChannelListener> cls) {
    return VeraPluginChannel.unregister(cls);
  }
}
