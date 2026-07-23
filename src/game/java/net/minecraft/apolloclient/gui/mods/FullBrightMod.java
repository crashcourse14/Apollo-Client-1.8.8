package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.Client;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.client.Minecraft;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.apolloclient.gui.hud.CategoryManager;
import net.minecraft.util.ResourceLocation;


public class FullBrightMod extends HudMod {

    public FullBrightMod() {
        super("FullBright", 5, 50, Category.MECHANIC);
        this.icon = new ResourceLocation("minecraft:apolloclient/textures/fullbright.png");
    }

    @Override
    public void draw() {
        fr.drawStringWithShadow("", getX(), getY(), -1);
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

