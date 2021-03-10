
package io.vera.server.world.gen;

import io.vera.server.world.World;
import io.vera.world.gen.*;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Set;

@Immutable
public class FlatGeneratorProvider implements GeneratorProvider{

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
