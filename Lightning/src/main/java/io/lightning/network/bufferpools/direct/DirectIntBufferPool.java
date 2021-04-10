
package io.lightning.network.bufferpools.direct;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DirectIntBufferPool extends AbstractBufferPool<IntBuffer> {

    @Override
    protected IntBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity << 2).asIntBuffer();
    }

    @Override
    public void give(IntBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("A non-direct IntBuffer cannot be given to a DirectIntBufferPool!");
        }

        super.give(buffer);
    }
}
