
package io.thunder.utils.vson.elements;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.enums.VsonType;

import java.math.BigDecimal;


public class VsonNumber extends VsonValue {

    private final double value;

    public VsonNumber(double value) {
        this.value=value;
    }

    @Override
    public String toString() {
        long l = (long)value;

        if (l == value) {
            return Long.toString(l);
        }
        String result = BigDecimal.valueOf(value).toEngineeringString();

        if (result.endsWith(".0")) {
            return result.substring(0, result.length()-2);
        } else if (result.contains("E")) {
            result = Double.toString(value);
            result = result.replace("E-", "e-").replace("E", "e+");
        }
        return result;
    }

    @Override
    public VsonType getType() {
        return VsonType.NUMBER;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public boolean isByte() {
        try {
            Byte.parseByte(String.valueOf(this.value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isLong() {
        try {
            Long.parseLong(String.valueOf(this.value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isShort() {
        try {
            Short.parseShort(String.valueOf(this.value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isFloat() {
        try {
            Float.parseFloat(String.valueOf(this.value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isDouble() {
        try {
            Double.parseDouble(String.valueOf(this.value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isInt() {
        try {
            int i = (int)value;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int asInt() {
        return (int)value;
    }

    @Override
    public long asLong() {
        return (long)value;
    }

    @Override
    public byte asByte() {
        return (byte) value;
    }

    @Override
    public short asShort() {
        return (short) value;
    }

    @Override
    public float asFloat() {
        return (float)value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(value).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        VsonNumber other = (VsonNumber)object;
        return value == other.value;
    }
}
