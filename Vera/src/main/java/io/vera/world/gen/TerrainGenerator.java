
package io.vera.world.gen;


public interface TerrainGenerator {

    void generate(int chunkX, int chunkZ, GeneratorContext context);
}
