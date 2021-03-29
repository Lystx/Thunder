package io.betterbukkit.provider.addon;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Addon {

    private AddonInfo info;

    public abstract void onLoadConfig();

    public abstract void onEnable();

    public abstract void onDisable();

    @Override
    public String toString() {
        return info.getName();
    }

}
