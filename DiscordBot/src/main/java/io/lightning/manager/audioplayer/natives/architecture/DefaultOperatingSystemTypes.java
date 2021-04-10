package io.lightning.manager.audioplayer.natives.architecture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum DefaultOperatingSystemTypes implements OperatingSystemType {

    LINUX("linux", "lib", ".so"),
    WINDOWS("win", "", ".dll"),
    DARWIN("darwin", "lib", ".dylib"),
    SOLARIS("solaris", "lib", ".so");

    private final String identifier;
    private final String libraryFilePrefix;
    private final String libraryFileSuffix;

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String libraryFilePrefix() {
        return libraryFilePrefix;
    }

    @Override
    public String libraryFileSuffix() {
        return libraryFileSuffix;
    }

    public static OperatingSystemType detect() {
        String osFullName = System.getProperty("os.name");

        if (osFullName.startsWith("Windows")) {
            return WINDOWS;
        } else if (osFullName.startsWith("Mac OS X")) {
            return DARWIN;
        } else if (osFullName.startsWith("Solaris")) {
            return SOLARIS;
        } else if (osFullName.toLowerCase().startsWith("linux")) {
            return LINUX;
        } else {
            throw new IllegalArgumentException("Unknown operating system: " + osFullName);
        }
    }
}
