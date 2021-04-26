package io.thunder.annotation;

import io.thunder.manager.packet.ThunderPacket;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = TYPE)
public @interface PacketData {

    Class<? extends ThunderPacket> packet();

    int id();

    boolean compress();

    String[] usage();

}
