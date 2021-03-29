package io.betterbukkit.provider.addon;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.internal.utils.BetterClassLoader;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
public class AddonProvider {

    private final EasyBukkit easyBukkit;
    private final List<Addon> addons;
    private final File directory;

    public AddonProvider(EasyBukkit easyBukkit) {
        this.easyBukkit = easyBukkit;
        this.addons = new LinkedList<>();
        this.directory = new File("./addons");
        this.directory.mkdirs();
    }


    public int getSize() {
        int i = 0;
        for (File file : Objects.requireNonNull(this.directory.listFiles())) {
            if (!file.isFile()) {
                continue;
            }
            if (file.getName().endsWith(".jar")) {
                i++;
            }
        }
        return i;
    }

    public void loadAddons() {
        if (this.getSize() == 0) {
            System.out.println("[BetterBukkit/Addons] No addons to load!");
        } else {
            try {
                System.out.println("[BetterBukkit/Addons] There are " + this.getSize() + " addons to load!");
                for (File file : Objects.requireNonNull(this.directory.listFiles())) {
                    if (file.getName().endsWith(".jar")) {
                        BetterClassLoader classLoader = new BetterClassLoader(file);
                        VsonObject document = new VsonObject(classLoader.loadJson("config.json").toString(), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
                        if (document.isEmpty()) {
                            System.out.println("[BetterBukkit/Addons] §cThe file §e" + file.getName() + " §cdoesn't own a §4config.json§c!");
                            return;
                        }
                        if (document.has("main") && document.has("author") && document.has("version") && document.has("name")) {
                            Class<?> cl = classLoader.findClass(document.getString("main"));
                            if (cl == null) {
                                System.out.println("[BetterBukkit/Addons] §cThe provided MainClass of the Module §e" + file.getName() + " §ccouldn't be found!");
                                return;
                            }
                            if (cl.getSuperclass().getName().equalsIgnoreCase(Addon.class.getName())) {
                                Addon mod = (Addon) cl.newInstance();
                                AddonInfo info = new AddonInfo(document.getString("name"), document.getString("author"), document.getList("commands", String.class), document.getString("version"));
                                mod.setInfo(info);

                                File file1 = new File(directory, "config.json");
                                VsonObject config = new VsonObject(file1, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
                                if (!file1.exists()) {
                                    config.save();
                                }
                                mod.onLoadConfig();
                                this.addons.add(mod);
                                System.out.println("[BetterBukkit/Addons] The Module " + info.getName() + " by " + info.getAuthor() + " Version: " + info.getVersion() + " was loaded!");
                            } else {
                                System.out.println("[BetterBukkit/Addons] The provided MainClass of the Module " + file.getName() + " doesn't extends the Module.class!");
                            }
                        } else {
                            System.out.println("[BetterBukkit/Addons] A Module doesn't have all needed attributes in the config.json!");
                        }
                    }
                }

            } catch (InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enableAddons() {
        this.addons.forEach(Addon::onEnable);
    }

    public void disableAddons() {
        this.addons.forEach(Addon::onDisable);
    }

}
