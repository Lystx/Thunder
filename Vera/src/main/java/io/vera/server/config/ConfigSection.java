
package io.vera.server.config;

import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;


import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@ThreadSafe
public class ConfigSection {
    private static final String SEPARATOR_LITERAL = ".";
    private static final String SECTION_SEPARATOR = Pattern.quote(SEPARATOR_LITERAL);
    private final ConcurrentLinkedStringMap<Object> elements = new ConcurrentLinkedStringMap<>();

    public VsonObject write() {
        VsonObject object = new VsonObject();

        this.elements.forEach((k, v) -> {
            if (v instanceof ConfigSection) {
                ConfigSection section = (ConfigSection) v;
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
                ConfigSection section = this.createChild0(key);
                section.read(value.asVsonObject());
            } else {
                this.elements.put(key, ConfigIo.asObj(value));
            }
        });
    }

    private final String name;
    private final ConfigSection parent;
    private final ConfigSection root;

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
        if (split.length > 0) {
            for (String aSplit : split) {
                ConfigSection child = section.getChild(aSplit);
                if (child != null) {
                    section = child;
                    continue;
                }
                section = section.createChild0(aSplit);
            }
        }

        return section;
    }

    @Nonnull
    public ConfigSection getChild(String key) {
        return this.findSection(key.split(SECTION_SEPARATOR), false);
    }

    public boolean removeChild(String key) {
        String[] split = key.split(SECTION_SEPARATOR);
        String finalKey = split.length > 0 ? split[split.length - 1] : key;

        ConfigSection parent = this.findSection(split, true);
        return parent.elements.remove(finalKey) != null;
    }

    public Stream<ConfigSection> getChildren(boolean deep) {
        Set<ConfigSection> set = new LinkedHashSet<>();
        this.children0(set, deep);
        return Collections.unmodifiableSet(set).stream();
    }

    public Stream<String> getKeys(boolean deep) {
        Set<String> set = new LinkedHashSet<>();
        this.iterate("", set, (s, e) -> this.handlePath(s, e.getKey()), deep);
        return Collections.unmodifiableSet(set).stream();
    }

    public Stream<Object> getValues(boolean deep) {
        LinkedList<Object> list = new LinkedList<>();
        this.iterate("", list, (s, e) -> e.getValue(), deep);
        return Collections.unmodifiableCollection(list).stream();
    }

   
    public Stream<Map.Entry<String, Object>> getEntries(boolean deep) {
        Set<Map.Entry<String, Object>> set = new LinkedHashSet<>();
        this.iterate("", set, this::concatKey, deep);
        return Collections.unmodifiableSet(set).stream();
    }

   
    public Object get(String key) {
        return this.getElement(key);
    }

   
    public <T> T get(String key, Class<T> type) {
        Object element = this.getElement(key);
        if (!(element instanceof ConfigSection)) {
            return (T) element;
        }

        throw new IllegalArgumentException(key + " is a config section");
    }

   
    public void set(String key, Object value) {
        String[] split = key.split(SECTION_SEPARATOR);
        String finalKey = key;

        ConfigSection section = this;
        if (split.length > 0) {
            finalKey = split[split.length - 1];
            for (int i = 0; i < split.length - 1; i++) {
                section = section.createChild(split[i]);
            }
        }

        if (value instanceof Collection) {
            VsonArray v = new VsonArray();
            for (Object o : (Collection) value) {
                v.append(ConfigIo.asJson(o));
            }
            value = v;
        }

        section.elements.put(finalKey, value);
    }

   
    public boolean remove(String key) {
        String[] split = key.split(SECTION_SEPARATOR);
        String finalKey = split.length == 0 ? key : split[split.length - 1];
        ConfigSection section = this.findSection(split, true);
        return section.elements.remove(finalKey) != null;
    }

   
    public boolean hasKey(String key) {
        return this.elements.containsKey(key);
    }

   
    public int getInt(String key) {
        return this.get(key, Number.class).intValue();
    }

   
    public void setInt(String key, int value) {
        this.set(key, value);
    }

   
    public short getShort(String key) {
        return this.get(key, Number.class).shortValue();
    }

   
    public void setShort(String key, short value) {
        this.set(key, value);
    }

   
    public long getLong(String key) {
        return this.get(key, Number.class).longValue();
    }

   
    public void setLong(String key, long value) {
        this.set(key, value);
    }

   
    public byte getByte(String key) {
        return this.get(key, Number.class).byteValue();
    }

   
    public void setByte(String key, byte value) {
        this.set(key, value);
    }

   
    public float getFloat(String key) {
        return this.get(key, Number.class).floatValue();
    }

   
    public void setFloat(String key, float value) {
        this.set(key, value);
    }

   
    public double getDouble(String key) {
        return this.get(key, Number.class).doubleValue();
    }

   
    public void setDouble(String key, double value) {
        this.set(key, value);
    }

   
    public char getChar(String key) {
        return (char) this.getElement(key);
    }

   
    public void setChar(String key, char value) {
        this.set(key, value);
    }

   
    public boolean getBoolean(String key) {
        return (boolean) this.getElement(key);
    }

   
    public void setBoolean(String key, boolean value) {
        this.set(key, value);
    }

    @Nonnull
   
    public String getString(String key) {
        return (String) this.getElement(key);
    }

   
    public void setString(String key, String value) {
        this.set(key, value);
    }

   
    public <T, C extends Collection<T>> void getCollection(String key, C collection) {
        Object o = this.getElement(key);
        if (o instanceof VsonArray) {
            VsonArray array = (VsonArray) o;
            for (VsonValue value : array) {
                collection.add((T) ConfigIo.asObj(value));
            }
            return;
        }

        throw new NoSuchElementException(String.format("%s is not a collection (%s)", key, o.getClass()));
    }


    private void children0(Collection<ConfigSection> col, boolean deep) {
        this.elements.values().stream()
                .filter(o -> o instanceof ConfigSection)
                .map(o -> (ConfigSection) o)
                .forEach(cs -> {
                    if (deep) {
                        cs.children0(col, true);
                    }
                    col.add(cs);
                });
    }


    private <T> void iterate(String base, Collection<T> col, BiFunction<String, Map.Entry<String, Object>, T> function, boolean deep) {
        this.elements.entrySet().forEach(e -> {
                    Object val = e.getValue();
                    if (deep) {
                        if (val instanceof ConfigSection) {
                            ConfigSection section = (ConfigSection) val;
                            section.iterate(this.handlePath(base, section.name), col, function, true);
                            return;
                        }
                    }

                    col.add(function.apply(base, e));
                });
    }

    private ConfigSection createChild0(String name) {
        ConfigSection section = new ConfigSection(name, this, this.getRoot());
        this.elements.put(name, section);
        return section;
    }

    private String handlePath(String path, String cur) {
        if (path.isEmpty()) return cur;
        return path + SEPARATOR_LITERAL + cur;
    }

    @Nonnull
    private ConfigSection findSection(String[] split, boolean hasValue) {
        ConfigSection section = this;
        if (split.length > 1) {
            for (int i = 0; i < (hasValue ? split.length - 1 : split.length); i++) {
                String sectionName = split[i];
                Object o = section.elements.get(sectionName);
                if (!(o instanceof ConfigSection)) {
                    throw new NoSuchElementException(String.format("Section \"%s\" cannot be found in \"%s\"", sectionName, Arrays.toString(split)));
                }

                section = (ConfigSection) o;
            }
        } else if (!hasValue) {
            return (ConfigSection) this.elements.get(split[0]);
        }

        return section;
    }

    @Nonnull
    private Object getElement(String key) {
        String[] split = key.split(SECTION_SEPARATOR);
        String finalKey = key;

        ConfigSection section = this.findSection(split, true);
        if (section != this) {
            finalKey = split[split.length - 1];
        }

        Object element = section.elements.get(finalKey);
        if (element == null) {
            throw new NoSuchElementException(String.format("Key \"%s\" in your key \"%s\" cannot be found", finalKey, key));
        }

        return element;
    }

    private Map.Entry<String, Object> concatKey(String s, Map.Entry<String, Object> entry) {
        return new Map.Entry<String, Object>() {
           
            public String getKey() {
                return ConfigSection.this.handlePath(s, entry.getKey());
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
