package io.lightning.manager.audioplayer.natives;

import io.lightning.manager.audioplayer.natives.architecture.SystemType;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceNativeLibraryBinaryProvider implements NativeLibraryBinaryProvider {

    private static final Logger log = LoggerFactory.getLogger(ResourceNativeLibraryBinaryProvider.class);

    private final Class<?> classLoaderSample;
    private final String nativesRoot;

    public ResourceNativeLibraryBinaryProvider(Class<?> classLoaderSample, String nativesRoot) {
        this.classLoaderSample = classLoaderSample != null ? classLoaderSample : ResourceNativeLibraryBinaryProvider.class;
        this.nativesRoot = nativesRoot;
    }

    @Override
    public InputStream getLibraryStream(SystemType systemType, String libraryName) {
        String resourcePath = nativesRoot + systemType.formatSystemName() + "/" + systemType.formatLibraryName(libraryName);

        log.debug("Native library {}: trying to find from resources at {} with {} as classloader reference", libraryName,
                resourcePath, classLoaderSample.getName());

        return classLoaderSample.getResourceAsStream(resourcePath);
    }
}
