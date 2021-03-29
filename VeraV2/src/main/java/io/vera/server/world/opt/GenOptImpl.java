package io.vera.server.world.opt;

import io.vera.meta.nbt.Compound;
import io.vera.server.world.gen.FlatGeneratorProvider;
import io.vera.world.gen.GeneratorProvider;
import io.vera.world.opt.GenOpts;
import io.vera.world.opt.LevelType;
import io.vera.world.opt.WorldCreateSpec;
import java.util.Random;
import javax.annotation.concurrent.Immutable;

@Immutable
public class GenOptImpl implements GenOpts {
  private static final Random SEED_SRC = new Random();
  
  private final GeneratorProvider provider;
  
  private final long seed;
  
  private final String optionString;
  
  private final LevelType levelType;
  
  private final boolean allowFeatures;
  
  public GeneratorProvider getProvider() {
    return this.provider;
  }
  
  public long getSeed() {
    return this.seed;
  }
  
  public String getOptionString() {
    return this.optionString;
  }
  
  public LevelType getLevelType() {
    return this.levelType;
  }
  
  public boolean isAllowFeatures() {
    return this.allowFeatures;
  }
  
  public GenOptImpl(WorldCreateSpec spec) {
    if (spec.isDefault()) {
      this.provider = (GeneratorProvider)FlatGeneratorProvider.INSTANCE;
      this.levelType = LevelType.FLAT;
      this.optionString = "";
      this.allowFeatures = true;
      this.seed = verifySeed(0L);
    } else {
      this.provider = (spec.getProvider() == null) ? (GeneratorProvider)FlatGeneratorProvider.INSTANCE : spec.getProvider();
      this.levelType = spec.getLevelType();
      this.optionString = spec.getOptionString();
      this.allowFeatures = spec.isAllowFeatures();
      this.seed = verifySeed(spec.getSeed());
    } 
  }
  
  public GenOptImpl(Compound compound) {
    String providerClass = (String)compound.get("VeraProvider");
    if (providerClass != null) {
      try {
        this.provider = (GeneratorProvider)Class.forName(providerClass).newInstance();
      } catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
        throw new RuntimeException(e);
      } 
    } else {
      this.provider = (GeneratorProvider)FlatGeneratorProvider.INSTANCE;
    } 
    this.seed = compound.getLong("RandomSeed");
    this.levelType = LevelType.from(compound.getString("generatorName"));
    this.optionString = compound.getString("generatorOptions");
    this.allowFeatures = (compound.getByte("MapFeatures") == 1);
  }
  
  private static long verifySeed(long seed) {
    if (seed == 0L) {
      long potentialSeed;
      while ((potentialSeed = SEED_SRC.nextLong()) == 0L);
      return potentialSeed;
    } 
    return seed;
  }
  
  public void write(Compound compound) {
    if (this.provider != FlatGeneratorProvider.INSTANCE)
      compound.putString("VeraProvider", this.provider.getClass().getName()); 
    compound.putLong("RandomSeed", this.seed);
    compound.putString("generatorName", this.levelType.toString());
    compound.putString("generatorOptions", this.optionString);
    compound.putByte("MapFeatures", (byte)(this.allowFeatures ? 1 : 0));
  }
}
