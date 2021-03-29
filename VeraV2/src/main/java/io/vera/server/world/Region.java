package io.vera.server.world;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class Region {
  private static final Map<Path, Region> CACHE = new ConcurrentHashMap<>();
  
  private static final int VERSION_GZIP = 1;
  
  private static final int VERSION_DEFLATE = 2;
  
  private static final int SECTOR_BYTES = 4096;
  
  private static final int SECTOR_INTS = 1024;
  
  private static final int CHUNK_HEADER_SIZE = 5;
  
  private static final byte[] emptySector = new byte[4096];
  
  private final int regionX;
  
  private final int regionZ;
  
  private final Path path;
  
  private final RandomAccessFile file;
  
  private final int[] offsets;
  
  private final ArrayList<Boolean> sectorFree;
  
  public int getRegionX() {
    return this.regionX;
  }
  
  public int getRegionZ() {
    return this.regionZ;
  }
  
  private Region(Path path) {
    this.path = path;
    this.offsets = new int[1024];
    try {
      if (!Files.exists(path, new java.nio.file.LinkOption[0]))
        Files.createFile(path, (FileAttribute<?>[])new FileAttribute[0]); 
      this.file = new RandomAccessFile(path.toFile(), "rw");
      if (this.file.length() < 4096L) {
        int j;
        for (j = 0; j < 1024; j++)
          this.file.writeInt(0); 
        for (j = 0; j < 1024; j++)
          this.file.writeInt(0); 
      } 
      if ((this.file.length() & 0xFFFL) != 0L)
        for (int j = 0; j < (this.file.length() & 0xFFFL); j++)
          this.file.write(0);  
      int nSectors = (int)this.file.length() / 4096;
      this.sectorFree = new ArrayList<>(nSectors);
      int i;
      for (i = 0; i < nSectors; i++)
        this.sectorFree.add(Boolean.valueOf(true)); 
      this.sectorFree.set(0, Boolean.valueOf(false));
      this.sectorFree.set(1, Boolean.valueOf(false));
      this.file.seek(0L);
      for (i = 0; i < 1024; i++) {
        int offset = this.file.readInt();
        this.offsets[i] = offset;
        if (offset != 0 && (offset >> 8) + (offset & 0xFF) <= this.sectorFree.size())
          for (int sectorNum = 0; sectorNum < (offset & 0xFF); sectorNum++)
            this.sectorFree.set((offset >> 8) + sectorNum, Boolean.valueOf(false));  
      } 
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    String[] split = path.getFileName().toString().split(Pattern.quote("."));
    this.regionX = Integer.parseInt(split[1]);
    this.regionZ = Integer.parseInt(split[2]);
  }
  
  public static Region getFile(Chunk chunk, boolean create) {
    Path path = chunk.getWorld().getDirectory().resolve("region").resolve("r." + (chunk.getX() >> 5) + '.' + (chunk.getZ() >> 5) + ".mca");
    if (!Files.exists(path, new java.nio.file.LinkOption[0]) && !create)
      return null; 
    return CACHE.computeIfAbsent(path, Region::new);
  }
  
  public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
    if (outOfBounds(x, z))
      return null; 
    try {
      int offset = getOffset(x, z);
      if (offset == 0)
        return null; 
      int sectorNumber = offset >> 8;
      int numSectors = offset & 0xFF;
      if (sectorNumber + numSectors > this.sectorFree.size())
        return null; 
      this.file.seek((sectorNumber * 4096));
      int length = this.file.readInt();
      if (length > 4096 * numSectors)
        return null; 
      byte version = this.file.readByte();
      if (version == 1) {
        byte[] data = new byte[length - 1];
        this.file.read(data);
        return new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data)));
      } 
      if (version == 2) {
        byte[] data = new byte[length - 1];
        this.file.read(data);
        return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
      } 
      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public DataOutputStream getChunkDataOutputStream(int x, int z) {
    if (outOfBounds(x, z))
      return null; 
    return new DataOutputStream(new DeflaterOutputStream(new ChunkBuffer(x, z)));
  }
  
  class ChunkBuffer extends ByteArrayOutputStream {
    private final int x;
    
    private final int z;
    
    public ChunkBuffer(int x, int z) {
      super(8096);
      this.x = x;
      this.z = z;
    }
    
    public void close() {
      Region.this.write(this.x, this.z, this.buf, this.count);
    }
  }
  
  public synchronized void write(int x, int z, byte[] data, int length) {
    try {
      int offset = getOffset(x, z);
      int sectorNumber = offset >> 8;
      int sectorsAllocated = offset & 0xFF;
      int sectorsNeeded = (length + 5) / 4096 + 1;
      if (sectorsNeeded >= 256)
        return; 
      if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
        write(sectorNumber, data, length);
      } else {
        for (int i = 0; i < sectorsAllocated; i++)
          this.sectorFree.set(sectorNumber + i, Boolean.valueOf(true)); 
        int runStart = this.sectorFree.indexOf(Boolean.valueOf(true));
        int runLength = 0;
        if (runStart != -1)
          for (int j = runStart; j < this.sectorFree.size(); j++) {
            if (runLength != 0) {
              if (((Boolean)this.sectorFree.get(j)).booleanValue()) {
                runLength++;
              } else {
                runLength = 0;
              } 
            } else if (((Boolean)this.sectorFree.get(j)).booleanValue()) {
              runStart = j;
              runLength = 1;
            } 
            if (runLength >= sectorsNeeded)
              break; 
          }  
        if (runLength >= sectorsNeeded) {
          sectorNumber = runStart;
          setOffset(x, z, sectorNumber << 8 | sectorsNeeded);
          for (int j = 0; j < sectorsNeeded; j++)
            this.sectorFree.set(sectorNumber + j, Boolean.valueOf(false)); 
          write(sectorNumber, data, length);
        } else {
          this.file.seek(this.file.length());
          sectorNumber = this.sectorFree.size();
          for (int j = 0; j < sectorsNeeded; j++) {
            this.file.write(emptySector);
            this.sectorFree.add(Boolean.valueOf(false));
          } 
          write(sectorNumber, data, length);
          setOffset(x, z, sectorNumber << 8 | sectorsNeeded);
        } 
      } 
      setTimestamp(x, z, (int)(System.currentTimeMillis() / 1000L));
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  private void write(int sectorNumber, byte[] data, int length) throws IOException {
    this.file.seek((sectorNumber << 12));
    this.file.writeInt(length + 1);
    this.file.writeByte(2);
    this.file.write(data, 0, length);
  }
  
  private boolean outOfBounds(int x, int z) {
    return (x < 0 || x >= 32 || z < 0 || z >= 32);
  }
  
  private int getOffset(int x, int z) {
    return this.offsets[x + (z << 5)];
  }
  
  public boolean hasChunk(int x, int z) {
    return (getOffset(x, z) != 0);
  }
  
  private void setOffset(int x, int z, int offset) throws IOException {
    this.offsets[x + (z << 5)] = offset;
    this.file.seek((x + (z << 5) << 2));
    this.file.writeInt(offset);
  }
  
  private void setTimestamp(int x, int z, int value) throws IOException {
    this.file.seek((4096 + (x + (z << 5) << 2)));
    this.file.writeInt(value);
  }
  
  public void close() throws IOException {
    CACHE.remove(this.path);
    this.file.close();
  }
}
