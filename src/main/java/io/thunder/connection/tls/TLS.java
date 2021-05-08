package io.thunder.connection.tls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used
 * to manage the TLS
 * for the CIPHER Encription
 * with SSL Context
 *
 */
public class TLS {

    public static final List<String> PROTOCOLS = new LinkedList<>();
    public static final List<String> CIPHER_SUITES = new LinkedList<>();
    public static final String PROTOCOL = "TLSv1.2";

    static {

        PROTOCOLS.add("TLSv1.2");

        CIPHER_SUITES.add("TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256");
        CIPHER_SUITES.add("TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256");

        CIPHER_SUITES.add("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384");
        CIPHER_SUITES.add("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256");

        CIPHER_SUITES.add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
        CIPHER_SUITES.add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");

        CIPHER_SUITES.add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
        CIPHER_SUITES.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");

    }

    /**
     * Returns a usable TLS Parameter
     *
     * @param supported the supported options
     * @param available the available options
     * @return String-Array
     */
    public static String[] getUsableTLS(String[] supported, List<String> available) {
        List<String> filtered = new ArrayList<>(available.size());
        List<String> supportedList = Arrays.asList(supported);

        for (String s : available) {
            if (supportedList.contains(s)) {
                filtered.add(s);
            }
        }

        String[] filteredArray = new String[filtered.size()];
        filtered.toArray(filteredArray);
        return filteredArray;
    }

}
