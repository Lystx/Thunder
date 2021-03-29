package io.vera.server.world.gen;

import io.vera.server.world.World;
import io.vera.world.gen.FeatureGenerator;
import io.vera.world.gen.GenContainer;
import io.vera.world.gen.GeneratorProvider;
import io.vera.world.gen.PropGenerator;
import io.vera.world.gen.TerrainGenerator;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class FlatGeneratorProvider implements GeneratorProvider {
  public static final FlatGeneratorProvider INSTANCE = new FlatGeneratorProvider();
  
  public TerrainGenerator getTerrainGenerator(World world) {
    return FlatTerrainGenerator.INSTANCE;
  }
  
  @Nonnull
  public Set<FeatureGenerator> getFeatureGenerators(World world) {
    return Collections.emptySet();
  }
  
  @Nonnull
  public Set<PropGenerator> getPropGenerators(World world) {
    return Collections.emptySet();
  }
  
  public GenContainer getGenerationContainer() {
    return GenContainer.ARBITRARY;
  }
}
