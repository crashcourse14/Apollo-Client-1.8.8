package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.Client;
import net.minecraft.monsoonclient.gui.HudMod;
import net.lax1dude.eaglercraft.v1_8.socket.EaglercraftNetworkManager;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.monsoonclient.gui.hud.CategoryManager;
import net.minecraft.util.ResourceLocation;



public class PingMod extends HudMod {

    public PingMod() {
        super("Ping", 5, 50, Category.HUD);
        this.description = "Display your ping to a server";
        //this.icon = new ResourceLocation(null, null);
    }

    @Override
    public void draw() {
        fr.drawStringWithShadow("Ping: (In dev)", getX(), getY(), -1);
        super.draw();
    }

    @Override
    public int getWidth() {
        return fr.getStringWidth("Ping: (In dev)");
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }



}

