package io.vera.world.gen;

import io.vera.server.world.World;
import java.util.Set;
import javax.annotation.Nonnull;

public interface GeneratorProvider {
  TerrainGenerator getTerrainGenerator(World paramWorld);
  
  @Nonnull
  Set<FeatureGenerator> getFeatureGenerators(World paramWorld);
  
  @Nonnull
  Set<PropGenerator> getPropGenerators(World paramWorld);
  
  default GenContainer getGenerationContainer() {
    return GenContainer.DEFAULT;
  }
}
