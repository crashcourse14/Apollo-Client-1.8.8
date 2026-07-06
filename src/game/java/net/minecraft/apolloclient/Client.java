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


    private float oldGamma = -1;

    @EventTarget
    public void onTick(ClientTick event) {
        if (mc.theWorld == null) return;

        if (Client.INSTANCE.hudManager.fullBrightMod.isEnabled()) {
            if (oldGamma == -1) {
                oldGamma = mc.gameSettings.gammaSetting;
            }

            mc.gameSettings.gammaSetting = 1000.0F;
        } else {
            if (oldGamma != -1) {
                mc.gameSettings.gammaSetting = oldGamma;
                oldGamma = -1;
            }
        }
    }
}