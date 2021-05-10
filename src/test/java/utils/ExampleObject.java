package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor @Getter
public class ExampleObject {

    private final String name;
    private final UUID uniqueId;
    private final long stamp;
    private final List<String> data;
}
