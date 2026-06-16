package net.minecraft.monsoonclient.config;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.Client;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformApplication;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger();
    private static final String STORAGE_KEY = "monsoon_config";

    public void save() {
        logger.info("Saving client mod configurations to single key using org.json...");
        try {
            JSONObject configJson = new JSONObject();
            JSONObject modsJson = new JSONObject();

            for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {
                modsJson.put(mod.name, mod.isEnabled());
            }
            configJson.put("mods", modsJson);

            // Convert JSON directly to standard bytes without double-encoding in Base64
            String jsonString = configJson.toString();
            byte[] rawBytes = jsonString.getBytes(StandardCharsets.UTF_8);

            PlatformApplication.setLocalStorage(STORAGE_KEY, rawBytes);
        } catch (Exception e) {
            logger.error("Failed to save unified client configuration!", e);
        }
    }

    public void load() {
        logger.info("Loading client mod configurations via org.json...");
        byte[] data = PlatformApplication.getLocalStorage(STORAGE_KEY);
        
        if (data == null || data.length == 0) {
            logger.info("No existing configuration found. Using defaults.");
            return;
        }

        try {
            // Read raw bytes straight back into a standard UTF-8 string
            String jsonString = new String(data, StandardCharsets.UTF_8);
            JSONObject configJson = new JSONObject(jsonString);

            if (configJson.has("mods")) {
                JSONObject modsJson = configJson.getJSONObject("mods");

                for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {
                    if (modsJson.has(mod.name)) {
                        boolean isEnabled = modsJson.getBoolean(mod.name);
                        mod.setEnabled(isEnabled);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse client configuration! Data might be corrupted.", e);
        }
    }
}
