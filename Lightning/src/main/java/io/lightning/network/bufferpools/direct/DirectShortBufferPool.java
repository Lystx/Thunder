
package io.lightning.network.bufferpools.direct;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public final class DirectShortBufferPool extends AbstractBufferPool<ShortBuffer> {

    @Override
    protected ShortBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity << 1).asShortBuffer();
    }

    @Override
    public void give(ShortBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("A non-direct ShortBuffer cannot be given to a DirectShortBufferPool!");
        }

        super.give(buffer);
    }
}
