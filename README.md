
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

---------

## Basics:


The ThunderServer and the ThunderClient are based on the ThunderConnection
The ThunderConnection has its own unique Session to identify it.
You can get it by using ThunderConnection#getSession() 

---------
