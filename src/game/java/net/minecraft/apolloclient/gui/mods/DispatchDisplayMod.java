package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.ResourceLocation;


public class DispatchDisplayMod extends HudMod {

    public DispatchDisplayMod() {
        super("Dispatch Display", 5, 130, Category.HUD);

        this.textFormat = "Entities: %VALUE%";

        this.renderBackground = true;
        this.backgroundOpacity = 0.3f;
        this.icon = new ResourceLocation("minecraft: apolloclient/textures/frames.png");
    }

    @Override
    public void draw() {
        drawModText(formatText(String.valueOf(mc.renderGlobal.getDebugInfoRenders())), getX(), getY());
        super.draw();
    }

    @Override
    public int getWidth() {
        return getTextWidth(formatText(String.valueOf(mc.renderGlobal.getDebugInfoRenders())));
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }

}