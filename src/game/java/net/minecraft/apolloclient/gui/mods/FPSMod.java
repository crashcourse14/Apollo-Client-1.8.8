package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;

public class FPSMod extends HudMod {

    public FPSMod() {
        super("FPS", 5, 5, Category.HUD);
        
        this.textFormat = "%VALUE% FPS";
        this.icon = new ResourceLocation("apolloclient/textures/mods/frames.png");
        this.renderBackground = false;
        this.backgroundOpacity = 0.0f;
        this.enabled = true;
        this.textScale = 1.5f;

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