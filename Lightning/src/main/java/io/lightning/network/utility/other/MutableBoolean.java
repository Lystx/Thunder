
package io.lightning.network.utility.other;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
public class MutableBoolean {

    private boolean value;

    public boolean get() {
        return value;
    }

    public MutableBoolean set(boolean value) {
        this.value = value;
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MutableBoolean)) {
            return false;
        }
        
        return ((MutableBoolean) o).value == value;
    }
    
    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }
    
    @Override
    public String toString() {
        return "MutableBoolean[value = " + value + "]";
    }
}
