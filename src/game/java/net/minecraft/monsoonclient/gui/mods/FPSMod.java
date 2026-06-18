package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;

public class FPSMod extends HudMod {

    public FPSMod() {
        super("FPS", 5, 70, Category.HUD);
        
        this.textFormat = "%VALUE% FPS";
        this.icon = new ResourceLocation("monsoonclient:textures/frames.png");
        this.renderBackground = true;
        this.backgroundOpacity = 0.3f;
        this.enabled = true;

    }

    @Override
    public void draw() {
    
        super.draw();
        String displayText = formatText(String.valueOf(mc.getDebugFPS()));
        drawModText(displayText, getX(), getY());
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