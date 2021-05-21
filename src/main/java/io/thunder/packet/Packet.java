package io.thunder.packet;


import io.thunder.Thunder;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.connection.data.ThunderChannel;
import io.thunder.impl.other.ProvidedPacketBuffer;
import io.thunder.packet.impl.EmptyPacket;
import io.thunder.utils.logger.LogLevel;
import io.thunder.packet.impl.response.PacketRespond;
import io.thunder.packet.impl.response.ResponseStatus;
import io.thunder.utils.vson.elements.object.VsonObject;
import io.thunder.utils.vson.enums.FileFormat;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * This is class is the better usage
 * of (de-)serializing Packet-Data
 *
 * You have to work with the {@link ProvidedPacketBuffer} to (de-)serialize your
 * objects in the packet
 */
@Setter @Getter
public abstract class Packet {

    /**
     * The time this Packet took
     * to get sent from
     * Server <--> Client
     */
    protected long processingTime;

    /**
     * The data of this packet
     * It stores all data
     */
    protected byte[] data = new byte[9999];

    /**
     * The Unique ID of this packet
     * Used for identifying this Packet
     */
    protected UUID uniqueId = UUID.randomUUID();

    /**
     * The protocolVersion of this Packet
     * Used for security to check for right packets
     */
    protected int protocolVersion = 47;

    /**
     * The protocolId of this Packet
     * Used for security to check for right packets
     */
    protected int protocolId = 6;

    /**
     * The connection that handles this
     * Packet over the whole time
     */
    protected ThunderConnection connection;

    /**
     * The Channel of this Packet
     * that sends the Packet
     */
    protected ThunderChannel channel;

    /**
     * Cancels to send the packet
     */
    private boolean cancelled = false;

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
     * Sends this {@link Packet} to the given {@link ThunderChannel}
     *
     * @param channel the channel to send it to
     */
    public void sendAndFlush(ThunderChannel channel) {
        if (channel == null) {
            Thunder.LOGGER.log(LogLevel.ERROR, "Could not Execute " + getClass().getSimpleName() + "#sendAndFlush because the Channel for the Packet is null!");
            return;
        }
        this.channel = channel;
        channel.processOut(this);
        channel.flush();
    }

    /**
     * Sends the {@link Packet} with this channel
     * (Might be null at some point)
     */
    public void sendAndFlush() {
        this.sendAndFlush(this.channel);
    }

    /**
     * Respond to the packet with a {@link PacketRespond}
     *
     * @param packet  The packets to send as respond
     */
    public void respond(PacketRespond packet) {
        //Sends the packet over the connection of this Packet
        PacketRespond packetRespond = this.deepCopy(packet);
        this.channel.processOut(packetRespond);
    }

    /**
     * DeepCopies a Packet but does not copy the data-array
     * So all the values (as UUID for response) are the same but the Packet
     * data and values stay the same
     *
     * @param packet the packet to copy
     * @return copied Packet
     */
    public <T extends Packet> T deepCopy(T packet) {

        //Copies the packet 1:1
        packet.setUniqueId(this.uniqueId);
        packet.setChannel(this.channel);
        packet.setConnection(this.connection);
        packet.setProtocolId(this.protocolId);
        packet.setProtocolVersion(this.protocolVersion);
        packet.setProcessingTime(this.processingTime);

        return packet;
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
    public void respond(ResponseStatus status, Object... object) {

        VsonObject vsonObject = new VsonObject();
        for (int i = 0; i < object.length; i++) {
            vsonObject.append(String.valueOf(i), object[i]);
        }

        this.respond(status, vsonObject.toString(FileFormat.RAW_JSON));
    }

    @Override
    public String toString() {
        return "(Packet : [" + getClass().getSimpleName() + "] UUID : [" + getUniqueId() + "] ProtocolID : [" + getProtocolId() + "] ProtocolVersion: [" + getProtocolVersion() + "] Time : [" + getProcessingTime() + " ms] Data : [" + getData().length + " bytes])";
    }

    /**
     * Get the address of the channel
     *
     * @return The address
     */
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) getChannel().remoteAddress();
    }

    /**
     * Creates a new {@link Packet}
     *
     * @return new packet instance
     */
    public static Packet newInstance() {
        return new EmptyPacket();
    }
}
