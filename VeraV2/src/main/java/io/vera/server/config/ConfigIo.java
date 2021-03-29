package io.vera.server.config;

import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonType;
import io.vson.manage.vson.VsonParser;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ConfigIo {
  public static void exportResource(Path dest, String resource) {
    InputStream stream = ConfigIo.class.getResourceAsStream(resource);
    try {
      Files.copy(stream, dest, new java.nio.file.CopyOption[0]);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static VsonObject readConfig(Path path) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (FileInputStream stream = new FileInputStream(path.toFile())) {
      byte[] buffer = new byte[8192];
      while (stream.read(buffer, 0, buffer.length) > -1)
        out.write(buffer, 0, buffer.length); 
    } 
    String json = out.toString().trim();
    return (new VsonParser()).parse(json).asVsonObject();
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
        return Integer.valueOf(element.asInt());
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
    if (o instanceof Double)
      return VsonValue.valueOf(((Double)o).doubleValue()); 
    if (o instanceof Float)
      return VsonValue.valueOf(((Float)o).floatValue()); 
    if (o instanceof Long)
      return VsonValue.valueOf(((Long)o).longValue()); 
    if (o instanceof Integer)
      return VsonValue.valueOf(((Integer)o).intValue()); 
    if (o instanceof String)
      return VsonValue.valueOf((String)o); 
    if (o instanceof Boolean)
      return VsonValue.valueOf(((Boolean)o).booleanValue()); 
    if (o instanceof VsonValue)
      return (VsonValue)o; 
    throw new RuntimeException("Objects must be preformatted as JsonObjects: " + o.getClass());
  }
}
