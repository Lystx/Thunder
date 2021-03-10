
package io.vera.server.world.opt;

import io.vera.meta.nbt.Compound;
import io.vera.world.gen.GeneratorProvider;
import io.vera.world.opt.GenOpts;
import io.vera.world.opt.LevelType;
import io.vera.world.opt.WorldCreateSpec;
import lombok.Getter;
import io.vera.server.world.gen.FlatGeneratorProvider;

import javax.annotation.concurrent.Immutable;
import java.util.Random;

@Immutable
public class GenOptImpl implements GenOpts {

    private static final Random SEED_SRC = new Random();

    @Getter
    private final GeneratorProvider provider;
    @Getter
    private final long seed;
    @Getter
    private final String optionString;
    @Getter
    private final LevelType levelType;
    @Getter
    private final boolean allowFeatures;

    public GenOptImpl(WorldCreateSpec spec) {
        if (spec.isDefault()) {
            this.provider = FlatGeneratorProvider.INSTANCE;
            this.levelType = LevelType.FLAT;
            this.optionString = "";
            this.allowFeatures = true;
            this.seed = verifySeed(0);
        } else {
            this.provider = spec.getProvider() == null ? FlatGeneratorProvider.INSTANCE : spec.getProvider();
            this.levelType = spec.getLevelType();
            this.optionString = spec.getOptionString();
            this.allowFeatures = spec.isAllowFeatures();
            this.seed = verifySeed(spec.getSeed());
        }
    }

    public GenOptImpl(Compound compound) {
        String providerClass = compound.get("VeraProvider");
        if (providerClass != null) {
            try {
                this.provider = (GeneratorProvider) Class.forName(providerClass).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
               throw new RuntimeException(e);
            }
        } else {
            this.provider = FlatGeneratorProvider.INSTANCE;
        }

        this.seed = compound.getLong("RandomSeed");
        this.levelType = LevelType.from(compound.getString("generatorName"));
        this.optionString = compound.getString("generatorOptions");
        this.allowFeatures = compound.getByte("MapFeatures") == 1;
    }

    private static long verifySeed(long seed) {
        if (seed == 0) {
            long potentialSeed;
            while ((potentialSeed = SEED_SRC.nextLong()) == 0);
            return potentialSeed;
        } else {
            return seed;
        }
    }

    public void write(Compound compound) {
        if (this.provider != FlatGeneratorProvider.INSTANCE) {
            compound.putString("VeraProvider", this.provider.getClass().getName());
        }
        compound.putLong("RandomSeed", this.seed);
        compound.putString("generatorName", this.levelType.toString());
        compound.putString("generatorOptions", this.optionString);
        compound.putByte("MapFeatures", (byte) (this.allowFeatures ? 1 : 0));
    }
}