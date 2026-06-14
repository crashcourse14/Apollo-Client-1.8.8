package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.Client;
import net.minecraft.monsoonclient.gui.HudMod;

public class FPSMod extends HudMod {

    public FPSMod() {
        super("FPS", 5, 50);
    }

    @Override
    public void draw() {
        fr.drawStringWithShadow("FPS: " + mc.getDebugFPS(), getX(), getY(), -1);
        super.draw();
    }

    @Override
    public int getWidth() {
        return fr.getStringWidth("FPS: " + mc.getDebugFPS());
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }

}

