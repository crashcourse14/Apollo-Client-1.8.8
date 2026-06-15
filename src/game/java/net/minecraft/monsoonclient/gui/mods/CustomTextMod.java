package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;

public class CustomTextMod extends HudMod {

    // what the player sets
    public String customText = "Custom Text";

    public CustomTextMod() {
        super("Custom Text", 5, 90, Category.HUD);

        this.description = "Displays Custom Text";

        this.textFormat = "%VALUE%";

        this.icon = new ResourceLocation("minecraft:monsoonclient/textures/frames.png");
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