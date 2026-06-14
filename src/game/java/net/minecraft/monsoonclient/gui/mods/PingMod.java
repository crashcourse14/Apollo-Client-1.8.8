package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.Client;
import net.minecraft.monsoonclient.gui.HudMod;
import net.lax1dude.eaglercraft.v1_8.socket.EaglercraftNetworkManager;


public class PingMod extends HudMod {

    public PingMod() {
        super("Ping", 5, 50);
    }

    @Override
    public void draw() {
        if (mc.getNetworkManager().getPlayerInfo(mc.thePlayer.getUniqueID()) != null) {
            fr.drawStringWithShadow("Ping: " + mc.getNetworkManager().getPlayerInfo(mc.getSession().getProfile().getId()).getResponseTime(), getX(), getY(), -1);
        } else {
            fr.drawStringWithShadow("Ping: " + "N/A", getX(), getY(), -1);
        }
        super.draw();
    }

    @Override
    public int getWidth() {
        return fr.getStringWidth("Ping: " + mc.getNetworkManager().getPlayerInfo(mc.getSession().getProfile().getId()).getResponseTime());
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }

}

