package de.gusosasi.mysterymod.emotes;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import net.mysterymod.mod.MysteryMod;
import net.mysterymod.mod.addon.Addon;
import net.mysterymod.protocol.item.ItemState;

@Singleton
public class EmotesAddon extends Addon {

    private static EmotesAddon emotesAddon;
    private HashMap<Integer, ItemState> itemStates;

    @Inject
    public EmotesAddon() {
        emotesAddon = this;
        this.itemStates = new HashMap<>();
    }

    public void onEnable() {
        MysteryMod.getInstance().loadInitListeners("de.gusosasi.mysterymod.emotes.listener.init", () -> {

        });
    }

    public static EmotesAddon getEmotesAddon() {
        return emotesAddon;
    }

    public HashMap<Integer, ItemState> getItemStates() {
        return this.itemStates;
    }
}
