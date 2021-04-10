
package io.lightning.network.bufferpools.direct;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public class DirectDoubleBufferPool extends AbstractBufferPool<DoubleBuffer> {

    @Override
    protected DoubleBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity << 3).asDoubleBuffer();
    }

    @Override
    public void give(DoubleBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("A non-direct DoubleBuffer cannot be given to a " +
                "DirectDoubleBufferPool!");
        }

        super.give(buffer);
    }
}
