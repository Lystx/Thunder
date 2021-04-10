
package io.lightning.network.bufferpools.heap;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ByteBuffer;

public class ByteBufferPool extends AbstractBufferPool<ByteBuffer> {
    
    @Override
    protected ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    @Override
    public void give(ByteBuffer buffer) {
        if (buffer.isDirect()) {
            throw new IllegalArgumentException("A direct ByteBuffer cannot be given to a ByteBufferPool!");
        }
        
        super.give(buffer);
    }
}
