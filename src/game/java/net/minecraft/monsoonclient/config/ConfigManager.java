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
        logger.info("Saving client mod configurations...");

        try {
            JSONObject configJson = new JSONObject();
            JSONObject modsJson = new JSONObject();

            for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {

                JSONObject modJson = new JSONObject();

                modJson.put("enabled", mod.isEnabled());
                modJson.put("x", mod.getX());
                modJson.put("y", mod.getY());
                modJson.put("textFormat", mod.textFormat);
                modJson.put("textColor", mod.textColor);
                modJson.put("textShadow", mod.textShadow);
                modJson.put("textScale", mod.textScale);

                modsJson.put(mod.name, modJson);
            }

            configJson.put("mods", modsJson);

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

                    if (!modsJson.has(mod.name)) {
                        continue;
                    }

                    JSONObject modJson = modsJson.getJSONObject(mod.name);

                    mod.enabled = modJson.optBoolean("enabled", false);
                    mod.x = modJson.optInt("x", mod.x);
                    mod.y = modJson.optInt("y", mod.y);
                    mod.textFormat = modJson.optString("textFormat", mod.textFormat);
                    mod.textColor = modJson.optInt("textColor", mod.textColor);
                    mod.textShadow = modJson.optBoolean("textShadow", mod.textShadow);
                    mod.textScale = (float) modJson.optDouble("textScale", mod.textScale);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse client configuration! Data might be corrupted.", e);
        }
    }
}
