
package io.thunder.utils.vson.elements;


import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.enums.VsonType;
import io.thunder.utils.vson.enums.VsonValidates;

public class VsonLiteral extends VsonValue {

    public static final VsonValue NULL = new VsonLiteral(VsonValidates.NULL);
    public static final VsonValue TRUE = new VsonLiteral(VsonValidates.TRUE);
    public static final VsonValue FALSE = new VsonLiteral(VsonValidates.FALSE);

    private final VsonValidates value;

    private VsonLiteral(VsonValidates value) {
        this.value = value;
    }

    @Override
    public String toString() {
        switch (value) {
            case TRUE:
                return "true";
            case FALSE:
                return "false";
            case NULL:
                return "null";
            default:
                return null;
        }
    }


    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public VsonType getType() {
        return this.value == VsonValidates.NULL ? VsonType.NULL : VsonType.BOOLEAN;
    }

    @Override
    public boolean isNull() {
        return value == VsonValidates.NULL;
    }

    @Override
    public boolean isTrue() {
        return value == VsonValidates.TRUE;
    }

    @Override
    public boolean isFalse() {
        return value == VsonValidates.FALSE;
    }

    @Override
    public boolean isBoolean() {
        return value != VsonValidates.NULL;
    }

    @Override
    public Boolean asBoolean() {
        return value == VsonValidates.NULL ? super.asBoolean() : value== VsonValidates.TRUE;
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
        VsonLiteral other =(VsonLiteral)object;
        return value == other.value;
    }
}
