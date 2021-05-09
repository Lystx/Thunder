
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
- ThunderListenet (#listener)
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
