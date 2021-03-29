
package io.vera.server.player;

import io.vera.server.VeraServer;
import io.vera.server.entity.VeraEntity;
import io.vera.server.inventory.VeraInventory;
import io.vera.server.plugin.VeraPluginChannel;
import io.vera.server.ui.BossBar;
import io.vera.server.ui.Title;
import io.vera.server.ui.tablist.GlobalTabList;
import io.vera.server.ui.tablist.PlayerProperty;
import io.vera.server.ui.tablist.TabList;
import io.vera.server.world.Chunk;
import io.vera.server.world.World;
import io.vera.world.other.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.vera.command.CommandSourceType;
import io.vera.doc.Policy;
import io.vera.entity.living.Player;
import io.vera.event.player.PlayerChatEvent;
import io.vera.event.player.PlayerJoinEvent;
import io.vera.event.player.PlayerQuitEvent;
import io.vera.inventory.Inventory;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.entity.meta.EntityMetaType;
import io.vera.server.inventory.VeraPlayerInventory;
import io.vera.server.net.NetClient;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.login.Login;
import io.vera.server.packet.play.*;
import io.vera.server.ui.tablist.TabListElement;
import io.vera.ui.chat.*;
import io.vera.world.IntPair;
import io.vera.world.opt.GameMode;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


@ToString(of = "name")
@ThreadSafe
@EntityMetaType(VeraPlayerMeta.class)
public class VeraPlayer extends VeraEntity implements Player {

    @Getter
    private static final Map<UUID, VeraPlayer> players = new ConcurrentHashMap<>();
 
    @Getter
    private static final ConcurrentSkipListMap<String, VeraPlayer> playerNames =
            new ConcurrentSkipListMap<>((c0, c1) -> {
                int l0 = c0.length();
                int l1 = c1.length();

                if (l0 == 0 && l1 > 0) {
                    return -1;
                } else if (l1 == 0 && l0 > 0) {
                    return 1;
                }

                for (int i = 0, j = Math.min(l0, l1); i < j; i++) {
                    char c0i = c0.charAt(i);
                    char c1i = c1.charAt(i);

                    boolean d0 = Character.isDigit(c0i) || c0i == '_';
                    boolean d1 = Character.isDigit(c1i) || c0i == '_';

                    if (d0 && d1) {
                        int cmp = Character.compare(c0i, c1i);
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else if (d0) {
                        return -1;
                    } else if (d1) {
                        return 1;
                    }

                    boolean u0 = Character.isUpperCase(c0i);
                    boolean u1 = Character.isUpperCase(c1i);

                    char r0 = u0 ? 'A' : 'a';
                    char r1 = u1 ? 'A' : 'a';

                    int t0 = (c0i - r0 << 1) + (u0 ? 0 : 1);
                    int t1 = (c1i - r1 << 1) + (u1 ? 0 : 1);

                    if (t0 > t1) {
                        return 1;
                    } else if (t1 > t0) {
                        return -1;
                    }
                }

                if (l0 > l1) {
                    return 1;
                } else if (l0 < l1) {
                    return -1;
                }

                return 0;
            });

    private final NetClient client;
    @Getter
    private final String name;
    @Getter
    private final UUID uuid;
    @Getter
    private volatile ChatComponent tabListName;
    @Getter
    private volatile GameMode gameMode;
    @Getter
    private volatile PlayerProperty skinTextures;
    private final AtomicBoolean finishedLogin = new AtomicBoolean(false);
    @Getter
    @Setter
    private volatile int renderDistance = 7;
    private final Map<IntPair, Chunk> heldChunks = new ConcurrentHashMap<>();
    @Getter
    private final VeraPlayerMeta metadata;
    @Getter
    private volatile boolean godMode;
    private volatile boolean canFly;
    @Getter
    private volatile boolean flying;
    @Getter
    private volatile float flyingSpeed = Player.DEFAULT_FLYING_SPEED;
    @Getter
    private volatile float walkingSpeed = Player.DEFAULT_WALKING_SPEED;

    @Getter
    private TabList tabList;
    private final List<BossBar> bossBars = new CopyOnWriteArrayList<>();
    @Getter
    @Setter
    private volatile String locale;
    @Setter
    private volatile boolean chatColors;
    @Setter
    private volatile ClientChatMode chatMode;
    @Getter
    private final VeraPlayerInventory inventory;

    private final Set<String> permissions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    @Getter
    private volatile boolean op;

    
    private VeraPlayer(NetClient client, World world, String name, UUID uuid,
                       PlayerProperty skinTextures) {
        super(world, PoolSpec.PLAYERS);
        this.metadata = (VeraPlayerMeta) super.getMetadata();

        this.client = client;
        this.name = name;
        this.uuid = uuid;
        this.tabListName = ChatComponent.text(name);
        this.gameMode = world.getWorldOptions().getGameMode();
        this.canFly = this.gameMode == GameMode.CREATIVE || this.gameMode == GameMode.SPECTATOR;
        this.skinTextures = skinTextures;
        this.inventory = new VeraPlayerInventory(client);
    }
    
    public static VeraPlayer spawn(NetClient client, String name, UUID uuid,
                                      PlayerProperty skinTextures) {
        World world = VeraServer.getInstance().getWorldLoader().getDefaultWorld();
        VeraPlayer player = new VeraPlayer(client, world, name, uuid, skinTextures);
        client.setPlayer(player);

        VeraPlayer.players.put(uuid, player);
        VeraPlayer.playerNames.put(name, player);
        Login.finish();

        player.updateChunks(player.getPosition());
        player.resumeLogin();

        return player;
    }

    public void resumeLogin() {
        if (!this.finishedLogin.compareAndSet(false, true)) {
            return;
        }

        World world = this.getWorld();
        this.client.sendPacket(new PlayOutJoinGame(this, world));
        this.client.sendPacket(PlayOutPluginMsg.BRAND);
        VeraPluginChannel.autoAdd(this);
        this.client.sendPacket(new PlayOutDifficulty(world));
        this.client.sendPacket(new PlayOutSpawnPos());
        this.client.sendPacket(new PlayOutPlayerAbilities(this));
        this.inventory.update();
        this.client.sendPacket(new PlayOutPosLook(this));

        this.client.sendPacket(new PlayOutTime(world.getAge().longValue(), world.getTime()));
        if (world.getWeather().isRaining()) {
            this.client.sendPacket(new PlayOutGameState(2, 0));
        }

        this.setTabList(GlobalTabList.getInstance());

        Collections.addAll(this.permissions, "minecraft.help");
        if (VeraServer.getInstance().getOpsList().getOps().contains(this.uuid)) {
            this.op = true;
        }
        RecipientSelector.whoCanSee(this, true, new PlayOutSpawnPlayer(this));

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            ChatComponent chat = ChatComponent.create()
                    .setColor(ChatColor.YELLOW)
                    .setTranslate("multiplayer.player.joined")
                    .addWith(this.name);
            PlayerJoinEvent event = new PlayerJoinEvent(this, chat);
            VeraServer.getInstance().getEventController().callEvent(event);
            ChatComponent message = event.getMessage();
            if (message != null)
                players.values().forEach(p -> p.sendMessage(message, ChatType.CHAT));
        });

