
package io.lightning.network.bufferpools.direct;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ByteBuffer;

public class DirectByteBufferPool extends AbstractBufferPool<ByteBuffer> {

    @Override
    protected ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    @Override
    public void give(ByteBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("A non-direct ByteBuffer cannot be given to a DirectByteBufferPool!");
        }

        super.give(buffer);
    }
}
