
package io.vera.world.gen;

import io.vera.inventory.Substance;

public interface GeneratorContext {

    long nextLong();

    long nextLong(long max);

    int nextInt();

    int nextInt(int max);

    long seed();

    int maxHeight(int x, int z);

    void set(int x, int y, int z, Substance substance, byte meta);

    void set(int x, int y, int z, Substance substance);

    void set(int x, int y, int z, int id, byte meta);

    void run(Runnable r);
}
