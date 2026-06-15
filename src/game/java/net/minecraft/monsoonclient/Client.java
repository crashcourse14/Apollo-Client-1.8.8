package net.minecraft.monsoonclient;

import net.minecraft.monsoonclient.event.EventManager;
import net.minecraft.monsoonclient.event.EventTarget;
import net.minecraft.monsoonclient.event.events.ClientTick;
import net.minecraft.monsoonclient.gui.HudManager;
import net.minecraft.client.Minecraft;

public class Client {
    public String name = "MonsoonClient";

    public static Client INSTANCE = new Client();
    public Minecraft mc = Minecraft.getMinecraft();
    
    public HudManager hudManager;
    public EventManager eventManager;

    public void startUp() {

        eventManager = new EventManager();
        hudManager = new HudManager();

        EventManager.register(this);

    }

    public void shutDown() {

        eventManager.unregister(this);
    }


    @EventTarget
    public void onTick(ClientTick event) {
        if (Client.INSTANCE.hudManager.fullBrightMod.isEnabled() && mc.theWorld != null) {
            mc.gameSettings.gammaSetting = 1000;
        } else if (!Client.INSTANCE.hudManager.fullBrightMod.isEnabled()) {
            mc.gameSettings.gammaSetting = 1;
        }

    }
}
