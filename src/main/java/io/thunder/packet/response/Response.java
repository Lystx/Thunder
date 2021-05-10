package io.thunder.packet.response;

import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import lombok.Getter;
import lombok.SneakyThrows;

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
     * Transforms this Response into an Object
     * with given class
     *
     * @param tClass the class of the Object
     * @return transformed object
     */
    @SneakyThrows
    public <T> T transform(Class<T> tClass) {
        return new VsonObject(this.message).getAs(tClass);
    }

    /**
     * Transforms a object from the Response into an Object
     *
     * @param key name of the object
     * @param tClass class of the object
     * @return transformed Object
     */
    @SneakyThrows
    public <T> T transform(int position, Class<T> tClass) {
        return new VsonObject(this.message).getObject(String.valueOf(position), tClass);
    }


    public String toString() {
        VsonObject vsonObject = new VsonObject();

        vsonObject.append("status", this.status);
        vsonObject.append("message", this.message);
        vsonObject.append("packet", respondPacket);

        return vsonObject.toString(FileFormat.JSON);
    }

}
