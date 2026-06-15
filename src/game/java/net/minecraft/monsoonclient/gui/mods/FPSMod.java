package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;



public class FPSMod extends HudMod {

    public FPSMod() {
        super("FPS", 5, 70, Category.HUD);
        // Default - change to "%VALUE% FPS" for "400 FPS" instead of "FPS: 400"
        this.textFormat = "FPS: %VALUE%";
        this.icon = new ResourceLocation("minecraft: monsoonclient/textures/frames.png");
    }

    @Override
    public void draw() {
        drawModText(formatText(String.valueOf(mc.getDebugFPS())), getX(), getY());
        super.draw();
    }

    @Override
    public int getWidth() {
        return getTextWidth(formatText(String.valueOf(mc.getDebugFPS())));
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }

}