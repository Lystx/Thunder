
package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.login.LoginOutEncryptionRequest;

import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

import static io.vera.server.net.NetData.arr;


@ThreadSafe
public class NetCrypto {

    private static final int TOKEN_LEN = 4;
    private static final int KEY_PAIR_BITS = 1024;
    private static final String CIPHER_NAME = "AES/CFB8/NoPadding";
    private static final String SECRET_ALGO = "AES";
    private static final String KEY_PAIR_ALGO = "RSA";
    private static final SecureRandom RANDOM = new SecureRandom();

    static {
        RANDOM.nextBytes(new byte[TOKEN_LEN]);
    }


    private final KeyPair kp;
    private final byte[] token;
    private volatile Cipher encrypt;
    private volatile Cipher decrypt;

    public NetCrypto() {
        KeyPair localPair;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_PAIR_ALGO);
            generator.initialize(KEY_PAIR_BITS);
            localPair = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.kp = localPair;

        this.token = new byte[TOKEN_LEN];
        RANDOM.nextBytes(this.token);
    }


    public LoginOutEncryptionRequest reqCrypto() {
        return new LoginOutEncryptionRequest(this.kp.getPublic().getEncoded(), this.token);
    }

    public KeyPair kp() {
        return this.kp;
    }

    public byte[] begin(byte[] encryptedSecret, byte[] encryptedToken) {
        try {
            Cipher keyPairCipher = Cipher.getInstance(KEY_PAIR_ALGO);
            keyPairCipher.init(Cipher.DECRYPT_MODE, this.kp.getPrivate());

            byte[] decryptedSecret = keyPairCipher.doFinal(encryptedSecret);
            byte[] decryptedToken = keyPairCipher.doFinal(encryptedToken);

            if (Arrays.equals(decryptedToken, this.token)) {
                SecretKey sharedSecret = new SecretKeySpec(decryptedSecret, SECRET_ALGO);
                IvParameterSpec iv = new IvParameterSpec(sharedSecret.getEncoded());

                Cipher instance = Cipher.getInstance(CIPHER_NAME);
                instance.init(Cipher.DECRYPT_MODE, sharedSecret, iv);
                this.decrypt = instance;

                instance = Cipher.getInstance(CIPHER_NAME);
                instance.init(Cipher.ENCRYPT_MODE, sharedSecret, iv);
                this.encrypt = instance;

                return decryptedSecret;
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void encrypt(ByteBuf buf, ByteBuf dest) {
        Cipher cipher = this.encrypt;
        if (cipher == null) {
            dest.writeBytes(buf);
            return;
        }

        byte[] bytes = arr(buf);
        dest.writeBytes(cipher.update(bytes));
    }

    public void decrypt(ByteBuf buf, ByteBuf dest, int len) {
        Cipher cipher = this.decrypt;
        if (cipher == null) {
            dest.writeBytes(buf, len);
            return;
        }

        byte[] bytes = arr(buf, len);
        dest.writeBytes(cipher.update(bytes));
    }
}