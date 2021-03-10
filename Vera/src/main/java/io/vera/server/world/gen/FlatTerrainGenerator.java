
package io.vera.server.world.gen;

import io.vera.world.gen.GeneratorContext;
import io.vera.world.gen.TerrainGenerator;

import javax.annotation.concurrent.Immutable;


@Immutable
public class FlatTerrainGenerator implements TerrainGenerator {
    public static final FlatTerrainGenerator INSTANCE = new FlatTerrainGenerator();

    @Override
    public void generate(int chunkX, int chunkZ, GeneratorContext context) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                context.set(x, 0, z, 7, (byte) 0);
                context.set(x, 1, z, 3, (byte) 0);
                context.set(x, 2, z, 3, (byte) 0);
                context.set(x, 3, z, 2, (byte) 0);
            }
        }
    }
}