package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;


public class CoordinatesMod extends HudMod {

    public CoordinatesMod() {
        super("Coordinates", 2, 30, Category.HUD);
        this.renderBackground = false;
        this.backgroundOpacity = 0.0f;
        this.textFormat = "X: %X% Y: %Y% Z: %Z%";
        this.icon = new ResourceLocation("minecraft:apolloclient/textures/coordinates.png"); 
        this.textScale = 1.5f;

    }

    @Override
    public void draw() {
        drawModText(getCoordsText(), getX(), getY());
        super.draw();
    }

    @Override
    public int getWidth() {
        return getTextWidth(getCoordsText());
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }

    private String getCoordsText() {
        return textFormat
                .replace("%X%", String.valueOf((int) mc.thePlayer.posX))
                .replace("%Y%", String.valueOf((int) mc.thePlayer.posY))
                .replace("%Z%", String.valueOf((int) mc.thePlayer.posZ));
    }

}