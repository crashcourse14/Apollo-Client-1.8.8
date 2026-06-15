package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.ResourceLocation;


public class DispatchDisplayMod extends HudMod {

    public DispatchDisplayMod() {
        super("Dispatch Display", 5, 90, Category.HUD);

        this.textFormat = "Entities: %VALUE%";

        // Place a 22x22 (or larger square) png at:
        // assets/monsoonclient/textures/mods/fps.png
        // If the file isn't found, the mod list falls back to the placeholder icon.
        this.icon = new ResourceLocation("minecraft: monsoonclient/textures/dispatch.png");
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