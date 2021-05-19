package io.thunder.utils.vson.elements.object;

import io.thunder.utils.vson.VsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class VsonMember implements Serializable {

    private final String name;
    private final VsonValue value;

}
