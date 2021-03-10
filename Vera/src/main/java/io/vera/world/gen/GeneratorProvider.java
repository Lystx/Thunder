
package io.vera.world.gen;

import io.vera.server.world.World;

import javax.annotation.Nonnull;
import java.util.Set;

public interface GeneratorProvider {

    TerrainGenerator getTerrainGenerator(World world);

    @Nonnull
    Set<FeatureGenerator> getFeatureGenerators(World world);

    @Nonnull
    Set<PropGenerator> getPropGenerators(World world);

    default GenContainer getGenerationContainer() {
        return GenContainer.DEFAULT;
    }
}
