
package io.vera.world.gen;

import io.vera.world.opt.GenOpts;

public interface FeatureGenerator {

    void generate(int chunkX, int chunkZ, GeneratorContext context);
}
