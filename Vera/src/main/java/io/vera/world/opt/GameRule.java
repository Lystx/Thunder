
package io.vera.world.opt;

import io.vera.util.Misc;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Immutable
public class GameRule<T> {
    private static final Map<String, GameRule<?>> GAME_RULES = new HashMap<>();


    public static final GameRule<Boolean> ANNOUNCE_ADVANCEMENT = newRule("announceAdvancements", true);
    public static final GameRule<Boolean> CMD_BLOCK_OUTPUT = newRule("commandBlockOutput", true);
    public static final GameRule<Boolean> MOVE_CHECK = newRule("disableElytraMovementCheck", false);
    public static final GameRule<Boolean> DAYLIGHT_CYCLE = newRule("doDaylightCycle", true);
    public static final GameRule<Boolean> DO_ENTITY_DROPS = newRule("doEntityDrops", true);
    public static final GameRule<Boolean> FIRE_TICK = newRule("doFireTick", true);
    public static final GameRule<Boolean> LIMIT_CRAFTING = newRule("doLimitedCrafting", false);
    public static final GameRule<Boolean> MOB_LOOT = newRule("doMobLoot", true);
    public static final GameRule<Boolean> MOB_SPAWN = newRule("doMobSpawning", true);
    public static final GameRule<Boolean> TILE_DROP = newRule("doTileDrops", true);
    public static final GameRule<Boolean> WEATHER_CYCLE = newRule("doWeatherCycle", true);
    public static final GameRule<String> TICK_FUNCTION = newRule("gameLoopFunction", "");
    public static final GameRule<Boolean> KEEP_INVENTORY = newRule("keepInventory", false);
    public static final GameRule<Boolean> LOG_ADMIN_CMDS = newRule("logAdminCommands", true);
    public static final GameRule<Integer> MAX_CMD_CHAIN_LEN = newRule("maxCommandChainLength", 65536);
    public static final GameRule<Integer> MAX_ENTITY_CRAM = newRule("maxEntityCramming", 24);
    public static final GameRule<Boolean> MOB_GRIEF = newRule("mobGriefing", true);
    public static final GameRule<Boolean> NATURAL_REGEN = newRule("naturalRegeneration", true);
    public static final GameRule<Integer> RANDOM_TICK_SPEED = newRule("randomTickSpeed", 3);
    public static final GameRule<Boolean> REDUCE_DEBUG = newRule("reducedDebugInfo", false);
    public static final GameRule<Boolean> SEND_CMD_FEEDBACK = newRule("sendCommandFeedback", true);
    public static final GameRule<Boolean> SHOW_DEATH_MSG = newRule("showDeathMessages", true);
    public static final GameRule<Integer> SPAWN_RADIUS = newRule("spawnRadius", 10);
    public static final GameRule<Boolean> SPEC_GEN_CHUNKS = newRule("spectatorsGenerateChunks", true);

    private static GameRule<Boolean> newRule(String name, boolean defValue) {
        GameRule<Boolean> rule = new GameRule<>(name, defValue, s -> s.equals("true"));
        GAME_RULES.put(name, rule);
        return rule;
    }

    private static GameRule<Integer> newRule(String name, int defValue) {
        GameRule<Integer> rule = new GameRule<>(name, defValue, Integer::parseInt);
        GAME_RULES.put(name, rule);
        return rule;
    }

    private static GameRule<String> newRule(String name, String defValue) {
        GameRule<String> rule = new GameRule<>(name, defValue, s -> s);
        GAME_RULES.put(name, rule);
        return rule;
    }

    private final String stringForm;
    private final T defValue;
    private final Function<String, T> parseFunction;

    private GameRule(String stringForm, T defValue, Function<String, T> parseFunction) {
        this.stringForm = stringForm;
        this.defValue = defValue;
        this.parseFunction = parseFunction;
    }

    public T getDefault() {
        return this.defValue;
    }

    public T parseValue(String value) {
        return this.parseFunction.apply(value);
    }

    public static <T> GameRule<T> from(String s) {
        GameRule rule = GAME_RULES.get(s);
        if (rule == null) {
            throw new IllegalArgumentException(String.format(Misc.NBT_BOUND_FAIL, "n.t.w.o.GameRule (" + s + ')'));
        }

        return rule;
    }

    public static Collection<String> getKeyStrings() {
        return Collections.unmodifiableCollection(GAME_RULES.keySet());
    }

    @Override
    public String toString() {
        return this.stringForm;
    }

}
