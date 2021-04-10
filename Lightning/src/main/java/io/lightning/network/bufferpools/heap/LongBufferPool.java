
package io.lightning.network.bufferpools.heap;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.LongBuffer;


public class LongBufferPool extends AbstractBufferPool<LongBuffer> {

    @Override
    protected LongBuffer allocate(int capacity) {
        return LongBuffer.allocate(capacity);
    }

    @Override
    public void give(LongBuffer buffer) {
        if (buffer.isDirect()) {
            throw new IllegalArgumentException("A direct LongBuffer cannot be given to a LongBufferPool!");
        }

        super.give(buffer);
    }
}
