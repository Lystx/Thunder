package org.gravel.library.utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class RandomString {

    private final Random random;
    private final char[] symbols;
    private char[] buf;

    public RandomString() {
        this.random = new SecureRandom();
        this.symbols = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase(Locale.ROOT) + "0123456789").toCharArray();
        this.buf = new char[23];
    }

    public String next(int newLength) {
        this.buf = new char[newLength];
        for (int idx = 0; idx < this.buf.length; idx++) {
            this.buf[idx] = this.symbols[this.random.nextInt(this.symbols.length)];
        }
        return new String(this.buf);
    }
}
