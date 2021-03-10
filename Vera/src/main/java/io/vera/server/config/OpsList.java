
package io.vera.server.config;

import lombok.Getter;
import io.vera.server.VeraServer;
import io.vera.server.player.VeraPlayer;
import io.vera.util.Misc;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ThreadSafe
public class OpsList extends Config {
    public static final Path PATH = Misc.HOME_PATH.resolve("ops.vson");
    private static final String OPS_KEY = "ops";

    @Getter
    private final Set<UUID> ops = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private OpsList() {
        super(PATH);
    }

    public static OpsList init(boolean needsInit) throws IOException {
        OpsList list = new OpsList();
        if (needsInit) {
            Files.write(list.getPath(), "{}".getBytes());
        }
        list.load();
        return list;
    }

    public void addOp(UUID uuid) {
        this.ops.add(uuid);
        this.set(OPS_KEY, this.ops.stream().map(UUID::toString).collect(Collectors.toList()));
        try {
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VeraServer.getInstance().getLogger().log(VeraPlayer.getPlayers().get(uuid).getName() + " [" + uuid + "] has been opped");
    }

    public void removeOp(UUID uuid) {
        this.ops.remove(uuid);
        this.set(OPS_KEY, this.ops);
        try {
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VeraServer.getInstance().getLogger().log(VeraPlayer.getPlayers().get(uuid).getName() +
                " [" + uuid + "] has been deopped");
    }

    @Override
    public void load() throws IOException {
        super.load();
        if (this.hasKey(OPS_KEY)) {
            this.getCollection(OPS_KEY, new AbstractSet<String>() {
                @Override
                public boolean add(String c) {
                    OpsList.this.ops.add(UUID.fromString(c));
                    return true;
                }

                @Override
                public Iterator<String> iterator() {
                    return null;
                }

                @Override
                public int size() {
                    return 0;
                }
            });
        } else {
            this.set(OPS_KEY, this.ops);
            this.save();
        }
    }
}