package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.Client;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.apolloclient.gui.hud.CategoryManager;
import net.minecraft.util.ResourceLocation;



public class TestMod extends HudMod {

    public TestMod() {
        super("Client Name", 5, 10, Category.HUD);
        this.renderBackground = true;
        this.backgroundOpacity = 0.3f;
 
        this.icon = new ResourceLocation("minecraft:apolloclient/textures/name.png");
    }

    @Override
    public void draw() {
        fr.drawStringWithShadow("Apollo Client 1.8", getX(), getY(), -1);
        super.draw();
    }

    @Override
    public int getWidth() {
        return fr.getStringWidth("Apollo Client 1.8");
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }



}
