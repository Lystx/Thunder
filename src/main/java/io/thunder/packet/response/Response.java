package io.thunder.packet.response;

import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.SneakyThrows;


@Getter
public class Response {

    private final PacketRespond respondPacket;
    private final ResponseStatus status;
    private final String message;
    private final long processingTime;

    public Response(PacketRespond respond) {
        this.respondPacket = respond;
        this.status = respond.getStatus();
        this.message = respond.getMessage();
        this.processingTime = respond.getProcessingTime();
    }

    public Response(ResponseStatus status) {
        this(new PacketRespond(status));
    }

    @SneakyThrows
    public <T> T transform(Class<T> tClass) {
        return new VsonObject(this.message).getAs(tClass);
    }

    @SneakyThrows
    public <T> T transform(String key, Class<T> tClass) {
        return new VsonObject(this.message).getObject(key, tClass);
    }


}
