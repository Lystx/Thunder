package io.vera.server.net;

import io.netty.buffer.ByteBuf;
import io.vera.server.packet.login.LoginOutEncryptionRequest;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class NetCrypto {
  private static final int TOKEN_LEN = 4;
  
  private static final int KEY_PAIR_BITS = 1024;
  
  private static final String CIPHER_NAME = "AES/CFB8/NoPadding";
  
  private static final String SECRET_ALGO = "AES";
  
  private static final String KEY_PAIR_ALGO = "RSA";
  
  private static final SecureRandom RANDOM = new SecureRandom();
  
  private final KeyPair kp;
  
  private final byte[] token;
  
  private volatile Cipher encrypt;
  
  private volatile Cipher decrypt;
  
  static {
    RANDOM.nextBytes(new byte[4]);
  }
  
  public NetCrypto() {
    KeyPair localPair;
    try {
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(1024);
      localPair = generator.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } 
    this.kp = localPair;
    this.token = new byte[4];
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
      Cipher keyPairCipher = Cipher.getInstance("RSA");
      keyPairCipher.init(2, this.kp.getPrivate());
      byte[] decryptedSecret = keyPairCipher.doFinal(encryptedSecret);
      byte[] decryptedToken = keyPairCipher.doFinal(encryptedToken);
      if (Arrays.equals(decryptedToken, this.token)) {
        SecretKey sharedSecret = new SecretKeySpec(decryptedSecret, "AES");
        IvParameterSpec iv = new IvParameterSpec(sharedSecret.getEncoded());
        Cipher instance = Cipher.getInstance("AES/CFB8/NoPadding");
        instance.init(2, sharedSecret, iv);
        this.decrypt = instance;
        instance = Cipher.getInstance("AES/CFB8/NoPadding");
        instance.init(1, sharedSecret, iv);
        this.encrypt = instance;
        return decryptedSecret;
      } 
    } catch (NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException|java.security.InvalidKeyException|javax.crypto.BadPaddingException|javax.crypto.IllegalBlockSizeException|java.security.InvalidAlgorithmParameterException e) {
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
    byte[] bytes = NetData.arr(buf);
    dest.writeBytes(cipher.update(bytes));
  }
  
  public void decrypt(ByteBuf buf, ByteBuf dest, int len) {
    Cipher cipher = this.decrypt;
    if (cipher == null) {
      dest.writeBytes(buf, len);
      return;
    } 
    byte[] bytes = NetData.arr(buf, len);
    dest.writeBytes(cipher.update(bytes));
  }
}
