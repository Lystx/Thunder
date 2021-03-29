package io.betterbukkit.provider.addon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter @AllArgsConstructor
public class AddonInfo {

    private final String name;
    private final String author;
    private final List<String> commands;
    private final String version;
}
