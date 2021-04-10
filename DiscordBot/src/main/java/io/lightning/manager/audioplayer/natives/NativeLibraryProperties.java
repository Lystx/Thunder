package io.lightning.manager.audioplayer.natives;

public interface NativeLibraryProperties {

    String getLibraryPath();

    String getLibraryDirectory();

    String getExtractionPath();

    String getSystemName();

    String getLibraryFileNamePrefix();

    String getLibraryFileNameSuffix();

    String getArchitectureName();
}
