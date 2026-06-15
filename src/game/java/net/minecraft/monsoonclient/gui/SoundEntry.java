package net.minecraft.monsoonclient.gui;

/**
 * Represents a single named sound category that can have its volume adjusted.
 * Used by SoundMod and rendered as a labeled slider in GuiModList.
 */
public class SoundEntry {

    /** Display name shown next to the slider (e.g. "Creeper", "Skeleton") */
    public final String name;

    /**
     * Minecraft sound event prefix to match against.
     * e.g. "mob.creeper" matches mob.creeper.say, mob.creeper.death, etc.
     */
    public final String soundPrefix;

    /** Current volume multiplier: 0.0 (mute) to 1.0 (full). */
    public float volume;

    public SoundEntry(String name, String soundPrefix, float defaultVolume) {
        this.name = name;
        this.soundPrefix = soundPrefix;
        this.volume = defaultVolume;
    }
}