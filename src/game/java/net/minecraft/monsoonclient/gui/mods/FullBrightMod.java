package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.Client;
import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.client.Minecraft;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.monsoonclient.gui.hud.CategoryManager;

public class FullBrightMod extends HudMod {

    public FullBrightMod() {
        super("FullBright", 5, 50, Category.RENDER);
    }

    @Override
    public void draw() {
        fr.drawStringWithShadow("FullBright", getX(), getY(), -1);
        super.draw();
    }

    @Override
    public int getWidth() {
        return fr.getStringWidth("FullBright");
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }


}

