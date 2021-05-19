package io.thunder.packet.impl.response;

import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.UUID;

/**
 * This class is used to receive a Response
 * from a Packet after it responded
 */
@Getter
public class Response {

    /**
     * The Packet you get all your data from
     */
    private final PacketRespond respondPacket;

    /**
     * The Status of the Response
     */
    private final ResponseStatus status;

    /**
     * The message (Serialized Data or plain message)
     */
    private final String message;

    /**
     * The time it took to process
     */
    private final long processingTime;


    private int pos = -1;

    /**
     * If the Request has timed out
     */
    private boolean timedOut;

    /**
     * Settign all values using a {@link PacketRespond}
     *
     * @param respond the packet
     */
    public Response(PacketRespond respond) {
        this.respondPacket = respond;
        this.status = respond.getStatus();
        this.message = respond.getMessage();
        this.processingTime = respond.getProcessingTime();
        this.timedOut = this.message.equalsIgnoreCase("The Request timed out");

    }

    /**
     * Constructs the Response
     * by using a {@link ResponseStatus}
     *
     * @param status the status
     */
    public Response(ResponseStatus status) {
        this(new PacketRespond(status));
    }


    /**
     * Sets current position and prepares to use all the other Methods like {@link Response#asString()} etc.
     *
     * @param pos the position
     * @return current Response
     */
    public Response get(int pos) {
        this.pos = pos;
        return this;
    }

    /**
     * Returns value of given positon as String
     * Can only be used if the {@link Response#get(int)} was used before to declare the position
     *
     * @return value
     */
    public String asString() {
        return this.asCustom(String.class);
    }

    /**
     * Returns value of given positon as long
     * Can only be used if the {@link Response#get(int)} was used before to declare the position
     *
     * @return value
     */
    public long asLong() {
        return this.asCustom(Long.class);
    }

    /**
     * Returns value of given positon as int
     * Can only be used if the {@link Response#get(int)} was used before to declare the position
     *
     * @return value
     */
    public int asInt() {
        return this.asCustom(Integer.class);
    }

    /**
     * Returns value of given positon as boolean
     * Can only be used if the {@link Response#get(int)} was used before to declare the position
     *
     * @return value
     */
    public boolean asBoolean() {
        return this.asCustom(Boolean.class);
    }

    /**
     * Returns value of given positon as uuid
     * Can only be used if the {@link Response#get(int)} was used before to declare the position
     *
     * @return value
     */
    public UUID asUUID() {
        return this.asCustom(UUID.class);
    }

    /**
     * This transforms the Object of the given position (before marked at {@link Response#get(int)} ) to
     * your a List containing the tClass Objects
     *
     * @param tClass the class of the object
     * @return value
     */
    @SneakyThrows
    public <T> List<T> asList(Class<T> tClass) {
        return new VsonObject(this.message).getList(String.valueOf(this.pos), tClass);
    }

    /**
     * This transforms the Object of the given position (before marked at {@link Response#get(int)} ) to
     * your Class<T> to receive the object you want
     *
     * @param tClass the class of the object
     * @return value
     */
    @SneakyThrows
    public <T> T asCustom(Class<T> tClass) {
        return new VsonObject(this.message).getObject(String.valueOf(this.pos), tClass);
    }

    /**
     * Returns this Response's data transformed
     * into a {@link VsonObject} and then parsed into a String
     *
     * @return data as String
     */
    public String toString() {
        VsonObject vsonObject = new VsonObject();

        vsonObject.append("status", this.status);
        vsonObject.append("message", this.message);
        vsonObject.append("packet", respondPacket);

        return vsonObject.toString(FileFormat.JSON);
    }



    /**
     * Returns an Empty response without being able to return
     * a given Object
     * @return iResponse
     */
    public <T> IResponse<T> toIResponse() {
        return this.toIResponse(null);
    }

    /**
     * Transforms this {@link Response} to a {@link IResponse}
     *
     * @param object the object to return in the {@link IResponse}
     * @return iResponse
     */
    public <T> IResponse<T> toIResponse(T object) {
        return new IResponse<T>() {
            @Override
            public T get() {
                return object;
            }

            @Override
            public ResponseStatus getStatus() {
                return status;
            }

            @Override
            public UUID getUniqueId() {
                return respondPacket.getUniqueId();
            }

            @Override
            public String getMessage() {
                return Response.this.getMessage();
            }

            @Override
            public Response raw() {
                return Response.this;
            }
        };
    }
}
