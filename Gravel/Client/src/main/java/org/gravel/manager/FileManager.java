package org.gravel.manager;

import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;

@Getter
public class FileManager {

    private final File directory;
    private final VsonObject config;

    @SneakyThrows
    public FileManager() {
        String path;
        if (System.getProperty("os.name").startsWith("Win")) {
            path = System.getProperty("user.home") + "\\AppData\\Roaming";
        } else {
            path = "~/.messenger";
        }
        this.directory = new File(path, "GravelMessenger/"); this.directory.mkdirs();

        this.config = new VsonObject(new File(this.directory, "config.vson"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        this.config.getBoolean("registered", false);
        this.config.save();
    }
}
