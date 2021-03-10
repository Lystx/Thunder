
package io.vera.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.vera.doc.Policy;

import javax.annotation.concurrent.Immutable;

/**
 * A class containing two separate data values
 * Usually used in return statements or as values in maps
 *
 * <p>Tuples CANNOT be used as keys in Maps on account for
 * the fact that all constructed Tuples are completely
 * unique.</p>
 *
 * @param <T> The first (A) instance type
 * @param <M> The second (B) instance type
 */
@Immutable
@Getter
@RequiredArgsConstructor
@Policy("no hashCode/equals, will break Cache functionality")
public class Tuple<T, M> {
    private final T a;
    private final M b;
}