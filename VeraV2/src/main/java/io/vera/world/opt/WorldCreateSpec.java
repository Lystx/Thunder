package io.vera.world.opt;

import io.vera.world.gen.GeneratorProvider;
import io.vera.world.other.Vector;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class WorldCreateSpec {
    private WorldCreateSpec(boolean difficultyLocked, Vector spawn, GeneratorProvider provider, long seed, boolean def) {
        this.difficultyLocked = difficultyLocked;
        this.spawn = spawn;
        this.provider = provider;
        this.seed = seed;
        this.def = def;
    }

    public static WorldSpecBuilder builder() {
        return new WorldSpecBuilder();
    }

    public static class WorldSpecBuilder {
        private boolean difficultyLocked;

        private Vector spawn;

        private GeneratorProvider provider;

        private long seed;

        private boolean def;

        public WorldSpecBuilder difficultyLocked(boolean difficultyLocked) {
            this.difficultyLocked = difficultyLocked;
            return this;
        }

        public WorldSpecBuilder spawn(Vector spawn) {
            this.spawn = spawn;
            return this;
        }

        public WorldSpecBuilder provider(GeneratorProvider provider) {
            this.provider = provider;
            return this;
        }

        public WorldSpecBuilder seed(long seed) {
            this.seed = seed;
            return this;
        }

        public WorldSpecBuilder def(boolean def) {
            this.def = def;
            return this;
        }

        public WorldCreateSpec build() {
            return new WorldCreateSpec(this.difficultyLocked, this.spawn, this.provider, this.seed, this.def);
        }

        public String toString() {
            return "WorldCreateSpec.WorldSpecBuilder(difficultyLocked=" + this.difficultyLocked + ", spawn=" + this.spawn + ", provider=" + this.provider + ", seed=" + this.seed + ", def=" + this.def + ")";
        }
    }

    private static final WorldCreateSpec DEFAULT = new WorldCreateSpec(true);

    private final Difficulty difficulty = Difficulty.NORMAL;

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    private final Dimension dimension = Dimension.OVERWORLD;

    public Dimension getDimension() {
        return this.dimension;
    }

    private final GameMode gameMode = GameMode.SURVIVAL;

    public GameMode getGameMode() {
        return this.gameMode;
    }

    private final GameRuleMap gameRules = new GameRuleMap();

    private boolean difficultyLocked;

    private Vector spawn;

    private GeneratorProvider provider;

    private long seed;

    public GameRuleMap getGameRules() {
        return this.gameRules;
    }

    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    public Vector getSpawn() {
        return this.spawn;
    }

    public GeneratorProvider getProvider() {
        return this.provider;
    }

    public long getSeed() {
        return this.seed;
    }

    private final LevelType levelType = LevelType.DEFAULT;

    public LevelType getLevelType() {
        return this.levelType;
    }

    private final boolean allowFeatures = true;

    public boolean isAllowFeatures() {
        return true;
    }

    private final boolean def;

    public String getOptionString() {
        return "";
    }

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
