package io.thunder.manager.packet.featured;

import com.google.gson.JsonObject;
import lombok.Setter;

import java.io.Serializable;
/**
 * This class is the Query for the {@link QueryPacket}
 * to get more Information
 */
@Setter
public class Query implements Serializable {

    private final JsonObject result;

    public Query(JsonObject result) {
        this.result = result;
    }

    /**
     * Returns the Result als {@link JsonObject}
     *
     * @return Result
     */
    public JsonObject get() {
        return this.result;
    }

    /**
     * Adds an alternative Value to return if the
     * current Result is not set yet
     *
     * @param jsonObject the JsonObject to return
     * @return JsonObject
     */
    public JsonObject orElse(JsonObject jsonObject) {
        return (this.result == null ? jsonObject : this.result);
    }

}
