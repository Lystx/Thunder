
package io.lightning.network.bufferpools.heap;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.DoubleBuffer;

public class DoubleBufferPool extends AbstractBufferPool<DoubleBuffer> {

    @Override
    protected DoubleBuffer allocate(int capacity) {
        return DoubleBuffer.allocate(capacity);
    }

    @Override
    public void give(DoubleBuffer buffer) {
        if (buffer.isDirect()) {
            throw new IllegalArgumentException("A direct DoubleBuffer cannot be given to a DoubleBufferPool!");
        }

        super.give(buffer);
    }
}
