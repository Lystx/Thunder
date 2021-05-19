
package io.thunder.utils.vson.other;

import io.thunder.utils.vson.VsonValue;

public interface IVsonProvider {

    String getName();

    VsonValue parse(String text);

    String toString(VsonValue value);
}
