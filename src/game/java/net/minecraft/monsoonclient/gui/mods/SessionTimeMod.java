package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.monsoonclient.util.SessionManager;

public class SessionTimeMod extends HudMod {

    public SessionTimeMod() {
        super("Session Time", 5, 85, Category.HUD);
        this.textFormat = "Session: %VALUE%";
    }

    @Override
    public void draw() {
        drawModText(
            formatText(SessionManager.getSessionTime()),
            getX(),
            getY()
        );

        super.draw();
    }

    @Override
    public int getWidth() {
        return getTextWidth(
            formatText(SessionManager.getSessionTime())
        );
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }
}