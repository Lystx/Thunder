package io.vera.server.util;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Long2ReferenceOpenHashMap<V> {
  private static final long LONG_PHI = -7046029254386353131L;
  
  public static final int DEFAULT_INITIAL_SIZE = 16;
  
  public static final float DEFAULT_LOAD_FACTOR = 0.75F;
  
  protected transient long[] key;
  
  protected transient V[] value;
  
  protected transient int mask;
  
  protected transient boolean containsNullKey;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected int size;
  
  protected final float f;
  
  protected transient Collection<V> values;
  
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
  
  public static final long mix(long x) {
    long h = x * -7046029254386353131L;
    h ^= h >>> 32L;
    return h ^ h >>> 16L;
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
  
  public static class LongArrayList {
    public static final int MAX_ARRAY_SIZE = 2147483639;
    
    protected transient long[] a;
    
    protected int size;
    
    private static final boolean ASSERTS = false;
    
    protected void ensureIndex(int index) {
      if (index < 0)
        throw new IndexOutOfBoundsException("Index (" + index + ") is negative"); 
      if (index > size())
        throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + 
            size() + ")"); 
    }
    
    public static long[] ensureCapacity(long[] array, int length, int preserve) {
      if (length > array.length) {
        long[] t = new long[length];
        System.arraycopy(array, 0, t, 0, preserve);
        return t;
      } 
      return array;
    }
    
    public static long[] grow(long[] array, int length, int preserve) {
      if (length > array.length) {
        int newLength = (int)Math.max(
            Math.min(2L * array.length, 2147483639L), length);
        long[] t = new long[newLength];
        System.arraycopy(array, 0, t, 0, preserve);
        return t;
      } 
      return array;
    }
    
    protected LongArrayList(long[] a, boolean dummy) {
      this.a = a;
    }
    
    public LongArrayList(int capacity) {
      if (capacity < 0)
        throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative"); 
      this.a = new long[capacity];
    }
    
    public void ensureCapacity(int capacity) {
      this.a = ensureCapacity(this.a, capacity, this.size);
    }
    
    private void grow(int capacity) {
      this.a = grow(this.a, capacity, this.size);
    }
    
    public void add(int index, long k) {
      ensureIndex(index);
      grow(this.size + 1);
      if (index != this.size)
        System.arraycopy(this.a, index, this.a, index + 1, this.size - index); 
      this.a[index] = k;
      this.size++;
    }
    
    public boolean add(long k) {
      grow(this.size + 1);
      this.a[this.size++] = k;
      return true;
    }
    
    public long getLong(int index) {
      if (index >= this.size)
        throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
      return this.a[index];
    }
    
    public int indexOf(long k) {
      for (int i = 0; i < this.size; i++) {
        if (k == this.a[i])
          return i; 
      } 
      return -1;
    }
    
    public int lastIndexOf(long k) {
      for (int i = this.size; i-- != 0;) {
        if (k == this.a[i])
          return i; 
      } 
      return -1;
    }
    
    public long removeLong(int index) {
      if (index >= this.size)
        throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
      long old = this.a[index];
      this.size--;
      if (index != this.size)
        System.arraycopy(this.a, index + 1, this.a, index, this.size - index); 
      return old;
    }
    
    public boolean rem(long k) {
      int index = indexOf(k);
      if (index == -1)
        return false; 
      removeLong(index);
      return true;
    }
    
    public long set(int index, long k) {
      if (index >= this.size)
        throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
      long old = this.a[index];
      this.a[index] = k;
      return old;
    }
    
    public void clear() {
      this.size = 0;
    }
    
    public int size() {
      return this.size;
    }
    
    public void size(int size) {
      if (size > this.a.length)
        ensureCapacity(size); 
      if (size > this.size)
        Arrays.fill(this.a, this.size, size, 0L); 
      this.size = size;
    }
  }
  
  public Long2ReferenceOpenHashMap(int expected, float f) {
    if (f <= 0.0F || f > 1.0F)
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1"); 
    if (expected < 0)
      throw new IllegalArgumentException("The expected number of elements must be nonnegative"); 
    this.f = f;
    this.n = arraySize(expected, f);
    this.mask = this.n - 1;
    this.maxFill = maxFill(this.n, f);
    this.key = new long[this.n + 1];
    this.value = (V[])new Object[this.n + 1];
  }
  
  public Long2ReferenceOpenHashMap() {
    this(16, 0.75F);
  }
  
  private int realSize() {
    return this.containsNullKey ? (this.size - 1) : this.size;
  }
  
  private V removeEntry(int pos) {
    V oldValue = this.value[pos];
    this.value[pos] = null;
    this.size--;
    shiftKeys(pos);
    if (this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  private V removeNullEntry() {
    this.containsNullKey = false;
    V oldValue = this.value[this.n];
    this.value[this.n] = null;
    this.size--;
    if (this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  private int insert(long k, V v) {
    int pos;
    if (k == 0L) {
      if (this.containsNullKey)
        return this.n; 
      this.containsNullKey = true;
      pos = this.n;
    } else {
      long[] key = this.key;
      long curr;
      if ((curr = key[pos = (int)mix(k) & this.mask]) != 0L) {
        if (curr == k)
          return pos; 
        while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
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
  
  public V put(long k, V v) {
    int pos = insert(k, v);
    if (pos < 0)
      return null; 
    V oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  protected final void shiftKeys(int pos) {
    long[] key = this.key;
    while (true) {
      long curr;
      int last;
      pos = (last = pos) + 1 & this.mask;
      while (true) {
        if ((curr = key[pos]) == 0L) {
          key[last] = 0L;
          this.value[last] = null;
          return;
        } 
        int slot = (int)mix(curr) & this.mask;
        if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
          break; 
        pos = pos + 1 & this.mask;
      } 
      key[last] = curr;
      this.value[last] = this.value[pos];
    } 
  }
  
  public V remove(long k) {
    if (k == 0L) {
      if (this.containsNullKey)
        return removeNullEntry(); 
      return null;
    } 
    long[] key = this.key;
    long curr;
    int pos;
    if ((curr = key[pos = (int)mix(k) & this.mask]) == 0L)
      return null; 
    if (k == curr)
      return removeEntry(pos); 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0L)
        return null; 
      if (k == curr)
        return removeEntry(pos); 
    } 
  }
  
  public V get(long k) {
    if (k == 0L)
      return this.containsNullKey ? this.value[this.n] : null; 
    long[] key = this.key;
    long curr;
    int pos;
    if ((curr = key[pos = (int)mix(k) & this.mask]) == 0L)
      return null; 
    if (k == curr)
      return this.value[pos]; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0L)
        return null; 
      if (k == curr)
        return this.value[pos]; 
    } 
  }
  
  public void clear() {
    if (this.size == 0)
      return; 
    this.size = 0;
    this.containsNullKey = false;
    Arrays.fill(this.key, 0L);
    Arrays.fill((Object[])this.value, (Object)null);
  }
  
  public boolean containsKey(long k) {
    if (k == 0L)
      return this.containsNullKey; 
    long[] key = this.key;
    long curr;
    int pos;
    if ((curr = key[pos = (int)mix(k) & this.mask]) == 0L)
      return false; 
    if (k == curr)
      return true; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == 0L)
        return false; 
      if (k == curr)
        return true; 
    } 
  }
  
  public boolean containsValue(Object v) {
    V[] value = this.value;
    long[] key = this.key;
    if (this.containsNullKey && value[this.n] == v)
      return true; 
    for (int i = this.n; i-- != 0;) {
      if (key[i] != 0L && value[i] == v)
        return true; 
    } 
    return false;
  }
  
  private class MapIterator {
    int pos = Long2ReferenceOpenHashMap.this.n;
    
    int last = -1;
    
    int c = Long2ReferenceOpenHashMap.this.size;
    
    boolean mustReturnNullKey = Long2ReferenceOpenHashMap.this.containsNullKey;
    
    LongArrayList wrapped;
    
    public boolean hasNext() {
      return (this.c != 0);
    }
    
    public int nextEntry() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.c--;
      if (this.mustReturnNullKey) {
        this.mustReturnNullKey = false;
        return this.last = Long2ReferenceOpenHashMap.this.n;
      } 
      long[] key = Long2ReferenceOpenHashMap.this.key;
      while (true) {
        if (--this.pos < 0) {
          this.last = Integer.MIN_VALUE;
          long k = this.wrapped.getLong(-this.pos - 1);
          int p = (int)Long2ReferenceOpenHashMap.mix(k) & Long2ReferenceOpenHashMap.this.mask;
          while (k != key[p])
            p = p + 1 & Long2ReferenceOpenHashMap.this.mask; 
          return p;
        } 
        if (key[this.pos] != 0L)
          return this.last = this.pos; 
      } 
    }
    
    private final void shiftKeys(int pos) {
      long[] key = Long2ReferenceOpenHashMap.this.key;
      while (true) {
        long curr;
        int last;
        pos = (last = pos) + 1 & Long2ReferenceOpenHashMap.this.mask;
        while (true) {
          if ((curr = key[pos]) == 0L) {
            key[last] = 0L;
            Long2ReferenceOpenHashMap.this.value[last] = null;
            return;
          } 
          int slot = (int)Long2ReferenceOpenHashMap.mix(curr) & Long2ReferenceOpenHashMap.this.mask;
          if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
            break; 
          pos = pos + 1 & Long2ReferenceOpenHashMap.this.mask;
        } 
        if (pos < last) {
          if (this.wrapped == null)
            this.wrapped = new LongArrayList(2);
          this.wrapped.add(key[pos]);
        } 
        key[last] = curr;
        Long2ReferenceOpenHashMap.this.value[last] = Long2ReferenceOpenHashMap.this.value[pos];
      } 
    }
    
    public void remove() {
      if (this.last == -1)
        throw new IllegalStateException(); 
      if (this.last == Long2ReferenceOpenHashMap.this.n) {
        Long2ReferenceOpenHashMap.this.containsNullKey = false;
        Long2ReferenceOpenHashMap.this.value[Long2ReferenceOpenHashMap.this.n] = null;
      } else if (this.pos >= 0) {
        shiftKeys(this.last);
      } else {
        Long2ReferenceOpenHashMap.this
          .remove(this.wrapped.getLong(-this.pos - 1));
        this.last = -1;
        return;
      } 
      Long2ReferenceOpenHashMap.this.size--;
      this.last = -1;
    }
    
    public int skip(int n) {
      int i = n;
      while (i-- != 0 && hasNext())
        nextEntry(); 
      return n - i - 1;
    }
    
    private MapIterator() {}
  }
  
  private final class ValueIterator extends MapIterator implements Iterator<V> {
    public V next() {
      return Long2ReferenceOpenHashMap.this.value[nextEntry()];
    }
  }
  
  public Collection<V> values() {
    if (this.values == null)
      this.values = new AbstractCollection<V>() {
          public Iterator<V> iterator() {
            return new ValueIterator();
          }
          
          public int size() {
            return Long2ReferenceOpenHashMap.this.size;
          }
          
          public boolean contains(Object v) {
            return Long2ReferenceOpenHashMap.this.containsValue(v);
          }
          
          public void clear() {
            Long2ReferenceOpenHashMap.this.clear();
          }
        }; 
    return this.values;
  }
  
  @Deprecated
  public boolean rehash() {
    return true;
  }
  
  protected void rehash(int newN) {
    long[] key = this.key;
    V[] value = this.value;
    int mask = newN - 1;
    long[] newKey = new long[newN + 1];
    V[] newValue = (V[])new Object[newN + 1];
    int i = this.n;
    for (int j = realSize(); j-- != 0; ) {
      while (key[--i] == 0L);
      int pos;
      if (newKey[pos = (int)mix(key[i]) & mask] != 0L)
        while (newKey[pos = pos + 1 & mask] != 0L); 
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
}
