package org.gravel.library.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Setter @Getter
public class Value<T> {

    private T value;

    public Value() {
        this(null);
    }

    public void increase() {
        if (this.value instanceof Integer) {
            Integer integer = (Integer)this.value;
            Integer finalInt = integer + 1;
            this.value = (T)finalInt;
        } else {
            throw new UnsupportedOperationException("Can't increase value of type " + this.value.getClass().getSimpleName());
        }
    }

    public void change() {
        if (this.value instanceof Boolean) {
            Boolean aBoolean = (Boolean)this.value;
            aBoolean = !aBoolean;
            this.value = (T)aBoolean;
        } else {
            throw new UnsupportedOperationException("Can't increase value of type " + this.value.getClass().getSimpleName());
        }
    }

    public void decrease() {
        if (this.value instanceof Integer) {
            Integer integer = (Integer)this.value;
            Integer finalInt = integer +- 1;
            this.value = (T)finalInt;
        } else {
            throw new UnsupportedOperationException("Can't decrease value of type " + this.value.getClass().getSimpleName());
        }
    }
}
