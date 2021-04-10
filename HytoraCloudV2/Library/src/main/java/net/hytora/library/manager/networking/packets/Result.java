package net.hytora.library.manager.networking.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @RequiredArgsConstructor
public class Result<R> implements Serializable {

    private final UUID uniqueId;
    private final R result;
    private Throwable throwable;

}
