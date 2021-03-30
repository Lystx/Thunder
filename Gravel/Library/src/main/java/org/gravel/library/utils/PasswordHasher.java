package org.gravel.library.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private final String algorithm;
    private final int size;

    private final SecureRandom random;
    private final int cost;

    public PasswordHasher() {
        this.algorithm = "PBKDF2WithHmacSHA1";
        this.size = 128;
        this.cost = 16;
        this.random = new SecureRandom();
    }


    private int getIterations(int cost) {
        if ((cost < 0) || (cost > 30)) {
            throw new IllegalArgumentException("cost: " + cost);
        }
        return 1 << cost;
    }


    private String hash(char[] password) {
        byte[] salt = new byte[this.size / 8];
        random.nextBytes(salt);
        byte[] dk = generateEncodedSecret(password, salt, 1 << cost);
        byte[] hash = new byte[salt.length + dk.length];
        System.arraycopy(salt, 0, hash, 0, salt.length);
        System.arraycopy(dk, 0, hash, salt.length, dk.length);
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        return "$31$" + cost + '$' + enc.encodeToString(hash);
    }

    private boolean authenticate(char[] password, String token) {
        Matcher m = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})").matcher(token);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid token format");
        }
        int iterations = this.getIterations(Integer.parseInt(m.group(1)));
        byte[] hash = Base64.getUrlDecoder().decode(m.group(2));
        byte[] salt = Arrays.copyOfRange(hash, 0, this.size / 8);
        byte[] check = generateEncodedSecret(password, salt, iterations);
        int zero = 0;
        for (int idx = 0; idx < check.length; ++idx)
            zero |= hash[salt.length + idx] ^ check[idx];
        return zero == 0;
    }

    private byte[] generateEncodedSecret(char[] password, byte[] salt, int iterations) {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, this.size);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance(this.algorithm);
            return f.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Missing algorithm: " + this.algorithm, ex);
        }
        catch (InvalidKeySpecException ex) {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        }
    }

    public String hash(String password) {
        return hash(password.toCharArray());
    }

    public boolean authenticate(String password, String token) {
        return authenticate(password.toCharArray(), token);
    }

}
