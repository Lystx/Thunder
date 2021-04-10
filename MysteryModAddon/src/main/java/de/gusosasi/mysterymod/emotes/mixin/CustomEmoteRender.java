package de.gusosasi.mysterymod.emotes.mixin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.mysterymod.api.input.Mouse;
import net.mysterymod.api.minecraft.IMinecraft;
import net.mysterymod.api.minecraft.entity.IEntityPlayer;
import net.mysterymod.mod.MysteryMod;
import net.mysterymod.mod.connection.service.UserService;
import net.mysterymod.mod.emote.emotes.Emote;
import net.mysterymod.mod.emote.emotes.EmoteRegistry;
import net.mysterymod.mod.emote.gui.EmoteRender;
import net.mysterymod.mod.emote.gui.EmoteWheel;
import net.mysterymod.mod.emote.page.EmotePage;
import net.mysterymod.mod.emote.page.EmotePageEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({EmoteRender.class})
public class CustomEmoteRender {

    @Shadow
    private IMinecraft minecraft;

    @Shadow
    private Mouse mouse;

    @Shadow
    private EmoteWheel emoteWheel;

    @Shadow
    private int wheelHeight;

    @Shadow
    private int previousPointOfView;

    @Shadow
    private boolean enabledRender;

    @Shadow
    private long lastOpened;

    @Shadow
    private boolean releasedKey;

    @Shadow
    private volatile boolean initialized;

    @Shadow
    private float centerX;

    @Shadow
    private float centerY;

    @Shadow
    private int lastWidth;

    @Shadow
    private int lastHeight;

    @Shadow
    private float lockedYaw;

    @Shadow
    private float lockedPitch;

    @Overwrite
    private void initialize(boolean alreadyInitialized) {
        UserService userService = MysteryMod.getInjector().getInstance(UserService.class);
        Field field = null;
        try {
            field = userService.getClass().getDeclaredField("emoteRegistry");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        List<EmotePage> emotePages = Collections.synchronizedList(new ArrayList<>());
        try {
            EmoteRegistry emoteRegistry = (EmoteRegistry)field.get(userService);
            int emoteIndex = emotePages.size();
            int emotePosition = 1;
            List<EmotePage> newPages = new ArrayList<>();
            for (Emote emote : emoteRegistry.getAllEmotes()) {
                if (newPages.size() == 0 || ((EmotePage)newPages.get(newPages.size() - 1)).getEntries().size() == 6) {
                    newPages.add(new EmotePage(emoteIndex++, new ArrayList()));
                    emotePosition = 1;
                }
                ((EmotePage)newPages.get(newPages.size() - 1)).add(new EmotePageEntry(emote, emotePosition));
                emotePosition++;
            }
            emotePages.addAll(newPages);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        this.emoteWheel = null;
        this.wheelHeight = (int)((getHeight() / 3.0F) * 2.15D);
        this.wheelHeight &= 0xFFFFFFFE;
        this.centerX = getWidth() / 2.0F;
        this.centerY = getHeight() / 2.0F;
        this.mouse.setPosition(this.minecraft.getDisplayWidth() / 2, this.minecraft.getDisplayHeight() / 2);
        IEntityPlayer entityPlayer = this.minecraft.getEntityPlayer();
        this.lockedYaw = entityPlayer.getYaw();
        this.lockedPitch = entityPlayer.getPitch();
        createEmoteWheel(emotePages);
    }

    @Shadow
    public void createEmoteWheel(List<EmotePage> emotePages) {}

    @Shadow
    public int getHeight() {
        return 0;
    }

    @Shadow
    private int getWidth() {
        return 0;
    }
}
