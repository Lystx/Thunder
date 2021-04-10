

package io.thunder.manager.tls;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * This class builds the TLS Server
 * it will manage everythign for you
 */
public class TLSBuilder {
    
    private String host;
    private int port;

    private boolean customKeyStore = false;
    private String keyStoreType;
    private InputStream keyStoreStream;
    private char[] keyStorePassword;

    /**
     * Adds the Key Store
     * @param keyStoreType
     * @param keyStoreStream
     * @param keyStorePassword
     * @return
     */
    public TLSBuilder addKeyStore(String keyStoreType, InputStream keyStoreStream, char[] keyStorePassword) {
        customKeyStore = true;
        this.keyStoreType = keyStoreType;
        this.keyStoreStream = keyStoreStream;
        this.keyStorePassword = keyStorePassword;

        return this;
    }

    /**
     * Adds the Trust Store
     * @param trustStoreType
     * @param trustStoreStream
     * @param trustStorePassword
     * @return
     */
    public TLSBuilder addTrustStore(String trustStoreType, InputStream trustStoreStream, char[] trustStorePassword) {
        return addKeyStore(trustStoreType, trustStoreStream, trustStorePassword);
    }

    /**
     * Builds the {@link SSLContext}
     * @return the SSLContext for CIPHER
     *
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     */
    private SSLContext build() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {
        SSLContext sslContext;

        if (customKeyStore) {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(keyStoreStream, keyStorePassword);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            sslContext = SSLContext.getInstance(TLS.PROTOCOL);
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
        } else {
            sslContext = SSLContext.getDefault();
        }

        return sslContext;
    }

    /**
     * Builds the {@link SSLSocket}
     * @return the SSL Socket
     *
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     */
    public SSLSocket buildSSLSocket() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {
        if (host == null) throw new IllegalStateException("Cannot create socket without host");

        SSLSocket s = (SSLSocket) build().getSocketFactory().createSocket(host, port);

        s.setEnabledProtocols(TLS.getUsableTLS(s.getSupportedProtocols(), TLS.PROTOCOLS));
        s.setEnabledCipherSuites(TLS.getUsableTLS(s.getSupportedCipherSuites(), TLS.CIPHER_SUITES));

        return s;
    }

    /**
     * Builds the {@link SSLServerSocket}
     * @return the SSL Server Socket
     *
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public SSLServerSocket buildSSLServer() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        SSLServerSocket s = (SSLServerSocket) build().getServerSocketFactory().createServerSocket(port);

        s.setEnabledProtocols(TLS.getUsableTLS(s.getSupportedProtocols(), TLS.PROTOCOLS));
        s.setEnabledCipherSuites(TLS.getUsableTLS(s.getSupportedCipherSuites(), TLS.CIPHER_SUITES));

        return s;
    }

    /**
     * Sets the port of the server
     * @param port the port
     * @return current Builder
     */
    public TLSBuilder port(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the host of the server
     * @param host the host
     * @return current Builder
     */
    public TLSBuilder port(String host) {
        this.host = host;
        return this;
    }

}
