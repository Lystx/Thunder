package de.lystx.test;

import io.betterbukkit.EasyBukkit;
import io.betterbukkit.provider.addon.Addon;

public class TestAddon extends Addon {

    @Override
    public void onLoadConfig() {

    }

    @Override
    public void onEnable() {
       EasyBukkit.getInstance().getEventProvider().registerListener(new TestListener());
       EasyBukkit.getInstance().getCommandProvider().registerCommand(new TestCommand());
    }
    @Override
    public void onDisable() {

    }

}
