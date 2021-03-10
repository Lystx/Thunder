
package io.vera.server.config;

import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import io.vson.manage.vson.VsonParser;

import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


@Immutable
public final class ConfigIo {

    private ConfigIo() {
    }

    public static void exportResource(Path dest, String resource) {
        InputStream stream = ConfigIo.class.getResourceAsStream(resource);
        try {
            Files.copy(stream, dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static VsonObject readConfig(Path path) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (FileInputStream stream = new FileInputStream(path.toFile())) {
            byte[] buffer = new byte[8192];
            while (stream.read(buffer, 0, buffer.length) > -1) {
                out.write(buffer, 0, buffer.length);
            }
        }

        String json = out.toString().trim();
       return new VsonParser().parse(json).asVsonObject();
    }

    public static void writeConfig(Path path, VsonObject object) throws IOException {
        String json = object.toString();

        try (FileOutputStream stream = new FileOutputStream(path.toFile())) {
            stream.write(json.getBytes());
        }
    }

    public static Object asObj(VsonValue element) {
        switch (element.getType()) {
            case STRING:
                return element.asString();
            case NUMBER:
                return element.asInt();
            case OBJECT:
                throw new RuntimeException("This is a config section");
            case ARRAY:
                return element.asArray();
            case BOOLEAN:
                return element.asBoolean();
            case NULL:
                throw new RuntimeException("Element cannot be null");
        }

        throw new RuntimeException("Cannot parse " + element.getType());
    }

    public static VsonValue asJson(Object o) {
        if (o instanceof Double) {
            return VsonValue.valueOf((Double) o);
        }

        if (o instanceof Float) {
            return VsonValue.valueOf((Float) o);
        }

        if (o instanceof Long) {
            return VsonValue.valueOf((Long) o);
        }

        if (o instanceof Integer) {
            return VsonValue.valueOf((Integer) o);
        }

        if (o instanceof String) {
            return VsonValue.valueOf((String) o);
        }

        if (o instanceof Boolean) {
            return VsonValue.valueOf((Boolean) o);
        }

        if (o instanceof VsonValue) {
            return (VsonValue) o;
        }

        throw new RuntimeException("Objects must be preformatted as JsonObjects: " + o.getClass());
    }
}