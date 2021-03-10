
package io.vera.server;

import io.vera.server.command.*;
import io.vera.server.config.ConfigIo;
import io.vera.server.config.OpsList;
import io.vera.server.config.ServerConfig;
import io.vera.server.logger.InfoLogger;
import io.vera.server.logger.PipelinedLogger;
import io.vera.server.net.NetServer;
import io.vera.server.world.WorldLoader;
import io.vera.Impl;
import io.vera.command.CommandHandler;
import io.vera.logger.Logger;
import io.vera.plugin.Plugin;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.packet.status.StatusOutResponse;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import javax.annotation.concurrent.Immutable;
import java.nio.file.Files;

import static io.vera.server.command.DummyPlugin.MINECRAFT_INST;

@Immutable
public final class VeraBoostrap {

    private static final String VERBOSE = "-v";

    private static final String NO_EPOLL = "-noepoll";

    private VeraBoostrap() {
    }

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
            if (s.equals(VERBOSE)) {
                verbose = true;
                continue;
            }

            if (s.equals(NO_EPOLL)) {
                noEpoll = true;
                continue;
            }

            System.out.println("Unrecognized option: " + s + ", ignoring.");
        }
        PipelinedLogger internal = PipelinedLogger.init(verbose);
        Logger logger = InfoLogger.get(internal, "Server");

        logger.log("Vera Minecraft protocol for " + StatusOutResponse.MC_VERSION);
        logger.log("Vera Minecraft protocol ID " + StatusOutResponse.PROTOCOL_VERSION);

        if (!Files.exists(ServerConfig.PATH)) {
            ConfigIo.exportResource(ServerConfig.PATH, "/server.vson");
            ConfigIo.exportResource(ServerConfig.PATH, "/server-icon.png");
        }

        if (!Files.exists(Plugin.PLUGIN_DIR)) {
            Files.createDirectory(Plugin.PLUGIN_DIR);
        }

        boolean initOpsList = false;
        if (!Files.exists(OpsList.PATH)) {
            Files.createFile(OpsList.PATH);
            initOpsList = true;
        }

        ServerConfig config = ServerConfig.init();
        OpsList opsList = OpsList.init(initOpsList);

        String address = config.ip();
        int port = config.port();
        
        NetServer server = NetServer.init(address, port, config.useNative() && !noEpoll);

        ImplementationProvider impl = new ImplementationProvider(internal);
        Impl.setImpl(impl);

        ServerThreadPool.init();
        VeraServer trident = VeraServer.init(config, logger, server, opsList);

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> {
            CommandHandler h = trident.getCommandHandler();
            h.register(MINECRAFT_INST, new StopCommand());
            h.register(MINECRAFT_INST, new KickCommand());
            h.register(MINECRAFT_INST, new TeleportCommand());
            h.register(MINECRAFT_INST, new SayCommand());
            h.register(MINECRAFT_INST, new HelpCommand());
            h.register(MINECRAFT_INST, new OpCommand());
            h.register(MINECRAFT_INST, new DeopCommand());
            trident.getPluginLoader().loadAll();

        }).get();

        logger.log("Loading worlds...");
        WorldLoader.getInstance().loadAll();

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> {
            for (Plugin plugin : trident.getPluginLoader().getLoaded().values()) {
                plugin.setup();
            }
        }).get();
        logger.log(String.format("Netty Server is listening on %s:%s", address, port));
        server.setup();

        StatusOutResponse.init();

        LineReader reader = LineReaderBuilder.
                builder().
                appName("Vera").
                terminal(TerminalBuilder.
                        builder().
                        dumb(true).
                        jansi(true).
                        build()).
                build();

        while (true) {
            String line = reader.readLine("$ ");
            if (line.isEmpty()) {
                continue;
            }

            trident.runCommand(line);

            if (trident.isShutdownState()) {
                return;
            }
        }
    }
}
