package io.vera.server.command;

import io.vera.plugin.Plugin;
import io.vera.plugin.PluginDesc;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class DummyPlugin extends Plugin {
  private static final AtomicInteger used0 = new AtomicInteger();
  
  private static final AtomicInteger used1 = new AtomicInteger();
  
  public static final Plugin MINECRAFT_INST = new DummyPlugin("minecraft", "Minecraft");
  
  private DummyPlugin(final String id, final String display) {
    if (!Arrays.<String>asList(new String[] { "trident", "minecraft" }).contains(id))
      throw new IllegalArgumentException("only trident and minecraft are allowed ids"); 
    if (!used0.compareAndSet(0, 1) && !used1.compareAndSet(0, 1))
      throw new IllegalArgumentException("invalid registration"); 
    PluginDesc pluginDesc = new PluginDesc() {
        public boolean equals(Object obj) {
          return (obj == this);
        }
        
        public int hashCode() {
          return id().hashCode();
        }
        
        public String toString() {
          return id();
        }
        
        public Class<? extends Annotation> annotationType() {
          return (Class)PluginDesc.class;
        }
        
        public String id() {
          return id;
        }
        
        public String name() {
          return display;
        }
        
        public String version() {
          return "1.0.0";
        }
        
        public String author() {
          return "VeraSDK Team";
        }
        
        public String[] depends() {
          return new String[0];
        }
      };
    try {
      Field f = Plugin.class.getDeclaredField("description");
      f.setAccessible(true);
      f.set(this, pluginDesc);
    } catch (ReflectiveOperationException ex) {
      ex.printStackTrace();
    } 
  }
}
