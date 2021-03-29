package io.vera.server;

import io.netty.util.concurrent.Future;
import io.vera.command.CommandHandler;
import io.vera.command.CommandSource;
import io.vera.command.CommandSourceType;
import io.vera.logger.Logger;
import io.vera.plugin.PluginLoader;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.concurrent.VeraTick;
import io.vera.server.config.OpsList;
import io.vera.server.config.ServerConfig;
import io.vera.server.net.NetServer;
import io.vera.server.player.VeraPlayer;
import io.vera.server.plugin.EventManager;
import io.vera.server.world.World;
import io.vera.server.world.WorldLoader;
import io.vera.ui.chat.ChatComponent;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class VeraServer implements CommandSource {
  private static volatile VeraServer instance;
  
  private final ServerConfig config;
  
  private final OpsList opsList;
  
  private final Logger logger;
  
  private final NetServer server;
  
  private final VeraTick tick;
  
  public static VeraServer getInstance() {
    return instance;
  }
  
  public ServerConfig getConfig() {
    return this.config;
  }
  
  public OpsList getOpsList() {
    return this.opsList;
  }
  
  public Logger getLogger() {
    return this.logger;
  }
  
  private final PluginLoader pluginLoader = new PluginLoader();
  
  private final CommandHandler commandHandler = new CommandHandler();
  
  private boolean shutdownState;
  
  public boolean isShutdownState() {
    return this.shutdownState;
  }
  
  private VeraServer(ServerConfig config, Logger console, NetServer server, OpsList opsList) {
    this.config = config;
    this.logger = console;
    this.server = server;
    this.opsList = opsList;
    this.tick = new VeraTick(console);
  }
  
  public static VeraServer init(ServerConfig config, Logger console, NetServer net, OpsList list) throws IllegalStateException {
    VeraServer server = new VeraServer(config, console, net, list);
    if (instance == null) {
      instance = server;
      server.tick.start();
      return server;
    } 
    throw new IllegalStateException("Server is already initialized");
  }
  
  public static ServerConfig cfg() {
    return instance.getConfig();
  }
  
  public String getIp() {
    return this.config.ip();
  }
  
  public int getPort() {
    return this.config.port();
  }
  
  public Collection<UUID> getOps() {
    return Collections.unmodifiableCollection(this.opsList.getOps());
  }
  
  public Collection<VeraPlayer> getPlayers() {
    return Collections.unmodifiableCollection(VeraPlayer.getPlayers().values());
  }
  
  public WorldLoader getWorldLoader() {
    return WorldLoader.getInstance();
  }
  
  public EventManager getEventController() {
    return EventManager.getInstance();
  }
  
  public PluginLoader getPluginLoader() {
    return this.pluginLoader;
  }
  
  public CommandHandler getCommandHandler() {
    return this.commandHandler;
  }
  
  public void reload() {
    this.logger.warn("SERVER RELOADING...");
    try {
      this.logger.log("Reloading server configs...");
      this.config.load();
      this.opsList.load();
      this.logger.log("Reloading plugins...");
      this.pluginLoader.reload();
    } catch (IOException e) {
      return;
    } 
    this.logger.success("Server has reloaded successfully.");
  }
  
  public void shutdown() {
    this.logger.warn("SERVER SHUTTING DOWN...");
    this.shutdownState = true;
    try {
      this.logger.log("Unloading plugins...");
      if (!this.pluginLoader.unloadAll())
        this.logger.error("Unloading plugins failed..."); 
      this.tick.interrupt();
      this.logger.log("Kicking players... ");
      int removed = 0;
      Semaphore sem = new Semaphore(0);
      for (VeraPlayer player : VeraPlayer.getPlayers().values()) {
        removed++;
        player.net().disconnect(ChatComponent.text("Server closed")).addListener(future -> sem.release());
      } 
      sem.tryAcquire(removed, 10L, TimeUnit.SECONDS);
      this.logger.log("Closing network connections...");
      this.server.shutdown();
      for (World world : WorldLoader.getInstance().getWorlds().values()) {
        this.logger.log("Saving world \"" + world.getName() + "\"...");
        world.save();
      } 
      this.logger.log("Saving server config...");
      this.config.save();
      this.logger.log("Shutting down server process...");
      ServerThreadPool.shutdownAll();
    } catch (IOException|InterruptedException e) {
      return;
    } 
    this.logger.success("Server has shutdown successfully.");
    System.exit(0);
  }
  
  public void runCommand(String command) {
    this.logger.log("Server command issued by console: /" + command);
    try {
      if (!((Boolean)ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> Boolean.valueOf(this.commandHandler.dispatch(command, this))).get()).booleanValue())
        this.logger.log("No command \"" + command.split(" ")[0] + "\" found"); 
    } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public void sendMessage(ChatComponent text) {
    StringBuilder builder = new StringBuilder();
    if (text.getColor() != null)
      builder.append(text.getColor()); 
    builder.append(text.getText());
    for (ChatComponent e : text.getExtra()) {
      if (e.getColor() != null)
        builder.append(e.getColor()); 
      builder.append(e.getText());
    } 
    this.logger.log(builder.toString());
  }
  
  public CommandSourceType getCmdType() {
    return CommandSourceType.CONSOLE;
  }
  
  public boolean hasPermission(String permission) {
    return true;
  }
  
  public void addPermission(String perm) {}
  
  public boolean removePermission(String perm) {
    return false;
  }
  
  public void setOp(boolean op) {}
  
  public boolean isOp() {
    return true;
  }
}
