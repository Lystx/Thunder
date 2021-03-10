
package io.vera.server.world;

import io.netty.buffer.ByteBuf;
import io.vera.meta.nbt.Compound;
import io.vera.server.util.NibbleArray;
import io.vera.server.util.ShortArrayList;
import io.vera.server.util.ShortOpenHashSet;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicLongArray;

import static io.vera.server.net.NetData.wvint;

@ThreadSafe
public class ChunkSection {
    public static final ChunkSection EMPTY_WITH_SKYLIGHT = new ChunkSection(true);
    public static final ChunkSection EMPTY_WITHOUT_SKYLIGHT = new ChunkSection(false);
    private static final int BLOCKS_PER_SECTION = 4096;
    private static final int SHORTS_PER_LONG = 4;

    @GuardedBy("mainPalette")
    private final ShortOpenHashSet mainPalette = new ShortOpenHashSet();
    private final AtomicLongArray data = new AtomicLongArray(BLOCKS_PER_SECTION / SHORTS_PER_LONG);
    private final NibbleArray blockLight = new NibbleArray(BLOCKS_PER_SECTION / 2);
    private final NibbleArray skyLight = new NibbleArray(BLOCKS_PER_SECTION / 2);
    private final boolean doSkylight;


    public ChunkSection(boolean doSkylight) {
        this.mainPalette.add((short) 0);
        for (int i = 0; i < this.data.length(); i++) {
            this.data.set(i, 0L);
        }

        this.blockLight.fill((byte) 0xF);
        this.skyLight.fill((byte) 0xF);
        this.doSkylight = doSkylight;
    }

    public void set(int idx, short state) {
        synchronized (this.mainPalette) {
            this.mainPalette.add(state);
        }

        int spliceIdx = idx >>> 2;
        long shift = idx % SHORTS_PER_LONG << 4;

        long placeMask = ~(0xFFFFL << shift);
        long shiftedState = (long) state << shift;

        long oldSplice;
        long newSplice;
        do {
            oldSplice = this.data.get(spliceIdx);
            newSplice = oldSplice & placeMask | shiftedState;
        } while (!this.data.compareAndSet(spliceIdx, oldSplice, newSplice));
        // TODO relighting
    }

    public short dataAt(int idx) {
        int spliceIdx = idx >>> 2;
        long shift = idx % SHORTS_PER_LONG << 4;
        return (short) (this.data.get(spliceIdx) >>> shift & 0xFFFF);
    }

    public void write(ByteBuf buf) {
        int dataLen = 0;
        ByteBuf dataBuffer = buf.alloc().buffer();

        ShortArrayList palette = null;
        int bitsPerBlock;
        boolean doPalette;
        synchronized (this.mainPalette) {
            int paletteSize = this.mainPalette.size();
            bitsPerBlock = Integer.highestOneBit(paletteSize);
            if (bitsPerBlock < 4) {
                bitsPerBlock = 4;
            }

            doPalette = bitsPerBlock < 9;

            this.mainPalette.clear();
            this.mainPalette.add((short) 0);

            if (!doPalette) {
                bitsPerBlock = 13;
            } else {
                palette = new ShortArrayList(paletteSize);
                palette.add((short) 0);
            }

            int individualValueMask = (1 << bitsPerBlock) - 1;
            int bitsWritten = 0;
            long cur = 0;
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        int realIdx = y << 8 | z << 4 | x;
                        int data = this.dataAt(realIdx);
                        short shortData = (short) data;
                        boolean added = this.mainPalette.add(shortData);

                        if (doPalette) {
                            if (added) {
                                data = palette.add(shortData);
                            } else {
                                data = palette.indexOf(shortData);
                                if (data == -1) {
                                    throw new IllegalStateException("Failed to lock");
                                }
                            }
                        }

                        int shift = realIdx * bitsPerBlock % 64;
                        long or = data & individualValueMask;
                        bitsWritten += bitsPerBlock;

                        if (bitsWritten == 64) {
                            dataLen++;
                            dataBuffer.writeLong(cur | or << shift);

                            cur = 0;
                            bitsWritten = 0;
                        } else if (bitsWritten > 64) {
                            bitsWritten -= 64;
                            int lowerLen = bitsPerBlock - bitsWritten;
                            int lowerMask = (1 << lowerLen) - 1;

                            dataLen++;
                            dataBuffer.writeLong(cur | (or & lowerMask) << shift);

                            cur = (or & ~lowerMask) >> lowerLen;
                        } else {
                            cur |= or << shift;
                        }
                    }
                }
            }
        }

        buf.writeByte(bitsPerBlock);

        wvint(buf, doPalette ? palette.size() : 0);

        for (int i = 0, max = doPalette ? palette.size() : 0; i < max; i++) {
            wvint(buf, palette.getShort(i));
        }

        wvint(buf, dataLen);

        buf.writeBytes(dataBuffer);

        if (dataBuffer != null) {
            dataBuffer.release();
        }

        this.blockLight.write(buf);

        if (this.doSkylight) {
            this.skyLight.write(buf);
        }
    }

    public void read(Compound section) {
        byte[] blocks = section.getByteArray("Blocks");
        byte[] add = section.get("Add");
        byte[] data = section.getByteArray("Data");
        byte[] skyLight = section.getByteArray("SkyLight");
        byte[] blockLight = section.getByteArray("BlockLight");

        this.skyLight.read(skyLight);
        this.blockLight.read(blockLight);

        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int realIdx = y << 8 | z << 4 | x;

                    int block = blocks[realIdx];
                    byte blockData = NibbleArray.getNibble(data, realIdx);
                    if (add != null) {
                        int blockId = block + ((int) NibbleArray.getNibble(add, realIdx) << 8);
                        short state = (short) (blockId << 4 | blockData);
                        this.set(realIdx, state);
                    } else {
                        short state = (short) (block << 4 | blockData);
                        this.set(realIdx, state);
                    }
                }
            }
        }
    }

    public void write(Compound section) {
        section.putByteArray("SkyLight", this.skyLight.write());
        section.putByteArray("BlockLight", this.skyLight.write());

        byte[] blocks = new byte[4096];
        byte[] add = new byte[2048];
        byte[] data = new byte[2048];
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int realIdx = y << 8 | z << 4 | x;
                    short state = this.dataAt(realIdx);
                    int blockId = state >> 4;

                    blocks[realIdx] = (byte) (blockId & 0xFF);
                    NibbleArray.setNibble(data, realIdx, (byte) (state & 0xF));
                    if (blockId > 255) {
                        NibbleArray.setNibble(add, realIdx, (byte) (blockId >> 8));
                    }
                }
            }
        }

        section.putByteArray("Blocks", blocks);
        section.putByteArray("Add", add);
        section.putByteArray("Data", data);
    }
}