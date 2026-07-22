package net.minecraft.apolloclient.gui.mods;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.gui.SoundEntry;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;

/**
 * SoundMod — lets the player adjust the volume of individual mob and block
 * sound categories independently of Minecraft's master sliders.
 *
 * Sound volumes are applied in SoundManagerMixin (or equivalent hook) by
 * checking each playing sound's event name against every SoundEntry.soundPrefix.
 * The first matching entry's volume is used as a multiplier on the final gain.
 *
 * Supported options: SOUND_SLIDERS only — no text formatting controls.
 */
public class SoundMod extends HudMod {


    /** All configurable sound entries, in the order they appear in the UI. */
    public final List<SoundEntry> soundEntries = new ArrayList<>();

    public SoundMod() {
        super("Audio Manager", 0, 0, Category.MISC);
        this.icon = new ResourceLocation("minecraft:apolloclient/textures/sound.png");

        // Only expose the sound-slider panel — no text format / color / etc.
        this.supportedOptions = new ModOption[] { ModOption.SOUND_SLIDERS };

        soundEntries.add(new SoundEntry("Creeper",      "mob.creeper",      1.0f));
        soundEntries.add(new SoundEntry("Skeleton",     "mob.skeleton",     1.0f));
        soundEntries.add(new SoundEntry("Zombie",       "mob.zombie",       1.0f));
        soundEntries.add(new SoundEntry("Spider",       "mob.spider",       1.0f));
        soundEntries.add(new SoundEntry("Enderman",     "mob.endermen",     1.0f));
        soundEntries.add(new SoundEntry("Blaze",        "mob.blaze",        1.0f));
        soundEntries.add(new SoundEntry("Ghast",        "mob.ghast",        1.0f));
        soundEntries.add(new SoundEntry("Witch",        "mob.witch",        1.0f));
        soundEntries.add(new SoundEntry("Slime",        "mob.slime",        1.0f));
        soundEntries.add(new SoundEntry("Cave Spider",  "mob.spider",       1.0f));
        soundEntries.add(new SoundEntry("Pig",          "mob.pig",          1.0f));
        soundEntries.add(new SoundEntry("Cow",          "mob.cow",          1.0f));
        soundEntries.add(new SoundEntry("Sheep",        "mob.sheep",        1.0f));
        soundEntries.add(new SoundEntry("Chicken",      "mob.chicken",      1.0f));
        soundEntries.add(new SoundEntry("Wolf",         "mob.wolf",         1.0f));
        soundEntries.add(new SoundEntry("Cat",          "mob.cat",          1.0f));
        soundEntries.add(new SoundEntry("Villager",     "mob.villager",     1.0f));
        soundEntries.add(new SoundEntry("Bat",          "mob.bat",          1.0f));
        soundEntries.add(new SoundEntry("Footsteps",    "step",             1.0f));
        soundEntries.add(new SoundEntry("Block Break",  "dig",              1.0f));
        soundEntries.add(new SoundEntry("Block Place",  "place",            1.0f));
        soundEntries.add(new SoundEntry("Lava",         "liquid.lava",      1.0f));
        soundEntries.add(new SoundEntry("Water",        "liquid.water",     1.0f));
        soundEntries.add(new SoundEntry("Fire",         "fire",             1.0f));
        soundEntries.add(new SoundEntry("Explosion",    "random.explode",   1.0f));
        soundEntries.add(new SoundEntry("Arrow",        "random.bow",       1.0f));
        soundEntries.add(new SoundEntry("Eat / Drink",  "random.eat",       1.0f));
        soundEntries.add(new SoundEntry("XP Pickup",    "random.orb",       1.0f));
        soundEntries.add(new SoundEntry("Chest",        "random.chestopen", 1.0f));
        soundEntries.add(new SoundEntry("Door",         "random.door",      1.0f));
        soundEntries.add(new SoundEntry("Anvil",        "random.anvil",     1.0f));
        soundEntries.add(new SoundEntry("Minecart",     "minecart",         1.0f));
        soundEntries.add(new SoundEntry("Portal",       "portal",           1.0f));
        soundEntries.add(new SoundEntry("Rain",         "ambient.weather",  1.0f));
        soundEntries.add(new SoundEntry("Cave Ambient", "ambient.cave",     1.0f));
    }

    /**
     * Returns the volume multiplier for a given sound event name.
     * Called from your sound hook/mixin before playing each sound.
     *
     * @param soundName the full sound event string, e.g. "mob.creeper.say"
     * @return a multiplier in [0, 1], or 1.0 if no entry matches
     */
    public float getVolumeFor(String soundName) {
        if (soundName == null) return 1.0f;
        String lower = soundName.toLowerCase();
        for (SoundEntry entry : soundEntries) {
            if (lower.startsWith(entry.soundPrefix.toLowerCase())) {
                return entry.volume;
            }
        }
        return 1.0f;
    }


    @Override
    public void draw() {
        // No HUD element — this mod only affects audio
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}