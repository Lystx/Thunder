package de.lystx.messenger.util;

import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Getter @Setter
public class Appender<T> implements Iterable<T> {

    private final String name;
    private final Class<T> tClass;
    protected boolean access;
    protected File directory;

    private VsonObject vsonObject;

    @SneakyThrows
    public Appender(Class<T> tClass, String name) {
        this.name = name;
        this.access = false;
        this.tClass = tClass;
        this.directory = new File("saves/");
        if (!this.directory.exists()) this.directory.mkdirs();

        this.vsonObject = new VsonObject(new File(this.directory, name + ".json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
    }

    @SneakyThrows
    public boolean contains(String key) {
        if (this.access) {
            this.vsonObject = new VsonObject(new File(this.directory, name + ".json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        }
        return this.vsonObject.has(key);
    }

    @SneakyThrows
    public void remove(String key) {
        if (this.access) {
            this.vsonObject = new VsonObject(new File(this.directory, name + ".json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        }
        this.vsonObject.remove(key);
        if (this.access) {
            this.vsonObject.save();
        }
    }

    @SneakyThrows
    public void appendSingle(String key, T log) {
        if (this.access) {
            this.vsonObject = new VsonObject(new File(this.directory, name + ".json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        }
        this.vsonObject.append(key, log);
        if (this.access) {
            this.vsonObject.save();
        }
    }

    @SneakyThrows
    public void append(String key, T log) {
        if (this.access) {
            this.vsonObject = new VsonObject(new File(this.directory, name + ".json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        }
        this.vsonObject.append(key, new VsonObject()
                .append("@logType", log.getClass().getSimpleName())
                .append("logging", log)
                .append("date", new SimpleDateFormat("dd.MM.yyy - hh:mm:ss").format(new Date().getTime())));
        if (this.access) {
            this.vsonObject.save();
        }
    }

    public List<String> keySet() {
        return this.vsonObject.keys();
    }

    public void update(String key, T update) {
        this.remove(key);
        this.append(key, update);
    }

    @SneakyThrows
    public List<T> getList() {
        if (this.access) {
            this.vsonObject = new VsonObject(new File(this.directory, name + ".json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        }
        List<T> list = new LinkedList<>();
        for (String key : this.vsonObject.keys()) {
            if (vsonObject.get(key) instanceof VsonObject && vsonObject.getVson(key).has("logging")) {
                list.add(vsonObject.getVson(key).getObject("logging", this.tClass));
            } else {
                list.add(vsonObject.getObject(key, this.tClass));
            }
        }
        return list;
    }

    public <L> L get(String key, Class<L> tClass) {
        for (String s : this.keySet()) {
            if (s.equalsIgnoreCase(key)) {
                return (L) vsonObject.getVson(s).getObject("logging", this.tClass);
            }
        }
        return null;
    }

    @SneakyThrows
    public void append(String key, VsonObject gsonObject) {
        if (this.access) {
            this.vsonObject = new VsonObject(new File(this.directory, name + ".json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        }
        this.vsonObject.append(key, gsonObject);
        if (this.access) {
            this.vsonObject.save();
        }
    }

    public void save() {
        this.vsonObject.save();
    }

    @Override
    public Iterator<T> iterator() {
        return this.getList().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (T t : this.getList()) {
            action.accept(t);
        }
    }

}
