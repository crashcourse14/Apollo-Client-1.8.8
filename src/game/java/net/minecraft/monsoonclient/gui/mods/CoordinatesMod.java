package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;


public class CoordinatesMod extends HudMod {

    public CoordinatesMod() {
        super("Coordinates", 5, 30, Category.HUD);
        // Placeholders: %X% %Y% %Z% - rearrange/relabel however you like,
        // e.g. "%X%, %Y%, %Z%" or "Pos: %X% / %Y% / %Z%"
        this.textFormat = "X: %X% Y: %Y% Z: %Z%";
        this.description = "Display coordinates and world information";
        //this.icon = new ResourceLocation(null, null); 

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