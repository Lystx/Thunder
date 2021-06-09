package io.thunder.utils.objects;

import lombok.*;

@Getter @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ThunderOption<T> {

    /**
     * If handshakes should be ignored after failed
     * to get response of the handshake
     */
    public static final ThunderOption<Boolean> IGNORE_HANDSHAKE_IF_FAILED = new ThunderOption<>(0x00);

    /**
     * The IPTos option (default 24)
     */
    public static final ThunderOption<Integer> IP_TOS = new ThunderOption<>(0x01);

    /**
     * If using TCP or UDP
     */
    public static final ThunderOption<Boolean> USE_TCP = new ThunderOption<>(0x02);

    @Setter @Getter
    private T value;

    private final int id;
}
