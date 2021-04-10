
package io.lightning.network.bufferpools.heap;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.FloatBuffer;

public class FloatBufferPool extends AbstractBufferPool<FloatBuffer> {

    @Override
    protected FloatBuffer allocate(int capacity) {
        return FloatBuffer.allocate(capacity);
    }

    @Override
    public void give(FloatBuffer buffer) {
        if (buffer.isDirect()) {
            throw new IllegalArgumentException("A direct FloatBuffer cannot be given to a FloatBufferPool!");
        }

        super.give(buffer);
    }
}
