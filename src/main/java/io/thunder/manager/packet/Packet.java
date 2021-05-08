package io.thunder.manager.packet;


import io.thunder.connection.ThunderConnection;
import io.thunder.manager.packet.response.PacketRespond;
import io.thunder.manager.packet.response.ResponseStatus;
import io.vson.annotation.other.Vson;
import io.vson.enums.FileFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.*;


/**
 * This is class is the better usage
 * of (de-)serializing Packet-Data
 *
 * You have to work with the {@link PacketBuffer} to (de-)serialize your
 * objects in the packet
 */
@Setter @Getter
public abstract class Packet {

    /**
     * The time this Packet took
     * to get sent from
     * Server <--> Client
     */
    private long processingTime;

    /**
     * The data of this packet
     * It stores all data
     */
    private byte[] data = new byte[9999];

    /**
     * The Unique ID of this packet
     * Used for identifying this Packet
     */
    private UUID uniqueId = UUID.randomUUID();


    /**
     * The protocolVersion of this Packet
     * Used for security to check for right packets
     */
    protected int protocolVersion = 0;

    /**
     * The protocolId of this Packet
     * Used for security to check for right packets
     */
    protected int protocolId = -1;

    /**
     * The connection that handles this
     * Packet over the whole time
     */
    private ThunderConnection connection;


    /**
     * Will be called when the packet is received and fully
     * constructed.
     * This can also be used to call {@link Packet#respond(PacketRespond)}
     * so you can respond within the Packet directly without
     * having to register an extra class for it and listen for the {@link Packet}
     *
     * @param thunderConnection the connection who receives
     */
    public void handle(ThunderConnection thunderConnection) {}

    /**
     * Will be called if the packet will be send
     * You will add all the data to the given PacketBuffer
     *
     * @param buf the {@link PacketBuffer} you append your Data to
     */
    public abstract void write(PacketBuffer buf);

    /**
     * Will be called when the Packet gets created (when it's getting read)
     * Here you should set the values from the given PacketReader otherwise
     * the values (attributes) will be null, -1 or false (depends on the ParameterType)
     *
     * @param buf the {@link PacketBuffer} you get the Information from
     */
    public abstract void read(PacketBuffer buf);


    /**
     * Respond to the packet with a {@link PacketRespond}
     *
     * @param packet  The packets to send as respond
     */
    public void respond(PacketRespond packet) {

        //Copies the packet 1:1
        packet.setUniqueId(this.uniqueId);
        packet.setConnection(this.connection);
        packet.setData(this.data);
        packet.setProtocolId(this.protocolId);
        packet.setProtocolVersion(this.protocolVersion);
        packet.setProcessingTime(this.processingTime);

        //Sends the packet over the connection of this Packet
        packet.getConnection().sendPacket(packet);
    }

    /**
     * Responds to the Packet with a {@link ResponseStatus}
     *
     * @param status the status you want to respond
     */
    public void respond(ResponseStatus status) {
        this.respond(status, "No message provided");
    }

    /**
     * Responds to the Packet with a {@link ResponseStatus} and a message
     *
     * @param status the status you want to respond
     * @param message the message you want to respond
     */
    public void respond(ResponseStatus status, String message) {
        this.respond(new PacketRespond(message, status));
    }

    /**
     * Responds to the Packet with a {@link ResponseStatus} and an object
     * (But the object will be serialized to a String)
     *
     * @param status the status you want to respond
     * @param object the object you want to respond
     */
    public void respond(ResponseStatus status, Object object) {
        this.respond(status, Vson.get().parse(object).toString(FileFormat.RAW_JSON));
    }

    @Override
    public String toString() {
        return "";
    }
}
