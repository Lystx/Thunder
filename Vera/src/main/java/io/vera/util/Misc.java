
package io.vera.util;

import javax.annotation.concurrent.Immutable;
import java.nio.file.Path;
import java.nio.file.Paths;

@Immutable
public final class Misc {
    public static final String NBT_BOUND_FAIL = "NBT value out of range for class %s";
    public static final String HOME = System.getProperty("user.dir");
    public static final Path HOME_PATH = Paths.get(HOME);

    private Misc() {
    }
}