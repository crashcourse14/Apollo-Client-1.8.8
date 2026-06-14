package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.Client;
import net.minecraft.monsoonclient.gui.HudMod;

public class CoordinatesMod extends HudMod {

    public CoordinatesMod() {
        super("Coordinates", 5, 30);
    }

    @Override
    public void draw() {
        fr.drawStringWithShadow("X: " + (int) mc.thePlayer.posX + " Y: " + (int) mc.thePlayer.posY + " Z: " + (int) mc.thePlayer.posZ, getX(), getY(), -1);
        super.draw();
    }

    @Override
    public int getWidth() {
        return fr.getStringWidth("X: " + (int) mc.thePlayer.posX + " Y: " + (int) mc.thePlayer.posY + " Z: " + (int) mc.thePlayer.posZ);
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }

}
