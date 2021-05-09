import io.thunder.utils.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

public class SerializerTest {

    public static void main(String[] args) {
        Serializer<ExampleObject> serializer = new Serializer<>(new ExampleObject("Hans", UUID.randomUUID()));


        final String serialize = serializer.serialize();


        System.out.println(serialize);


        ExampleObject object = serializer.deserialize(serialize);


        System.out.println(object.getUniqueId());

    }



    @Getter @AllArgsConstructor
    public static class ExampleObject implements Serializable {

        private final String name;
        private final UUID uniqueId;

    }
}
