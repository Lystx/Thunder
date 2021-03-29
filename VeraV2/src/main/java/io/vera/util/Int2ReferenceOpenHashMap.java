package io.vera.util;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Int2ReferenceOpenHashMap<V> {

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

    protected transient int[] key;

    protected transient V[] value;

    protected transient int mask;

    protected transient boolean containsNullKey;

    protected transient int n;

    protected transient int maxFill;

    protected int size;

    protected final float f;

    public Int2ReferenceOpenHashMap(int expected, float f) {
        if (f <= 0.0F || f > 1.0F)
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        if (expected < 0)
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        this.f = f;
        this.n = arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = maxFill(this.n, f);
        this.key = new int[this.n + 1];
        this.value = (V[])new Object[this.n + 1];
    }

    public Int2ReferenceOpenHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    private int realSize() {
        return this.containsNullKey ? (this.size - 1) : this.size;
    }

    private V removeEntry(int pos) {
        V oldValue = this.value[pos];
        this.value[pos] = null;
        this.size--;
        shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > DEFAULT_INITIAL_SIZE)
            rehash(this.n / 2);
        return oldValue;
    }

    private V removeNullEntry() {
        this.containsNullKey = false;
        V oldValue = this.value[this.n];
        this.value[this.n] = null;
        this.size--;
        if (this.size < this.maxFill / 4 && this.n > DEFAULT_INITIAL_SIZE)
            rehash(this.n / 2);
        return oldValue;
    }

    private int insert(int k, V v) {
        int pos;
        if (k == 0) {
            if (this.containsNullKey)
                return this.n;
            this.containsNullKey = true;
            pos = this.n;
        } else {
            int[] key = this.key;
            int curr;
            if ((curr = key[pos = mix(k) & this.mask]) != 0) {
                if (curr == k)
                    return pos;
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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

    public V put(int k, V v) {
        int pos = insert(k, v);
        if (pos < 0)
            return null;
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    protected final void shiftKeys(int pos) {
        int[] key = this.key;
        while (true) {
            int curr, last;
            pos = (last = pos) + 1 & this.mask;
            while (true) {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    this.value[last] = null;
                    return;
                }
                int slot = mix(curr) & this.mask;
                if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
                    break;
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
            this.value[last] = this.value[pos];
        }
    }

    public V remove(int k) {
        if (k == 0) {
            if (this.containsNullKey)
                return removeNullEntry();
            return null;
        }
        int[] key = this.key;
        int curr, pos;
        if ((curr = key[pos = mix(k) & this.mask]) == 0)
            return null;
        if (k == curr)
            return removeEntry(pos);
        while (true) {
            if ((curr = key[pos = pos + 1 & this.mask]) == 0)
                return null;
            if (k == curr)
                return removeEntry(pos);
        }
    }

    public V get(int k) {
        if (k == 0)
            return this.containsNullKey ? this.value[this.n] : null;
        int[] key = this.key;
        int curr, pos;
        if ((curr = key[pos = mix(k) & this.mask]) == 0)
            return null;
        if (k == curr)
            return this.value[pos];
        while (true) {
            if ((curr = key[pos = pos + 1 & this.mask]) == 0)
                return null;
            if (k == curr)
                return this.value[pos];
        }
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
        int[] key = this.key;
        V[] value = this.value;
        int mask = newN - 1;
        int[] newKey = new int[newN + 1];
        V[] newValue = (V[])new Object[newN + 1];
        int i = this.n;
        for (int j = realSize(); j-- != 0; ) {
            while (key[--i] == 0);
            int pos;
            if (newKey[pos = mix(key[i]) & mask] != 0)
                while (newKey[pos = pos + 1 & mask] != 0);
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
