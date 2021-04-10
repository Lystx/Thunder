
package io.lightning.network.utility.exposed.predicate;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface CharPredicate {

    boolean test(char value);

    default CharPredicate and(CharPredicate other) {
        Objects.requireNonNull(other);
        return value -> test(value) && other.test(value);
    }

    default CharPredicate negate() {
        return value -> !test(value);
    }

    default CharPredicate or(CharPredicate other) {
        Objects.requireNonNull(other);
        return value -> test(value) || other.test(value);
    }
}
