import io.thunder.manager.packet.ThunderPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class TestUnbufferedPacket extends ThunderPacket {

    private final String name;
    private final int age;
}
