package net.minecraft.apolloclient.config;

import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.Client;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformApplication;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import net.minecraft.apolloclient.gui.NotificationManager;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger();
    private static final String STORAGE_KEY = "apollo_config";

    public void save() {
        logger.info("Saving client mod configurations...");

        try {
            JSONObject configJson = new JSONObject();
            JSONObject modsJson   = new JSONObject();

            for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {
                JSONObject modJson = new JSONObject();

                // Save standard positioning and state
                modJson.put("enabled", mod.isEnabled());
                modJson.put("x", mod.getX());
                modJson.put("y", mod.getY());

                // Dynamically save all supported options
                for (ModOption opt : mod.supportedOptions) {
                    String key = opt.name(); // e.g., "TEXT_FORMAT"
                    
                    // Skip custom types, they are handled by the mod itself
                    if (opt.type == ModOption.OptionType.CUSTOM) continue;

                    switch (opt.type) {
                        case STRING:
                            modJson.put(key, mod.getOptionString(opt));
                            break;
                        case COLOR:
                            modJson.put(key, mod.getOptionColor(opt));
                            break;
                        case NUMBER:
                            modJson.put(key, mod.getOptionNumber(opt));
                            break;
                        case BOOLEAN:
                            modJson.put(key, mod.getOptionBoolean(opt));
                            break;
                    }
                }
                mod.saveCustomOptions(modJson);

                modsJson.put(mod.name, modJson);

            }

            configJson.put("mods", modsJson);

            String jsonString = configJson.toString();
            byte[] rawBytes   = jsonString.getBytes(StandardCharsets.UTF_8);

            PlatformApplication.setLocalStorage(STORAGE_KEY, rawBytes);

        } catch (Exception e) {
            logger.error("Failed to save unified client configuration!", e);
            NotificationManager.push("Local Storage Failed", "Failed to save local storage! Error" + e);
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
            String     jsonString = new String(data, StandardCharsets.UTF_8);
            JSONObject configJson = new JSONObject(jsonString);

            if (configJson.has("mods")) {
                JSONObject modsJson = configJson.getJSONObject("mods");

                for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {
                    if (!modsJson.has(mod.name)) continue;

                    JSONObject modJson = modsJson.getJSONObject(mod.name);

                    // Load standard positioning and state
                    mod.setEnabled(modJson.optBoolean("enabled", mod.isEnabled()));
                    mod.setX(modJson.optInt("x", mod.getX()));
                    mod.setY(modJson.optInt("y", mod.getY()));

                    // Dynamically load all supported options
                    for (ModOption opt : mod.supportedOptions) {
                        String key = opt.name();

                        if (opt.type == ModOption.OptionType.CUSTOM) continue;

                        switch (opt.type) {
                            case STRING:
                                mod.setOptionString(opt, modJson.optString(key, mod.getOptionString(opt)));
                                break;
                            case COLOR:
                                mod.setOptionColor(opt, modJson.optInt(key, mod.getOptionColor(opt)));
                                break;
                            case NUMBER:
                                mod.setOptionNumber(opt, (float) modJson.optDouble(key, mod.getOptionNumber(opt)));
                                break;
                            case BOOLEAN:
                                mod.setOptionBoolean(opt, modJson.optBoolean(key, mod.getOptionBoolean(opt)));
                                break;
                        }
                    }

                    // Let the mod load any custom data
                    mod.loadCustomOptions(modJson);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse client configuration! Data might be corrupted.", e);
        }
    }
}