package net.minecraft.monsoonclient.config;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.HudManager;
import net.minecraft.monsoonclient.Client;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformApplication;



public class ConfigManager {

    public void save() {
        for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {

            PlatformApplication.setLocalStorage(
                "mod_" + mod.name,
                String.valueOf(mod.isEnabled()).getBytes()
            );
        }
    }

    public void load() {
        for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {

            byte[] data = PlatformApplication.getLocalStorage(
                "mod_" + mod.name
            );

            if (data != null) {
                mod.setEnabled(
                    Boolean.parseBoolean(new String(data))
                );
            }
        }
    }
}