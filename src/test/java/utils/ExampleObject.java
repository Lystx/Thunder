package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor @Getter @ToString
public class ExampleObject implements Serializable {

    private final String name;
    private final UUID uniqueId;
    private final long stamp;
    private final List<String> data;


    public static ExampleObject newInstance() {
        return new ExampleObject("Lystx", UUID.randomUUID(), System.currentTimeMillis(), Arrays.asList("a", "b", "c", "d", "e", "f"));
    }
}
