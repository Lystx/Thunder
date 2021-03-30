package org.gravel.manager;

import lombok.Getter;
import org.gravel.GravelServer;

import java.io.File;

@Getter
public class FileManager {

    private final GravelServer gravelServer;
    private final File directory;

    private final File userFile;
    private final File accountFile;
    private final File chatFile;

    public FileManager(GravelServer gravelServer) {
        this.gravelServer = gravelServer;

        this.directory = new File("./local/"); this.directory.mkdirs();

        this.userFile = new File(this.directory, "users.gravel");
        this.accountFile = new File(this.directory, "accounts.gravel");
        this.chatFile = new File(this.directory, "chats.gravel");
    }

}
