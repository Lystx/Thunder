
package io.vera.server.util;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicLongArray;


@ThreadSafe
@RequiredArgsConstructor
public class NibbleArray {

    public static final int BYTES_PER_LONG = 8;

    @NonNull
    private final AtomicLongArray nibbles;

    public NibbleArray(int length) {
        this.nibbles = new AtomicLongArray(length / BYTES_PER_LONG);
    }

    public static byte getNibble(byte[] array, int idx) {
        return (byte) ((idx & 1) == 0 ? array[idx >> 1] & 0x0F : array[idx >> 1] >> 4 & 0x0F);
    }

    public static void setNibble(byte[] array, int idx, byte nibble) {
        int i = idx >> 1;
        if ((idx & 1) == 0) {
            array[i] = (byte) (array[i] & 0xF0 | nibble & 0x0F);
        } else {
            array[i] = (byte) (array[i] & 0x0F | nibble << 4 & 0xF0);
        }
    }

    public int getLength() {
        return this.nibbles.length() * BYTES_PER_LONG << 1;
    }

    public byte getByte(int position) {
        int nibblePosition = position / 2;
        long splice = this.nibbles.get(nibblePosition / BYTES_PER_LONG);
        long shift = nibblePosition % BYTES_PER_LONG << 3;
        long shifted = splice >> shift;

        if ((position & 1) == 0) {
            return (byte) (shifted & 0x0F);
        } else {
            return (byte) (shifted >> 4 & 0x0F);
        }
    }

    public void setByte(int position, byte value) {
        int nibblePosition = position / 2;
        int spliceIndex = nibblePosition >> 3;
        long shift = nibblePosition % BYTES_PER_LONG << 3;

        long oldSpice; // easter egg (play Old Spice theme)
        long newSplice;
        if ((position & 1) == 0) {
            do {
                oldSpice = this.nibbles.get(spliceIndex);
                long newByte = oldSpice >>> shift & 0xF0 | value;

                newSplice = oldSpice & ~(0xFFL << shift) | newByte << shift;
            }
            while (!this.nibbles.compareAndSet(spliceIndex, oldSpice, newSplice));
        } else {
            long shiftedVal = value << 4;
            do {
                oldSpice = this.nibbles.get(spliceIndex);
                long newByte = oldSpice >>> shift & 0x0F | shiftedVal;

                newSplice = oldSpice & ~(0xFFL << shift) | newByte << shift;
            }
            while (!this.nibbles.compareAndSet(spliceIndex, oldSpice, newSplice));
        }
    }

    public void write(ByteBuf buf) {
        for (int i = 0, len = this.nibbles.length(); i < len; i++) {
            long l = this.nibbles.get(i);
            for (int shift = 0; shift < 64; shift += 8) {
                long shifted = l >> shift;
                byte b = (byte) (shifted & 0xFF);
                buf.writeByte(b);
            }
        }
    }

    public void fill(byte value) {
        long splice = 0;
        long newValue = value << 4 | value & 0xFF;
        for (int i = 0; i < 64; i += 8) {
            splice |= newValue << i;
        }

        for (int i = 0; i < this.nibbles.length(); i++) {
            this.nibbles.set(i, splice);
        }
    }

    public void read(byte[] bytes) {
        long cur = 0;
        for (int i = 0, shift = 0, splice = 0; i < bytes.length; i++) {
            cur |= (long) bytes[i] << shift;

            shift += 8;
            if (shift == 64) {
                this.nibbles.set(splice, cur);
                cur = 0;
                shift = 0;
                splice++;
            }
        }
    }

    public byte[] write() {
        byte[] bytes = new byte[this.nibbles.length() * BYTES_PER_LONG];
        for (int i = 0, len = this.nibbles.length(); i < len; i++) {
            long l = this.nibbles.get(i);
            for (int shift = 0, offset = 0; shift < 64; shift += 8, offset++) {
                long shifted = l >> shift;
                byte b = (byte) (shifted & 0xFF);
                bytes[i * BYTES_PER_LONG + offset] = b;
            }
        }
        return bytes;
    }
}