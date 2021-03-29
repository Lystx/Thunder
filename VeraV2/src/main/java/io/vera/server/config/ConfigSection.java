package io.vera.server.config;

import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonMember;
import io.vson.elements.object.VsonObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ConfigSection {
  private static final String SEPARATOR_LITERAL = ".";
  
  private static final String SECTION_SEPARATOR = Pattern.quote(".");
  
  private final ConcurrentLinkedStringMap<Object> elements = new ConcurrentLinkedStringMap();
  
  private final String name;
  
  private final ConfigSection parent;
  
  private final ConfigSection root;
  
  public VsonObject write() {
    VsonObject object = new VsonObject();
    this.elements.forEach((k, v) -> {
          if (v instanceof ConfigSection) {
            ConfigSection section = (ConfigSection)v;
            object.append(k, section.write());
          } else {
            object.append(k, ConfigIo.asJson(v));
          } 
        });
    return object;
  }
  
  public void read(VsonObject object) {
    object.forEach(e -> {
          String key = e.getName();
          VsonValue value = e.getValue();
          if (value.isObject()) {
            ConfigSection section = createChild0(key);
            section.read(value.asVsonObject());
          } else {
            this.elements.put(key, ConfigIo.asObj(value));
          } 
        });
  }
  
  public ConfigSection(String name, ConfigSection parent, ConfigSection root) {
    this.name = name;
    this.parent = parent;
    this.root = root;
  }
  
  public String getName() {
    return this.name;
  }
  
  public ConfigSection getRoot() {
    return this.root;
  }
  
  public ConfigSection getParent() {
    return this.parent;
  }
  
  public ConfigSection createChild(String key) {
    String[] split = key.split(SECTION_SEPARATOR);
    ConfigSection section = this;
    if (split.length > 0)
      for (String aSplit : split) {
        ConfigSection child = section.getChild(aSplit);
        if (child != null) {
          section = child;
        } else {
          section = section.createChild0(aSplit);
        } 
      }  
    return section;
  }
  
  @Nonnull
  public ConfigSection getChild(String key) {
    return findSection(key.split(SECTION_SEPARATOR), false);
  }
  
  public boolean removeChild(String key) {
    String[] split = key.split(SECTION_SEPARATOR);
    String finalKey = (split.length > 0) ? split[split.length - 1] : key;
    ConfigSection parent = findSection(split, true);
    return (parent.elements.remove(finalKey) != null);
  }
  
  public Stream<ConfigSection> getChildren(boolean deep) {
    Set<ConfigSection> set = new LinkedHashSet<>();
    children0(set, deep);
    return Collections.<ConfigSection>unmodifiableSet(set).stream();
  }
  
  public Stream<String> getKeys(boolean deep) {
    Set<String> set = new LinkedHashSet<>();
    iterate("", set, (s, e) -> handlePath(s, (String)e.getKey()), deep);
    return Collections.<String>unmodifiableSet(set).stream();
  }
  
  public Stream<Object> getValues(boolean deep) {
    LinkedList<Object> list = new LinkedList();
    iterate("", list, (s, e) -> e.getValue(), deep);
    return Collections.<Object>unmodifiableCollection(list).stream();
  }
  
  public Stream<Map.Entry<String, Object>> getEntries(boolean deep) {
    Set<Map.Entry<String, Object>> set = new LinkedHashSet<>();
    iterate("", set, this::concatKey, deep);
    return Collections.<Map.Entry<String, Object>>unmodifiableSet(set).stream();
  }
  
  public Object get(String key) {
    return getElement(key);
  }
  
  public <T> T get(String key, Class<T> type) {
    Object element = getElement(key);
    if (!(element instanceof ConfigSection))
      return (T)element; 
    throw new IllegalArgumentException(key + " is a config section");
  }
  
  public void set(String key, Object value) {
    String[] split = key.split(SECTION_SEPARATOR);
    String finalKey = key;
    ConfigSection section = this;
    if (split.length > 0) {
      finalKey = split[split.length - 1];
      for (int i = 0; i < split.length - 1; i++)
        section = section.createChild(split[i]); 
    } 
    if (value instanceof Collection) {
      VsonArray v = new VsonArray();
      for (Object o : value)
        v.append(ConfigIo.asJson(o)); 
      value = v;
    } 
    section.elements.put(finalKey, value);
  }
  
  public boolean remove(String key) {
    String[] split = key.split(SECTION_SEPARATOR);
    String finalKey = (split.length == 0) ? key : split[split.length - 1];
    ConfigSection section = findSection(split, true);
    return (section.elements.remove(finalKey) != null);
  }
  
  public boolean hasKey(String key) {
    return this.elements.containsKey(key);
  }
  
  public int getInt(String key) {
    return ((Number)get(key, Number.class)).intValue();
  }
  
  public void setInt(String key, int value) {
    set(key, Integer.valueOf(value));
  }
  
  public short getShort(String key) {
    return ((Number)get(key, Number.class)).shortValue();
  }
  
  public void setShort(String key, short value) {
    set(key, Short.valueOf(value));
  }
  
  public long getLong(String key) {
    return ((Number)get(key, Number.class)).longValue();
  }
  
  public void setLong(String key, long value) {
    set(key, Long.valueOf(value));
  }
  
  public byte getByte(String key) {
    return ((Number)get(key, Number.class)).byteValue();
  }
  
  public void setByte(String key, byte value) {
    set(key, Byte.valueOf(value));
  }
  
  public float getFloat(String key) {
    return ((Number)get(key, Number.class)).floatValue();
  }
  
  public void setFloat(String key, float value) {
    set(key, Float.valueOf(value));
  }
  
  public double getDouble(String key) {
    return ((Number)get(key, Number.class)).doubleValue();
  }
  
  public void setDouble(String key, double value) {
    set(key, Double.valueOf(value));
  }
  
  public char getChar(String key) {
    return ((Character)getElement(key)).charValue();
  }
  
  public void setChar(String key, char value) {
    set(key, Character.valueOf(value));
  }
  
  public boolean getBoolean(String key) {
    return ((Boolean)getElement(key)).booleanValue();
  }
  
  public void setBoolean(String key, boolean value) {
    set(key, Boolean.valueOf(value));
  }
  
  @Nonnull
  public String getString(String key) {
    return (String)getElement(key);
  }
  
  public void setString(String key, String value) {
    set(key, value);
  }
  
  public <T, C extends Collection<T>> void getCollection(String key, C collection) {
    Object o = getElement(key);
    if (o instanceof VsonArray) {
      VsonArray array = (VsonArray)o;
      for (VsonValue value : array)
        collection.add(ConfigIo.asObj(value)); 
      return;
    } 
    throw new NoSuchElementException(String.format("%s is not a collection (%s)", new Object[] { key, o.getClass() }));
  }
  
  private void children0(Collection<ConfigSection> col, boolean deep) {
    this.elements.values().stream()
      .filter(o -> o instanceof ConfigSection)
      .map(o -> (ConfigSection)o)
      .forEach(cs -> {
          if (deep)
            cs.children0(col, true); 
          col.add(cs);
        });
  }
  
  private <T> void iterate(String base, Collection<T> col, BiFunction<String, Map.Entry<String, Object>, T> function, boolean deep) {
    this.elements.entrySet().forEach(e -> {
          Object val = e.getValue();
          if (deep && val instanceof ConfigSection) {
            ConfigSection section = (ConfigSection)val;
            section.iterate(handlePath(base, section.name), col, function, true);
            return;
          } 
          col.add(function.apply(base, e));
        });
  }
  
  private ConfigSection createChild0(String name) {
    ConfigSection section = new ConfigSection(name, this, getRoot());
    this.elements.put(name, section);
    return section;
  }
  
  private String handlePath(String path, String cur) {
    if (path.isEmpty())
      return cur; 
    return path + "." + cur;
  }
  
  @Nonnull
  private ConfigSection findSection(String[] split, boolean hasValue) {
    ConfigSection section = this;
    if (split.length > 1) {
      for (int i = 0; i < (hasValue ? (split.length - 1) : split.length); i++) {
        String sectionName = split[i];
        Object o = section.elements.get(sectionName);
        if (!(o instanceof ConfigSection))
          throw new NoSuchElementException(String.format("Section \"%s\" cannot be found in \"%s\"", new Object[] { sectionName, Arrays.toString(split) })); 
        section = (ConfigSection)o;
      } 
    } else if (!hasValue) {
      return (ConfigSection)this.elements.get(split[0]);
    } 
    return section;
  }
  
  @Nonnull
  private Object getElement(String key) {
    String[] split = key.split(SECTION_SEPARATOR);
    String finalKey = key;
    ConfigSection section = findSection(split, true);
    if (section != this)
      finalKey = split[split.length - 1]; 
    Object element = section.elements.get(finalKey);
    if (element == null)
      throw new NoSuchElementException(String.format("Key \"%s\" in your key \"%s\" cannot be found", new Object[] { finalKey, key })); 
    return element;
  }
  
  private Map.Entry<String, Object> concatKey(final String s, final Map.Entry<String, Object> entry) {
    return new Map.Entry<String, Object>() {
        public String getKey() {
          return ConfigSection.this.handlePath(s, (String)entry.getKey());
        }
        
        public Object getValue() {
          return entry.getValue();
        }
        
        public Object setValue(Object value) {
          return entry.setValue(value);
        }
        
        public boolean equals(Object o) {
          return entry.equals(o);
        }
        
        public int hashCode() {
          return entry.hashCode();
        }
      };
  }
}
