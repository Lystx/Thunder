package utils;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.UUID;

@Getter @ToString
public class BaseObject extends ExampleObject {

    private final int id;

    public BaseObject(int id) {
        super("Base", UUID.randomUUID(), System.currentTimeMillis(), Arrays.asList("fjasf", "ufhas"));
        this.id = id;
    }


}
