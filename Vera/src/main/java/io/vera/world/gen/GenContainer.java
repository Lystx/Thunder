
package io.vera.world.gen;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.Executor;


@Immutable
public interface GenContainer extends Executor {

    GenContainer DEFAULT = (c) -> {
        throw new RuntimeException();
    };

    GenContainer ARBITRARY = (c) -> {
        throw new RuntimeException();
    };
}