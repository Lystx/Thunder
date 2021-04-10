
package io.lightning.network.utility.exposed.predicate;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface FloatPredicate {

    boolean test(float value);

    default FloatPredicate and(FloatPredicate other) {
        Objects.requireNonNull(other);
        return value -> test(value) && other.test(value);
    }

    default FloatPredicate negate() {
        return value -> !test(value);
    }

    default FloatPredicate or(FloatPredicate other) {
        Objects.requireNonNull(other);
        return value -> test(value) || other.test(value);
    }
}
