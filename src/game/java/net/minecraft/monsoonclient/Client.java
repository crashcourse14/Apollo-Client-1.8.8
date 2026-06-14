package net.minecraft.monsoonclient;

import net.minecraft.monsoonclient.event.EventManager;
import net.minecraft.monsoonclient.event.events.ClientTick;
import net.minecraft.monsoonclient.gui.HudManager;

public class Client {
    public String name = "MonsoonClient";
    public static Client INSTANCE = new Client();
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

    public void onTick(ClientTick event) {

    }
}
