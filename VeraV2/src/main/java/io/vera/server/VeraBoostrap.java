package io.vera.server;

import io.vera.Impl;
import io.vera.command.CommandHandler;
import io.vera.command.CommandListener;
import io.vera.logger.Logger;
import io.vera.plugin.Plugin;
import io.vera.server.command.DeopCommand;
import io.vera.server.command.DummyPlugin;
import io.vera.server.command.HelpCommand;
import io.vera.server.command.KickCommand;
import io.vera.server.command.OpCommand;
import io.vera.server.command.SayCommand;
import io.vera.server.command.StopCommand;
import io.vera.server.command.TeleportCommand;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.config.ConfigIo;
import io.vera.server.config.OpsList;
import io.vera.server.config.ServerConfig;
import io.vera.server.logger.InfoLogger;
import io.vera.server.logger.PipelinedLogger;
import io.vera.server.net.NetServer;
import io.vera.server.packet.status.StatusOutResponse;
import io.vera.server.world.WorldLoader;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import javax.annotation.concurrent.Immutable;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

@Immutable
public final class VeraBoostrap {
  private static final String VERBOSE = "-v";
  
  private static final String NO_EPOLL = "-noepoll";
  
  public static void main(String[] args) {
    try {
      start(args);
    } catch (Exception e) {
      System.exit(1);
    } 
  }
  
  private static void start(String[] args) throws Exception {
    boolean verbose = false;
    boolean noEpoll = false;
    for (String s : args) {
      if (s.equals("-v")) {
        verbose = true;
      } else if (s.equals("-noepoll")) {
        noEpoll = true;
      } else {
        System.out.println("Unrecognized option: " + s + ", ignoring.");
      } 
    } 
    PipelinedLogger internal = PipelinedLogger.init(verbose);
    Logger logger = InfoLogger.get(internal, "Server");
    logger.log("Vera Minecraft protocol for 1.8.9");
    logger.log("Vera Minecraft protocol ID 47");
    if (!Files.exists(ServerConfig.PATH, new java.nio.file.LinkOption[0])) {
      ConfigIo.exportResource(ServerConfig.PATH, "/server.vson");
      ConfigIo.exportResource(ServerConfig.PATH, "/server-icon.png");
    } 
    if (!Files.exists(Plugin.PLUGIN_DIR, new java.nio.file.LinkOption[0]))
      Files.createDirectory(Plugin.PLUGIN_DIR, (FileAttribute<?>[])new FileAttribute[0]); 
    boolean initOpsList = false;
    if (!Files.exists(OpsList.PATH, new java.nio.file.LinkOption[0])) {
      Files.createFile(OpsList.PATH, (FileAttribute<?>[])new FileAttribute[0]);
      initOpsList = true;
    } 
    ServerConfig config = ServerConfig.init();
    OpsList opsList = OpsList.init(initOpsList);
    String address = config.ip();
    int port = config.port();
    NetServer server = NetServer.init(address, port, (config.useNative() && !noEpoll));
    ImplementationProvider impl = new ImplementationProvider(internal);
    Impl.setImpl(impl);
    ServerThreadPool.init();
    VeraServer trident = VeraServer.init(config, logger, server, opsList);
    ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> {
          CommandHandler h = trident.getCommandHandler();
          h.register(DummyPlugin.MINECRAFT_INST, (CommandListener)new StopCommand());
          h.register(DummyPlugin.MINECRAFT_INST, (CommandListener)new KickCommand());
          h.register(DummyPlugin.MINECRAFT_INST, (CommandListener)new TeleportCommand());
          h.register(DummyPlugin.MINECRAFT_INST, (CommandListener)new SayCommand());
          h.register(DummyPlugin.MINECRAFT_INST, (CommandListener)new HelpCommand());
          h.register(DummyPlugin.MINECRAFT_INST, (CommandListener)new OpCommand());
          h.register(DummyPlugin.MINECRAFT_INST, (CommandListener)new DeopCommand());
          trident.getPluginLoader().loadAll();
        }).get();
    logger.log("Loading worlds...");
    WorldLoader.getInstance().loadAll();
    ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> {
          for (Plugin plugin : trident.getPluginLoader().getLoaded().values())
            plugin.setup(); 
        }).get();
    logger.log(String.format("Netty Server is listening on %s:%s", new Object[] { address, Integer.valueOf(port) }));
    server.setup();
    StatusOutResponse.init();
    LineReader reader = LineReaderBuilder.builder().appName("Vera").terminal(TerminalBuilder.builder().dumb(true).jansi(true).build()).build();
    while (true) {
      String line = reader.readLine("$ ");
      if (line.isEmpty())
        continue; 
      trident.runCommand(line);
      if (trident.isShutdownState())
        break; 
    } 
  }
}
