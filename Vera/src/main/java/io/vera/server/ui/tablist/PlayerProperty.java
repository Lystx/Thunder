package io.vera.server.ui.tablist;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PlayerProperty {
    private final String name;
    @NonNull
    private final String value;
    private final String signature;
}
