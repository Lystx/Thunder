package io.thunder.utils.vson.elements.object;

import java.io.Serializable;

/**
 * Code not by me
 * Found somewhere on Stackoverflow
 */
public class HashIndexTable implements Serializable {

    private final byte[] hashTable;

    public HashIndexTable() {
        this.hashTable = new byte[32];
    }

    public void add(String name, int index) {
        int slot = this.cacheSlotFor(name);
        if (index < 0xff) {
            hashTable[slot] = (byte) (index + 1);
        } else {
            hashTable[slot] = 0;
        }
    }

    public void remove(int index) {
        for (int i = 0; i < hashTable.length; i++) {
            if (hashTable[i] == index + 1) {
                hashTable[i] = 0;
            } else if (hashTable[i] > index + 1) {
                hashTable[i]--;
            }
        }
    }

    public int get(Object name) {
        int slot = cacheSlotFor(name);
        return (hashTable[slot] & 0xff) - 1;
    }

    private int cacheSlotFor(Object element) {
        return element.hashCode() & hashTable.length - 1;
    }
}
