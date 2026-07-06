package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;

public class CustomTextMod extends HudMod {

    // what the player sets
    public String customText = "Custom Text";

    public CustomTextMod() {
        super("Custom Text", 5, 110, Category.HUD);
        this.textFormat = "%VALUE%";

        this.renderBackground = true;
        this.backgroundOpacity = 0.3f;
        this.icon = new ResourceLocation("minecraft:apolloclient/textures/frames.png");
    }

    @Override
    public void draw() {
        drawModText(formatText(customText), getX(), getY());
        super.draw();
    }

    @Override
    public int getWidth() {
        return getTextWidth(formatText(customText));
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }
}