
What is Thunder?

Thunder is an open-source and easy to use Java Networking Framework 
for Java 1.8 and higher
Thunder uses TCP for guranteed data transfer 
Thunder is thread safe and supports Compression of Packets.
  
---------

Content:

- [Understanding the Basics](#basics)
- [ThunderServer](#thunderserver) 
- [ThunderClient](#thunderclient)
- [Using a ThunderListener](#thunderlistener)
- [Creating and Sending Packets](#packets)
- [Handling Packets](#packethandling)
- [Response System](#response-system)

---------

## Basics:


The ThunderServer and the ThunderClient are based on the ThunderConnection
The ThunderConnection has its own unique Session to identify it.
You can get it by using ThunderConnection#getSession() 

---------

## ThunderServer:

You can create a ThunderServer as the following.
The Server is used to handle all Connections (ThunderClients).

```Java

ThunderServer thunderServer = Thunder.createServer();
thunderServer.start(1401).perform();

```


---------

## ThunderClient:

The ThunderClient connects to the ThunderServer and transfers data from one connection to another.

```Java

ThunderClient thunderClient = Thunder.createClient();
thunderClient.connect("127.0.0.1", 1401).perform();

```

---------

## ThunderListener:

The ThunderListener implements three methods to listen for events.
The Listener can be add to a ThunderConnection (so either ThunderClient or ThunderServer)

```Java


thunderClient.addSessionListener(new ThunderListener() {
    @Override
    public void handleConnect(ThunderSession session) {
        System.out.println("[Client] Connected to ThunderServer (" + System.currentTimeMillis() + ")");
    }

    @Override
    public void handleHandshake(PacketHandshake handshake) {
        System.out.println("[Client] Received HandShake from ThunderServer! (" + System.currentTimeMillis() + ")");
     }

    @Override
    public void handlePacketSend(Packet packet) {
        System.out.println("[Client] Sending " + packet.getClass().getSimpleName() + "... (" + System.currentTimeMillis() + ")");
    }

    @Override
    public void handlePacketReceive(Packet packet) {
        System.out.println("[Client] Received " + packet.getClass().getSimpleName() + "! (" + System.currentTimeMillis() + ")");
     }

    @Override
    public void handleDisconnect(ThunderSession session) {
        System.out.println(thunderClient.toString());
    }
    
});

```

---------

## Packets:

Packets are the objects that contain the data which is getting transferred between ThunderConnections.
Packet.class is abstract and contains two default methods.
Packet#write(PacketBuffer) and Packet#read(PacketBuffer).

Every Packet has its own UUID, a protocolVersion, a protocolId, and other values.
An example Packet could look like the following:

```Java

@Getter @AllArgsConstructor
public class SamplePacket extends Packet {

    private String name;
    private int age;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(name);
        buf.writeInt(age);
    }

    @Override
    public void read(PacketBuffer buf) {
        name = buf.readString();
        age = buf.readInt();
    }

}

```

The attributes inside of a Packet must be read and written from or into the PacketBuffer.
A Packet can be send through a ThunderConnection.

```Java

SamplePacket samplePacket = new SamplePacket("Name", 32);
thunderConnection.sendPacket(samplePacket);

```

---------

## PacketHandling:

When receiving Packets from a different ThunderConnection you can handle the received Packet
with the integrated PacketAdapter and register a PacketHandler to handle the Packet.

```Java

thunderConnection.addPacketHandler(new PacketHandler() {
       @Override
       public void handle(ThunderPacket packet) {
           //Check if packet is SamplePacket
           if (packet instanceof SamplePacket) {
              SamplePacket samplePacket = (SamplePacket) packet;
              System.out.println(samplePacket.getAge());
              System.out.println(samplePacket.getName());
              System.out.println(samplePacket.getProcessingTime() + "ms"); //The time the packet took
              System.out.println(samplePacket.toString()); //Information on the Packet
          }
      }
  });

```

---------

## Response System:

The PacketSystem takes advantage of a Response-based System.
You can await for a Response when sending a Packet.
You can respond to a Packet with a ResponseStatus and/or a Message.

An example could look like this:

(Waiting for the Response with Consumer)
```Java

thunderConnection.sendPacket(new SamplePacket("Luca", 16), new Consumer<Response>() {
      @Override
      public void accept(Response response) {
          System.out.println(response.getStatus() + " - " + response.getMessage() + " [" + response.getProcessingTime() + "ms]");
      }
  });

```

(Waiting for the Response without Consumer)
```Java

SamplePacket samplePacket = new SamplePacket("Name", 16);
Response response = thunderConnection.transferToResponse(samplePacket);
System.out.println(response.getStatus() + " - " + response.getMessage());

```

(Responding to the Packet)
```Java

thunderConnection.addPacketHandler(new PacketHandler() {
       @Override
       public void handle(ThunderPacket packet) {
           //Check if packet is SamplePacket
           if (packet instanceof SamplePacket) {
             packet.respond(ResponseStatus.SUCCESS, "Test Message");
          }
      }
  });

```
