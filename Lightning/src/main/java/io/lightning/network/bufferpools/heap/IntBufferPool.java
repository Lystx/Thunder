
package io.lightning.network.bufferpools.heap;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.IntBuffer;

public class IntBufferPool extends AbstractBufferPool<IntBuffer> {

    @Override
    protected IntBuffer allocate(int capacity) {
        return IntBuffer.allocate(capacity);
    }

    @Override
    public void give(IntBuffer buffer) {
        if (buffer.isDirect()) {
            throw new IllegalArgumentException("A direct IntBuffer cannot be given to a IntBufferPool!");
        }

        super.give(buffer);
    }
}
