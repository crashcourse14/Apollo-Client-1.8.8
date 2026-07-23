package net.minecraft.apolloclient;

import net.minecraft.apolloclient.event.EventManager;
import net.minecraft.apolloclient.event.EventTarget;
import net.minecraft.apolloclient.event.events.ClientTick;
import net.minecraft.apolloclient.gui.HudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.apolloclient.gui.NotificationManager;
import net.minecraft.apolloclient.config.ConfigManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;



public class Client {

    private static final Logger logger = LogManager.getLogger();

    public String name = "ApolloClient";

    public static Client INSTANCE = new Client();
    public Minecraft mc = Minecraft.getMinecraft();
    
    public HudManager hudManager;
    public EventManager eventManager;
    public ConfigManager configManager;

    public void startUp() {

        eventManager = new EventManager();
        hudManager = new HudManager();
        configManager = new ConfigManager();

        EventManager.register(this);

        logger.info("Apollo Client: Loading client configurations!");
        configManager.load();

    }

    public void shutDown() {

        eventManager.unregister(this);
    }


    private boolean gammaOverridden = false;
    private float oldGamma = 1.0F;

    @EventTarget
    public void onTick(ClientTick event) {
        boolean shouldOverride = mc.theWorld != null
                && Client.INSTANCE.hudManager.fullBrightMod.isEnabled();

        if (shouldOverride) {
            if (!gammaOverridden) {
                oldGamma = mc.gameSettings.gammaSetting;
                gammaOverridden = true;
            }
            mc.gameSettings.gammaSetting = 1000.0F;
        } else if (gammaOverridden) {
            mc.gameSettings.gammaSetting = oldGamma;
            gammaOverridden = false;
        }
    }
}