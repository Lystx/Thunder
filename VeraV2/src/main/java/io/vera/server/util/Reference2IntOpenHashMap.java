package io.vera.server.util;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Reference2IntOpenHashMap<K> {
  private static final int INT_PHI = -1640531527;
  
  public static final int mix(int x) {
    int h = x * -1640531527;
    return h ^ h >>> 16;
  }
  
  public static long nextPowerOfTwo(long x) {
    if (x == 0L)
      return 1L; 
    x--;
    x |= x >> 1L;
    x |= x >> 2L;
    x |= x >> 4L;
    x |= x >> 8L;
    x |= x >> 16L;
    return (x | x >> 32L) + 1L;
  }
  
  public static int maxFill(int n, float f) {
    return Math.min((int)Math.ceil((n * f)), n - 1);
  }
  
  public static int arraySize(int expected, float f) {
    long s = Math.max(2L, nextPowerOfTwo((long)Math.ceil((expected / f))));
    if (s > 1073741824L)
      throw new IllegalArgumentException("Too large (" + expected + " expected elements with load factor " + f + ")"); 
    return (int)s;
  }
  
  private static int DEFAULT_INITIAL_SIZE = 16;
  
  private static float DEFAULT_LOAD_FACTOR = 0.75F;
  
  protected transient K[] key;
  
  protected transient int[] value;
  
  protected transient int mask;
  
  protected transient boolean containsNullKey;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected int size;
  
  protected final float f;
  
  public Reference2IntOpenHashMap(int expected, float f) {
    if (f <= 0.0F || f > 1.0F)
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1"); 
    if (expected < 0)
      throw new IllegalArgumentException("The expected number of elements must be nonnegative"); 
    this.f = f;
    this.n = arraySize(expected, f);
    this.mask = this.n - 1;
    this.maxFill = maxFill(this.n, f);
    this.key = (K[])new Object[this.n + 1];
    this.value = new int[this.n + 1];
  }
  
  public Reference2IntOpenHashMap() {
    this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
  }
  
  private int realSize() {
    return this.containsNullKey ? (this.size - 1) : this.size;
  }
  
  public int getInt(Object k) {
    if (k == null)
      return this.containsNullKey ? this.value[this.n] : -1; 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = mix(
          System.identityHashCode(k)) & this.mask]) == null)
      return -1; 
    if (k == curr)
      return this.value[pos]; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return -1; 
      if (k == curr)
        return this.value[pos]; 
    } 
  }
  
  private int insert(K k, int v) {
    int pos;
    if (k == null) {
      if (this.containsNullKey)
        return this.n; 
      this.containsNullKey = true;
      pos = this.n;
    } else {
      K[] key = this.key;
      K curr;
      if ((curr = key[pos = mix(System.identityHashCode(k)) & this.mask]) != null) {
        if (curr == k)
          return pos; 
        while ((curr = key[pos = pos + 1 & this.mask]) != null) {
          if (curr == k)
            return pos; 
        } 
      } 
    } 
    this.key[pos] = k;
    this.value[pos] = v;
    if (this.size++ >= this.maxFill)
      rehash(arraySize(this.size + 1, this.f)); 
    return -1;
  }
  
  public int put(K k, int v) {
    int pos = insert(k, v);
    if (pos < 0)
      return -1; 
    int oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean trim() {
    int l = arraySize(this.size, this.f);
    if (l >= this.n || this.size > maxFill(l, this.f))
      return true; 
    try {
      rehash(l);
    } catch (OutOfMemoryError cantDoIt) {
      return false;
    } 
    return true;
  }
  
  protected void rehash(int newN) {
    K[] key = this.key;
    int[] value = this.value;
    int mask = newN - 1;
    K[] newKey = (K[])new Object[newN + 1];
    int[] newValue = new int[newN + 1];
    int i = this.n;
    for (int j = realSize(); j-- != 0; ) {
      while (key[--i] == null);
      int pos;
      if (newKey[pos = mix(
            System.identityHashCode(key[i])) & mask] != null)
        while (newKey[pos = pos + 1 & mask] != null); 
      newKey[pos] = key[i];
      newValue[pos] = value[i];
    } 
    newValue[newN] = value[this.n];
    this.n = newN;
    this.mask = mask;
    this.maxFill = maxFill(this.n, this.f);
    this.key = newKey;
    this.value = newValue;
  }
  
  public int hashCode() {
    int h = 0;
    for (int j = realSize(), i = 0, t = 0; j-- != 0; ) {
      while (this.key[i] == null)
        i++; 
      if (this != this.key[i])
        t = System.identityHashCode(this.key[i]); 
      t ^= this.value[i];
      h += t;
      i++;
    } 
    if (this.containsNullKey)
      h += this.value[this.n]; 
    return h;
  }
}
