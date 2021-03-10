package de.lystx.api.elements;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Getter
public class Storage<K, V> {

    private final HashMap<K, List<V>> saves;
    private List<V> values;

    public Storage() {
        this.values = new LinkedList<>();
        this.saves = new HashMap<>();
    }

    public void append(K saveObject, int position, V value) {
        this.values = this.saves.containsKey(saveObject) ? this.saves.get(saveObject) : new LinkedList<>();
        this.values.add(position, value);
        this.saves.put(saveObject, values);
    }

    @SafeVarargs
    public final void append(K saveObject, V... value) {
        this.values = Arrays.asList(value);
        this.saves.put(saveObject, values);
    }

    public List<V> get(K saveObject) {
        this.values = this.saves.containsKey(saveObject) ? saves.get(saveObject) : new LinkedList<>();
        return this.saves.containsKey(saveObject) ? this.values : null;
    }

    public void remove(K saveObject) {
        this.saves.remove(saveObject);
    }

    public Object get(K saveObject, int position) {
        this.values = this.saves.containsKey(saveObject) ? this.saves.get(saveObject) : new LinkedList<>();
        return (!(position > this.get(saveObject).size())) && saves.containsKey(saveObject) ? this.get(saveObject).get(position) : null;
    }

    public String getString(K saveObject, int position) {
          return (String) this.get(saveObject, position);
    }

    public Boolean getBoolean(K saveObject, int position) {
        return (Boolean) this.get(saveObject, position);
    }

    public Integer getInteger(K saveObject, int position) {
        return (Integer) this.get(saveObject, position);
    }
    public List<?> getList(K saveObject, int position) {
        return (List<?>) this.get(saveObject, position);
    }

    public int getSize() {
        return this.values.size();
    }

}
