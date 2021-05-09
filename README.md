
What is Thunder?

Thunder is an open-source and easy to use Java Networking Framework 
for Java 1.8 and higher
Thunder uses TCP for guranteed data transfer or TLS if you want to use encryption.
Thunder is thread safe and supports Compression of Packets.
  
---------

Content:

- Basics (#basics)
- ThunderServer (#server)
- ThunderClient (#client)
- ThunderListener (#listener)
- Packets (#packets)
- Response System (#response)

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

ThunderServer thunderServer = Thunder.createServer(new ThunderListener() {
       @Override
       public void handlePacket(Packet packet, ThunderConnection thunderConnection) throws IOException {
           System.out.println(packet.toString());
       }

       @Override
       public void handleConnect(ThunderConnection thunderConnection) {

       }

       @Override
       public void handleDisconnect(ThunderConnection thunderConnection) {

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

---------
