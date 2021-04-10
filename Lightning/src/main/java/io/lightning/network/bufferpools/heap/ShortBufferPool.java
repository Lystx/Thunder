
package io.lightning.network.bufferpools.heap;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ShortBuffer;

public class ShortBufferPool extends AbstractBufferPool<ShortBuffer> {

    @Override
    protected ShortBuffer allocate(int capacity) {
        return ShortBuffer.allocate(capacity);
    }

    @Override
    public void give(ShortBuffer buffer) {
        if (buffer.isDirect()) {
            throw new IllegalArgumentException("A direct ShortBuffer cannot be given to a ShortBufferPool!");
        }

        super.give(buffer);
    }
}
