
package io.thunder.utils.vson.elements;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.annotation.other.Vson;
import io.thunder.utils.vson.enums.VsonType;

import java.util.*;

public class VsonArray extends VsonValue implements Iterable<VsonValue> {

    private final List<VsonValue> values;

    public VsonArray() {
        this.values = new LinkedList<>();
    }

    public VsonArray(VsonArray array) {
        this(array, false);
    }

    public VsonArray(VsonArray array, boolean unmodifiable) {
        this.values = unmodifiable ? Collections.unmodifiableList(array.values) : new ArrayList<>(array.values);
    }

    public List<?> asList() {
        return new LinkedList<Object>(this.values);
    }

    public VsonArray append(int value) {
        values.add(valueOf(value));
        return this;
    }

    public VsonArray append(long value) {
        values.add(valueOf(value));
        return this;
    }

    public VsonArray append(float value) {
        values.add(valueOf(value));
        return this;
    }

    public VsonArray append(double value) {
        values.add(valueOf(value));
        return this;
    }

    public VsonArray append(boolean value) {
        values.add(valueOf(value));
        return this;
    }

    public VsonArray submit(Object object) {
        if (object instanceof Boolean) {
            return this.append(((Boolean)object));
        }
        if (object instanceof String) {
            return this.append((String)object);
        }
        if (object instanceof Integer) {
            return this.append((Integer)object);
        }
        if (object instanceof Byte) {
            return this.append((Byte)object);
        }
        if (object instanceof Double) {
            return this.append((Double)object);
        }
        if (object instanceof Short) {
            return this.append((Short)object);
        }
        if (object instanceof Long) {
            return this.append((Long)object);
        }
        if (object instanceof Float) {
            return this.append((Float)object);
        }
        if (object instanceof VsonValue) {
            return this.append((VsonValue) object);
        }
        return this.append(Vson.get().parse(object));
    }

    public VsonArray append(String value) {
        values.add(valueOf(value));
        return this;
    }

    public VsonArray append(VsonValue value) {
        if (value == null) {
            value = VsonLiteral.NULL;
        }
        values.add(value);
        return this;
    }

    public VsonArray remove(int index) {
        values.remove(index);
        return this;
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public VsonValue get(int index) {
        return values.get(index);
    }

    public List<VsonValue> values() {
        return Collections.unmodifiableList(values);
    }

    public Iterator<VsonValue> iterator() {
        return values.iterator();
    }

    @Override
    public VsonType getType() {
        return VsonType.ARRAY;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public VsonArray asArray() {
        return this;
    }

    @Override
    public int hashCode() {
        return values.hashCode();
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
        VsonArray other=(VsonArray)object;
        return values.equals(other.values);
    }


    public List<VsonValue> getValues() {
        return values;
    }
}
