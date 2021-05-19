
package io.thunder.utils.vson.elements.other;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.elements.VsonString;
import io.thunder.utils.vson.other.IVsonProvider;

public class Dsf {
    private Dsf() {}

    public static IVsonProvider math() {
        return new DsfMath();
    }

    public static IVsonProvider hex(boolean stringify) { return new DsfHex(stringify); }

    public static boolean isInvalidDsfChar(char c)
    {
        return c == '{' || c == '}' || c == '[' || c == ']' || c == ',';
    }


    public static VsonValue parse(IVsonProvider[] dsfProviders, String value) {
        for (IVsonProvider dsf : dsfProviders) {
            try {
                VsonValue res = dsf.parse(value);
                if (res != null) return res;
            } catch (Exception exception) {
                throw new RuntimeException("DSF-" + dsf.getName() + " failed; " + exception.getMessage());
            }
        }
        return new VsonString(value);
    }

    public static String stringify(IVsonProvider[] dsfProviders, VsonValue value) {
        for (IVsonProvider dsf : dsfProviders) {
            try {
                String text = dsf.toString(value);
                if (text != null) {
                    boolean isInvalid = false;
                    char[] textc = text.toCharArray();
                    for (char ch : textc) {
                        if (isInvalidDsfChar(ch)) {
                            isInvalid = true;
                            break;
                        }
                    }
                    if (isInvalid || text.length() == 0 || textc[0] == '"')
                        throw new Exception("value may not be empty, start with a quote or contain a punctuator character except colon: " + text);
                    return text;
                }
            } catch (Exception exception) {
                throw new RuntimeException("DSF-" + dsf.getName() + " failed; " + exception.getMessage());
            }
        }
        return null;
    }

}

