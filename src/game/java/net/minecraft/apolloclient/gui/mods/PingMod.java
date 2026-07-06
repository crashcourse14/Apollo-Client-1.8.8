package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.Client;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.apolloclient.gui.hud.CategoryManager;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.Minecraft;



public class PingMod extends HudMod {

    public PingMod() {
        super("Ping", 5, 150, Category.HUD);
        this.renderBackground = true;
        this.backgroundOpacity = 0.3f;
        this.textFormat = "Ping: %VALUE%";
    }

    @Override
    public void draw() {
        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());

        int ping = info != null ? info.getResponseTime() : 0;

        drawModText(formatText(String.valueOf(ping)), getX(), getY());

        super.draw();
    }

    @Override
    public int getWidth() {
        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());

        int ping = info != null ? info.getResponseTime() : 0;

        return getTextWidth(formatText(String.valueOf(ping)));
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }



}

