package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.ModOption;
import net.minecraft.monsoonclient.gui.hud.Category;

public class SelfNameTagMod extends HudMod {

    public SelfNameTagMod() {
        // Name, default X, default Y, Category
        super("Self Name Tag", 5, 110, Category.HUD);
        
        // No customization options! 
        // The GUI will automatically hide the "Options" button for this mod.
        this.supportedOptions = new ModOption[0];
    }

    @Override
    public void draw() {
        // Draw background first (though it defaults to false since we can't toggle it)
        super.draw();

        // Only draw if we are actually in a world
        if (mc.thePlayer != null) {
            // Get the player's username
            String name = mc.thePlayer.getName();
            
            // Draw the text on screen
            drawModText(name, getX(), getY());
        }
    }

    @Override
    public int getWidth() {
        if (mc.thePlayer != null) {
            return getTextWidth(mc.thePlayer.getName());
        }
        // Default width if not in a world
        return getTextWidth("Player");
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }
}