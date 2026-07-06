package net.minecraft.apolloclient.gui.mods;

import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.gui.hud.Category;

public class ToggleSprintMod extends HudMod {

    private boolean isSprinting = true; // Default to sprinting
    private boolean rWasPressed = false;

    public ToggleSprintMod() {
        super("Toggle Sprint", 5, 90, Category.HUD);
        
        // Enable background by default so it looks nice
        this.renderBackground = true;
        this.backgroundOpacity = 0.3f;
        
        // Only allow the specific options you requested (NO TEXT_FORMAT)
        this.supportedOptions = new ModOption[] {
            ModOption.TEXT_COLOR,
            ModOption.TEXT_SCALE,
            ModOption.TEXT_SHADOW,
            ModOption.RENDER_BACKGROUND,
            ModOption.BACKGROUND_COLOR,
            ModOption.BACKGROUND_OPACITY
        };
    }

    @Override
    public void draw() {
        super.draw();

        boolean rIsPressed = Keyboard.isKeyDown(KeyboardConstants.KEY_R);
        if (rIsPressed && !rWasPressed) {
            isSprinting = !isSprinting; 
        }
        rWasPressed = rIsPressed; 

        if (mc.thePlayer != null && mc.thePlayer.moveForward > 0.0F && !mc.thePlayer.isSneaking()) {
            mc.thePlayer.setSprinting(isSprinting);
        }


        String displayText = isSprinting ? "Sprinting (Toggled)" : "Sprinting (Walking)";
        drawModText(displayText, getX(), getY());
    }

    @Override
    public int getWidth() {
        // Make the background perfectly fit the longest possible text
        String displayText = isSprinting ? "Sprinting(Toggled)" : "Sprinting(Walking)";
        return getTextWidth(displayText);
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }
}