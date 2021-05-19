package io.thunder.utils.vson.elements.other;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.elements.VsonNumber;
import io.thunder.utils.vson.other.IVsonProvider;

import java.util.regex.Pattern;

public class DsfHex implements IVsonProvider {

    boolean stringify;
    public static Pattern isHex = Pattern.compile("^0x[0-9A-Fa-f]+$");

    public DsfHex(boolean stringify) {
        this.stringify = stringify;
    }

    public String getName() {
        return "hex";
    }

    public String getDescription() {
        return "parse hexadecimal numbers prefixed with 0x";
    }

    public VsonValue parse(String text) {
        if (isHex.matcher(text).find())
            return new VsonNumber(Long.parseLong(text.substring(2), 16));
        else
            return null;
    }

    public String toString(VsonValue value) {
        if (stringify && value.isNumber() && value.asLong() == value.asDouble()) {
            return "0x" + Long.toHexString(value.asLong());
        } else {
            return null;
        }
    }
}
