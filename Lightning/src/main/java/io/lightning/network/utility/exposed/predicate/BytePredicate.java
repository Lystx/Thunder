
package io.lightning.network.utility.exposed.predicate;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface BytePredicate {

    boolean test(byte value);

    default BytePredicate and(BytePredicate other) {
        Objects.requireNonNull(other);
        return value -> test(value) && other.test(value);
    }
    
    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     * predicate
     */
    default BytePredicate negate() {
        return value -> !test(value);
    }

    default BytePredicate or(BytePredicate other) {
        Objects.requireNonNull(other);
        return value -> test(value) || other.test(value);
    }
}
