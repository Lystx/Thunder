
package io.lightning.network.bufferpools.direct;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DirectFloatBufferPool extends AbstractBufferPool<FloatBuffer> {

    @Override
    protected FloatBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity << 2).asFloatBuffer();
    }

    @Override
    public void give(FloatBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("A non-direct FloatBuffer cannot be given to a DirectFloatBufferPool!");
        }

        super.give(buffer);
    }
}
