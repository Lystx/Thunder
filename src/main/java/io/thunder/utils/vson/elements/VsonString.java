
package io.thunder.utils.vson.elements;


import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.enums.VsonType;

public class VsonString extends VsonValue {

    private final String string;

    public VsonString(String string) {
        if (string==null) {
            throw new NullPointerException("string is null");
        }
        this.string=string;
    }

    @Override
    public VsonType getType() {
        return VsonType.STRING;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String asString() {
        return string;
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this==object) {
            return true;
        }
        if (object==null) {
            return false;
        }
        if (getClass()!=object.getClass()) {
            return false;
        }
        VsonString other=(VsonString)object;
        return string.equals(other.string);
    }
}
