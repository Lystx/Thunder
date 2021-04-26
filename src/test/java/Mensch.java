import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter @AllArgsConstructor @ToString
public class Mensch {

    private final String name;
    private final String nachname;
    private final int alter;
    private final UUID uniqueId;
}
