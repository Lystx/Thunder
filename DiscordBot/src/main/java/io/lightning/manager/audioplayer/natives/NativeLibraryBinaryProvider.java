package io.lightning.manager.audioplayer.natives;

import io.lightning.manager.audioplayer.natives.architecture.SystemType;
import java.io.InputStream;

public interface NativeLibraryBinaryProvider {

    InputStream getLibraryStream(SystemType systemType, String libraryName);
}
