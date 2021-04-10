
package io.lightning.network.bufferpools.heap;

import io.lightning.network.bufferpools.AbstractBufferPool;

import java.nio.CharBuffer;

public class CharBufferPool extends AbstractBufferPool<CharBuffer> {
    
    @Override
    protected CharBuffer allocate(int capacity) {
        return CharBuffer.allocate(capacity);
    }

    @Override
    public void give(CharBuffer buffer) {
        if (buffer.isDirect()) {
            throw new IllegalArgumentException("A direct CharBuffer cannot be given to a CharBufferPool!");
        }
        
        super.give(buffer);
    }
}
