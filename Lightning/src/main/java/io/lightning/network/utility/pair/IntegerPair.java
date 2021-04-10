
package io.lightning.network.utility.pair;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter @AllArgsConstructor
public class IntegerPair<V> {

    private final int key;
    private final V value;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntegerPair<?>)) {
            return false;
        }
        
        IntegerPair<?> pair = (IntegerPair<?>) o;
        
        return key == pair.key && Objects.equals(value, pair.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
    
    @Override
    public String toString() {
        return "IntPair[key: " + key + ", value: " + value + "]";
    }
}
