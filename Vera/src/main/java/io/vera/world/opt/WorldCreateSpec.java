
package io.vera.world.opt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.vera.world.other.Vector;
import io.vera.world.gen.GeneratorProvider;

import javax.annotation.concurrent.NotThreadSafe;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "WorldSpecBuilder")
@NotThreadSafe
public class WorldCreateSpec {

    private static final WorldCreateSpec DEFAULT = new WorldCreateSpec(true);

    @Getter
    private final Difficulty difficulty = Difficulty.NORMAL;
    @Getter
    private final Dimension dimension = Dimension.OVERWORLD;
    @Getter
    private final GameMode gameMode = GameMode.SURVIVAL;
    @Getter
    private final GameRuleMap gameRules = new GameRuleMap();
    @Getter
    private boolean difficultyLocked;
    @Getter
    private Vector spawn;
    @Getter
    private GeneratorProvider provider;
    @Getter
    private long seed;
    @Getter
    private final LevelType levelType = LevelType.DEFAULT;
    @Getter
    private final boolean allowFeatures = true;
    @Getter
    private final String optionString = "";

    private final boolean def;

    private WorldCreateSpec(boolean def) {
        this.def = def;
    }

    public static WorldCreateSpec getDefaultOptions() {
        return DEFAULT;
    }

    public static WorldCreateSpec custom() {
        return new WorldCreateSpec(false);
    }

    public boolean isDefault() {
        return this.def;
    }
}