        VeraServer.getInstance().getLogger().log("Player " + this.name + " [" + this.uuid + "] has connected");
    }

    public NetClient net() {
        return this.client;
    }

    @Override
    public void doTick() {
        this.client.tick();
    }

    @Override
    public PacketOut getSpawnPacket() {
        return new PlayOutSpawnPlayer(this);
    }

    @Override
    public void doRemove() {
        if (VeraPlayer.players.remove(this.uuid) == null) {
            Login.finish();
        }

        VeraPluginChannel.autoRemove(this);
        playerNames.remove(this.name);

        this.setTabList(null);
        GlobalTabList.getInstance().unsubscribe(this);
        VeraInventory.clean();
        for (Chunk chunk : this.heldChunks.values()) {
            chunk.getHolders().remove(this);
        }
        this.heldChunks.clear();

        ChatComponent chat = ChatComponent.create()
                .setColor(ChatColor.YELLOW)
                .setTranslate("multiplayer.player.left")
                .addWith(this.name);
        VeraServer.getInstance().getEventController().callEvent(new PlayerQuitEvent(this, chat), e -> {
            ChatComponent message = e.getMessage();
            if (message != null)
                players.values().forEach(p -> p.sendMessage(message, ChatType.CHAT));
        });
        this.client.disconnect(ChatComponent.empty());
    }

    @Override
    public void setTabListName(ChatComponent name) {
        if (name != null && name.getText() == null)
            throw new IllegalArgumentException("display name must set text field");
        this.tabListName = name != null ? name : ChatComponent.text(this.name);

        GlobalTabList.getInstance().updateTabListName(this);
    }

    @Override
    public void sendMessage(ChatComponent chat, ChatType type) {
        ClientChatMode chatMode = this.chatMode;
        if (chatMode == ClientChatMode.COMMANDS_ONLY && type == ChatType.SYSTEM ||
                chatMode == ClientChatMode.CHAT_AND_COMMANDS) {
            this.net().sendPacket(new PlayOutChat(chat, type, this.chatColors));
        }
    }

    @Override
    public void kick(ChatComponent reason) {
        this.client.disconnect(reason);
    }

    @Override
    public void setTabList(TabList tabList) {
        synchronized (this.heldChunks) {
            TabList old = this.tabList;
            if (old != null) {
                old.unsubscribe(this);
            }

            if (tabList != null) {
                this.tabList = tabList;
                this.tabList.subscribe(this);
            }
        }
    }

    @Override
    public List<BossBar> getBossBars() {
        return Collections.unmodifiableList(this.bossBars);
    }

    @Override
    public void addBossBar(BossBar bossBar) {
        Objects.requireNonNull(bossBar, "boss bar cannot be null");

        this.bossBars.add(bossBar);
        this.net().sendPacket(new PlayOutBossBar.Add(bossBar));
    }

    @Override
    public void removeBossBar(BossBar bossBar) {
        Objects.requireNonNull(bossBar, "boss bar cannot be null");
        
        if (this.bossBars.remove(bossBar)) {
            this.net().sendPacket(new PlayOutBossBar.Remove(bossBar));
        }
    }

    @Override
    public void updateBossBars() {
        this.updateBossBars(false);
    }

    private void updateBossBars(boolean force) {
        for (BossBar bar : this.bossBars) {
            if (force) {
                this.net().sendPacket(new PlayOutBossBar.Add((BossBar) bar));
                continue;
            }

            this.net().sendPacket(new PlayOutBossBar.UpdateHealth((BossBar) bar));
            this.net().sendPacket(new PlayOutBossBar.UpdateTitle((BossBar) bar));
            this.net().sendPacket(new PlayOutBossBar.UpdateStyle((BossBar) bar));
            this.net().sendPacket(new PlayOutBossBar.UpdateFlags((BossBar) bar));
        }
    }

    @Override
    public void sendTitle(Title title) {
        synchronized (this.bossBars) {
            if (!title.isDefaultFadeTimes()) {
                this.net().sendPacket(new PlayOutTitle.SetTiming(title));
            }

            ChatComponent mainTitle = title.getHeader();
            ChatComponent subtitle = title.getSubtitle();

            this.net().sendPacket(new PlayOutTitle.SetSubtitle(subtitle));
            this.net().sendPacket(new PlayOutTitle.SetTitle(mainTitle));
        }
    }

    @Override
    public void resetTitle() {
        synchronized (this.bossBars) {
            this.net().sendPacket(new PlayOutTitle.Reset());
        }
    }

    @Override
    public void openInventory(Inventory inventory) {
        VeraInventory.open((VeraInventory) inventory, this);
    }

    public void setTextures(PlayerProperty skinTextures) {
        this.skinTextures = skinTextures;

        GlobalTabList.getInstance().update(this);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        this.canFly = gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR;
        this.client.sendPacket(new PlayOutPlayerAbilities(this));
        this.client.sendPacket(new PlayOutGameState(3, gameMode.asInt()));
    }

    @Override
    public void setGodMode(boolean godMode) {
        this.setGodMode(godMode, true);
    }

    public void setGodMode(boolean godMode, boolean sendPacket) {
        this.godMode = godMode;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    @Override
    public boolean canFly() {
        return this.canFly;
    }

    @Override
    public void setCanFly(boolean canFly) {
        this.setCanFly(canFly, true);
    }

    public void setCanFly(boolean canFly, boolean sendPacket) {
        this.canFly = canFly;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    @Override
    public void setFlying(boolean flying) {
        this.setFlying(flying, true);
    }

    public void setFlying(boolean flying, boolean sendPacket) {
        this.flying = flying;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    @Override
    public void setFlyingSpeed(float flyingSpeed) {
        this.setFlyingSpeed(flyingSpeed, true);
    }

    public void setFlyingSpeed(float flyingSpeed, boolean sendPacket) {
        this.flyingSpeed = flyingSpeed;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    @Override
    public void setWalkingSpeed(float walkingSpeed) {
        this.setWalkingSpeed(walkingSpeed, true);
    }

    @Override
    public void setSprinting(boolean sprinting) {
        this.metadata.setSprinting(sprinting);
        if (sprinting) {
            this.walkingSpeed = Player.DEFAULT_SPRINT_SPEED;
        } else {
            this.walkingSpeed = Player.DEFAULT_WALKING_SPEED;
        }
        this.client.sendPacket(new PlayOutPlayerAbilities(this));
        this.updateMetadata();
    }

    @Override
    public boolean isSprinting() {
        return this.metadata.isSprinting();
    }

    @Override
    public void setCrouching(boolean crouching) {
        this.metadata.setCrouched(crouching);
        this.updateMetadata();
    }

    @Override
    public boolean isCrouching() {
        return this.metadata.isCrouched();
    }

    public void setWalkingSpeed(float walkingSpeed, boolean sendPacket) {
        this.walkingSpeed = walkingSpeed;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    public void updateChunks(Position position) {
        World world = position.getWorld();
        int centerX = position.getChunkX();
        int centerZ = position.getChunkZ();

        int radius = this.renderDistance;

        this.pool.execute(() -> {
            for (int x = centerX - radius; x < centerX + radius; x++) {
                for (int z = centerZ - radius; z < centerZ + radius; z++) {
                    IntPair pair = IntPair.make(x, z);
                    if (!this.heldChunks.containsKey(pair)) {
                        Chunk chunk = world.getChunkAt(x, z);
                        this.heldChunks.put(pair, chunk);
                        chunk.getHolders().add(this);
                        chunk.getEntities().filter(e -> !e.equals(this)).forEach(e -> this.net().sendPacket(((VeraEntity) e).getSpawnPacket()));
                        this.net().sendPacket(new PlayOutChunk(chunk));
                    }
                }
            }
        });

        this.pool.execute(() -> {
            for (Chunk chunk : this.heldChunks.values()) {
                if (Math.abs(chunk.getX() - centerX) > radius || Math.abs(chunk.getZ() - centerZ) > radius) {
                    this.heldChunks.remove(IntPair.make(chunk.getX(), chunk.getZ()));
                    chunk.getHolders().remove(this);
                    this.net().sendPacket(new PlayOutUnloadChunk(chunk.getX(), chunk.getZ()));

                    if (!chunk.getEntitySet().isEmpty() || !chunk.getOccupants().isEmpty()) {
                        this.net().sendPacket(new PlayOutDestroyEntities(chunk.getEntities().collect(Collectors.toList())));
                    }
                    chunk.checkValidForGc();
                }
            }
        });
    }

    @Override
    public void chat(String msg) {
        ChatComponent chat = ChatComponent.create()
                .setTranslate("chat.type.text")
                .addWith(ChatComponent.create()
                        .setText(this.getName())
                        .setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/tell " + this.getName() + " ")))
                .addWith(msg);
        Collection<Player> recipients = new ArrayList<>(VeraPlayer.getPlayers().values());
        PlayerChatEvent _event = new PlayerChatEvent(this, chat, recipients);
        VeraServer.getInstance().getEventController().callEvent(_event, event -> {
            if (!event.isCancelled()) {
                ChatComponent chatComponent = event.getChatComponent();
                event.getRecipients().forEach(p -> p.sendMessage(chatComponent, ChatType.CHAT));
            }
            VeraServer.getInstance().getLogger().log(getName() + " [" + getUuid() + "]: " + msg);
        });
    }

    @Override
    @Policy("plugin thread only")
    public void runCommand(String command) {
        VeraServer.getInstance().getLogger().log(this.name + " issued server command: /" + command);
        if (!VeraServer.getInstance().getCommandHandler().dispatch(command, this)) {
            this.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No command found for " + command.split(" ")[0]));
        }
    }

    @Override
    public CommandSourceType getCmdType() {
        return CommandSourceType.PLAYER;
    }

    @Override
    public boolean hasPermission(String perm) {
        return this.op || this.permissions.contains(perm);
    }

    @Override
    public void addPermission(String perm) {
        this.permissions.add(perm);
    }

    @Override
    public boolean removePermission(String perm) {
        return this.permissions.remove(perm);
    }

    @Override
    public void setOp(boolean op) {
        this.op = op;

        if (op) {
            VeraServer.getInstance().getOpsList().addOp(this.uuid);

            ChatComponent c = ChatComponent.
                    create().
                    setColor(ChatColor.GRAY).
                    setText("[Server: " + this.name + " has been opped]");
            for (UUID uuid : VeraServer.getInstance().getOpsList().getOps()) {
                VeraPlayer p = players.get(uuid);
                if (p != null) {
                    p.sendMessage(c);
                }
            }
        } else {
            VeraServer.getInstance().getOpsList().removeOp(this.uuid);

            ChatComponent c = ChatComponent.
                    create().
                    setColor(ChatColor.GRAY).
                    setText("[Server " + this.name + " has been deopped]");
            for (UUID uuid : VeraServer.getInstance().getOpsList().getOps()) {
                VeraPlayer p = players.get(uuid);
                if (p != null) {
                    p.sendMessage(c);
                }
            }
        }
    }
}