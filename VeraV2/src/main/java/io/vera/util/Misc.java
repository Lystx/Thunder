package io.vera.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Misc {

    public static final String NBT_BOUND_FAIL = "NBT value out of range for class %s";
    public static final String HOME = System.getProperty("user.dir");
    public static final Path HOME_PATH = Paths.get(HOME, new String[0]);
}
