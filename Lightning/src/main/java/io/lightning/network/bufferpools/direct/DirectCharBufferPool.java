
package io.lightning.network.bufferpools.direct;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class DirectCharBufferPool extends AbstractBufferPool<CharBuffer> {
    
    @Override
    protected CharBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity << 1).asCharBuffer();
    }

    @Override
    public void give(CharBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("A non-direct CharBuffer cannot be given to a DirectCharBufferPool!");
        }
        
        super.give(buffer);
    }
}
