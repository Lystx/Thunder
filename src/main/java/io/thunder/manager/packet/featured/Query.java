package io.thunder.manager.packet.featured;

import io.vson.elements.object.VsonObject;
import lombok.Setter;

import java.io.Serializable;
/**
 * This class is the Query for the {@link QueryPacket}
 * to get more Information
 */
@Setter
public class Query implements Serializable {

    private final VsonObject result;

    public Query(VsonObject result) {
        this.result = result;
    }

    /**
     * Returns the Result als {@link VsonObject}
     *
     * @return Result
     */
    public VsonObject get() {
        return this.result;
    }

    /**
     * Adds an alternative Value to return if the
     * current Result is not set yet
     *
     * @param jsonObject the JsonObject to return
     * @return JsonObject
     */
    public VsonObject orElse(VsonObject jsonObject) {
        return (this.result == null ? jsonObject : this.result);
    }

}
