
package io.vera.server.util;

import io.vera.doc.Policy;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Copyright (C) 2002-2016 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@NotThreadSafe
public class ShortArrayList {
    /**
     * Grows the given array to the maximum between the
     * given length and the
     * current length multiplied by two, provided that the
     * given length is
     * larger than the current length, preserving just a
     * part of the array.
     *
     * <P>
     * If you want complete control on the array growth, you
     * should probably use
     * <code>ensureCapacity()</code> instead.
     *
     * @param array an array.
     * @param length the new minimum length for this array.
     * @param preserve the number of elements of the array
     * that must be preserved in
     * case a new allocation is necessary.
     * @return <code>array</code>, if it can contain
     * <code>length</code>
     * entries; otherwise, an array with max(<code>length</code>,
     * <code>array.length</code>/&phi;) entries whose first
     * <code>preserve</code> entries are the same as those
     * of
     * <code>array</code>.
     */
    public static short[] grow(short[] array, int length,
                               int preserve) {
        if (length > array.length) {
            int newLength = (int) Math.max(
                    Math.min(2L * array.length, MAX_ARRAY_SIZE), length);
            short t[] = new short[newLength];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    /**
     * Ensures that an array can contain the given number of
     * entries, preserving
     * just a part of the array.
     *
     * @param array an array.
     * @param length the new minimum length for this array.
     * @param preserve the number of elements of the array
     * that must be preserved in
     * case a new allocation is necessary.
     * @return <code>array</code>, if it can contain
     * <code>length</code> entries
     * or more; otherwise, an array with <code>length</code>
     * entries
     * whose first <code>preserve</code> entries are the
     * same as those
     * of <code>array</code>.
     */
    public static short[] ensureCapacity(short[] array, int length,
                                         int preserve) {
        if (length > array.length) {
            short t[] = new short[length];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    /**
     * Ensures that the given index is nonnegative and not
     * greater than the list
     * size.
     *
     * @param index an index.
     * @throws IndexOutOfBoundsException if the given index
     * is negative or greater than the list size.
     */
    protected void ensureIndex(int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("Index (" + index
                    + ") is negative");
        if (index > this.size())
            throw new IndexOutOfBoundsException("Index (" + index
                    + ") is greater than list size (" + this.size() + ")");
    }

    /**
     * This is a safe value used by {@link ArrayList} (as of
     * Java 7) to avoid
     * throwing {@link OutOfMemoryError} on some JVMs. We
     * adopt the same value.
     */
    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;


    /**
     * The initial default capacity of an array list.
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * The backing array.
     */
    protected transient short a[];
    /**
     * The current actual size of the list (never greater
     * than the backing-array
     * length).
     */
    protected int size;
    private static final boolean ASSERTS = false;

    /**
     * Creates a new array list using a given array.
     *
     * <P>
     * This constructor is only meant to be used by the
     * wrapping methods.
     *
     * @param a the array that will be used to back this
     * array list.
     * @param dummy dummy
     */
    @SuppressWarnings("unused")
    protected ShortArrayList(short a[], boolean dummy) {
        this.a = a;
    }

    /**
     * Creates a new array list with given capacity.
     *
     * @param capacity the initial capacity of the array
     * list (may be 0).
     */

    public ShortArrayList(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Initial capacity (" + capacity
                    + ") is negative");
        this.a = new short[capacity];
    }

    /**
     * Creates a new array list with {@link
     * #DEFAULT_INITIAL_CAPACITY} capacity.
     */
    public ShortArrayList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Ensures that this array list can contain the given
     * number of entries
     * without resizing.
     *
     * @param capacity the new minimum capacity for this
     * array list.
     */

    public void ensureCapacity(int capacity) {
        this.a = ensureCapacity(this.a, capacity, this.size);
        if (ASSERTS)
            assert this.size <= this.a.length;
    }

    /**
     * Grows this array list, ensuring that it can contain
     * the given number of
     * entries without resizing, and in case enlarging it at
     * least by a factor
     * of two.
     *
     * @param capacity the new minimum capacity for this
     * array list.
     */
    private void grow(int capacity) {
        this.a = grow(this.a, capacity, this.size);
        if (ASSERTS)
            assert this.size <= this.a.length;
    }

    public void add(int index, short k) {
        this.ensureIndex(index);
        this.grow(this.size + 1);
        if (index != this.size)
            System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
        this.a[index] = k;
        this.size++;
        if (ASSERTS)
            assert this.size <= this.a.length;
    }

    public int add(short k) {
        this.grow(this.size + 1);
        int idx = this.size++;
        this.a[idx] = k;
        if (ASSERTS)
            assert this.size <= this.a.length;
        return idx;
    }

    public short getShort(int index) {
        if (index >= this.size)
            throw new IndexOutOfBoundsException("Index (" + index
                    + ") is greater than or equal to list size (" + this.size + ")");
        return this.a[index];
    }

    public int indexOf(short k) {
        for (int i = 0; i < this.size; i++)
            if (k == this.a[i])
                return i;
        return -1;
    }

    public short set(int index, short k) {
        if (index >= this.size)
            throw new IndexOutOfBoundsException("Index (" + index
                    + ") is greater than or equal to list size (" + this.size + ")");
        short old = this.a[index];
        this.a[index] = k;
        return old;
    }

    public int size() {
        return this.size;
    }

    public void size(int size) {
        if (size > this.a.length)
            this.ensureCapacity(size);
        if (size > this.size)
            Arrays.fill(this.a, this.size, size, (short) 0);
        this.size = size;
    }
}