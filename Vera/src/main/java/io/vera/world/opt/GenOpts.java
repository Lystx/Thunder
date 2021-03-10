
package io.vera.world.opt;

import io.vera.world.gen.GeneratorProvider;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface GenOpts {

    GeneratorProvider getProvider();

    long getSeed();

    String getOptionString();

    LevelType getLevelType();

    boolean isAllowFeatures();
}