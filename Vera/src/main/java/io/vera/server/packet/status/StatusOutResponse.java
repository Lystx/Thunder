
package io.vera.server.packet.status;

import io.netty.buffer.ByteBuf;
import io.vera.event.server.ServerPingEvent;
import io.vera.server.net.NetData;
import io.vera.server.packet.PacketOut;
import io.vera.logger.Logger;
import io.vson.enums.FileFormat;

import javax.annotation.concurrent.Immutable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.StandardWatchEventKinds.*;

@Immutable
public final class StatusOutResponse extends PacketOut {

    public static final String MC_VERSION = "1.8.9";

    public static final int PROTOCOL_VERSION = 47;

    static final AtomicReference<String> b64icon = new AtomicReference<>();
    private static final Logger logger = Logger.get("Server Icon File Watcher");
    private static final Path iconPath = Paths.get("server-icon.png");

    private static final AtomicBoolean init = new AtomicBoolean();

    public static void init() {
        if (!init.compareAndSet(false, true))
            return;
        try {
            loadIcon();
        } catch (IOException ex) {
            logger.log("No server-icon.png!");
        }

        String userDir = System.getProperty("user.dir");
        Thread watcherThread = new Thread(() -> {
            try {
                Path dir = Paths.get(userDir);
                WatchService service = dir.getFileSystem().newWatchService();
                WatchKey watchKey = dir.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                while (true) {
                    try {
                        WatchKey eventKey = service.take();
                        logger.log("Got server icon watcher key!");
                        if (eventKey != watchKey) {
                            logger.warn(String.format("unexpected watch key: %s. expected %s%n", eventKey, watchKey));
                            break;
                        }
                        eventKey.pollEvents().forEach(e -> {
                            if (!e.context().equals(iconPath))
                                return;
                            logger.log("server-icon.png fired an event: " + e.kind());
                            if (e.kind() == ENTRY_CREATE || e.kind() == ENTRY_MODIFY) {
                                try {
                                    loadIcon();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            } else if (e.kind() == ENTRY_DELETE) {
                                b64icon.set(null);
                            }
                        });

                        if (!eventKey.reset()) {
                            logger.log("Server icon watch key no longer valid!");
                            break;
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "VRA - Icon Watcher");
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    private static void loadIcon() throws IOException {
        BufferedImage image = ImageIO.read(iconPath.toFile());

        if (image.getWidth() != 64 || image.getHeight() != 64){
            BufferedImage resizedImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resizedImage.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(image, 0, 0, 64, 64, null);
            g.dispose();
            image = resizedImage;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] data = baos.toByteArray();
        String b64 = Base64.getEncoder().encodeToString(data);
        b64 = "data:image/png;base64," + b64;
        b64icon.set(b64);
    }

    private final ServerPingEvent event;

    public StatusOutResponse(ServerPingEvent event) {
        super(StatusOutResponse.class);
        this.event = event;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wstr(buf, event.getResponse().asJson().toString(FileFormat.RAW_JSON));
    }
}
