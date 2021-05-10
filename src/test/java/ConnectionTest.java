import io.thunder.Thunder;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderServer;
import io.thunder.manager.logger.LogLevel;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.response.Response;
import io.thunder.packet.response.ResponseStatus;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Consumer;

public class ConnectionTest {


    public static void main(String[] args) {


        ThunderServer thunderServer = Thunder.createServer();
        ThunderClient thunderClient = Thunder.createClient();

        thunderServer.start(1758).perform();
        thunderServer.addPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                //.........
                int coins = 3000;
                long firstLogin = new Date().getTime();

                packet.respond(ResponseStatus.SUCCESS, coins, firstLogin);

            }
        });

        thunderClient.connect("localhost", 1758).perform(new Consumer<ThunderClient>() {
            @Override
            public void accept(ThunderClient client) {
                PacketDatabase packetDatabase = new PacketDatabase("admin", UUID.randomUUID());

                Response response = client.transferToResponse(packetDatabase);
                String message = response.getMessage();

                int coins = response.transform("0", int.class);
                long firstLogin = response.transform("1", long.class);


                System.out.println(coins + " - " + firstLogin);
            }
        });

    }
}
