
package io.lightning.network.bufferpools.direct;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class DirectLongBufferPool extends AbstractBufferPool<LongBuffer> {

    @Override
    protected LongBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity << 3).asLongBuffer();
    }

    @Override
    public void give(LongBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("A non-direct LongBuffer cannot be given to a DirectLongBufferPool!");
        }

        super.give(buffer);
    }
}
