package net.hytora.library.elements.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class PermissionGroup implements Serializable {

    private final String name;
    private final int id;
    private final List<String> permissions;
    private final List<PermissionGroup> inheritances;
}
