package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.Client;
import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.monsoonclient.gui.hud.CategoryManager;
import net.minecraft.util.ResourceLocation;


public class TestMod extends HudMod {

    public TestMod() {
        super("Client Name", 5, 10, Category.HUD);
 
        this.icon = new ResourceLocation("minecraft:monsoonclient/textures/name.png");
    }

    @Override
    public void draw() {
        fr.drawStringWithShadow("Monsoon Client 1.8", getX(), getY(), -1);
        super.draw();
    }

    @Override
    public int getWidth() {
        return fr.getStringWidth("Monsoon Client 1.8");
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }



}
