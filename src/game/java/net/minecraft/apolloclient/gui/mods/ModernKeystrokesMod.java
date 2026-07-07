package net.minecraft.apolloclient.gui.mods;

import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.client.gui.Gui;

public class ModernKeystrokesMod extends HudMod {

    private static final int KEY_SIZE = 24; // Width and height of standard keys
    private static final int GAP = 4;       // Space between keys
    private static final int BORDER_COLOR = 0xFF2A2B2F;

    public ModernKeystrokesMod() {
        super("Modern Keystrokes", 12, 90, Category.HUD);
        this.supportedOptions = new ModOption[]{
            ModOption.RENDER_BACKGROUND,
            ModOption.BACKGROUND_COLOR,
            ModOption.BACKGROUND_OPACITY,
            ModOption.TEXT_COLOR,
            ModOption.CLICK_COLOR,
            ModOption.HOVER_OPACITY
        };
    }

    @Override
    public void draw() {
        // Draws the background if enabled in options
        super.draw();

        int x = getX();
        int y = getY();

        // Row 1: W (Centered over ASD)
        int wX = x + KEY_SIZE + GAP;
        drawKey(wX, y, KEY_SIZE, Keyboard.isKeyDown(KeyboardConstants.KEY_W), "W");

        // Row 2: A, S, D
        int row2Y = y + KEY_SIZE + GAP;
        drawKey(x, row2Y, KEY_SIZE, Keyboard.isKeyDown(KeyboardConstants.KEY_A), "A");
        drawKey(x + KEY_SIZE + GAP, row2Y, KEY_SIZE, Keyboard.isKeyDown(KeyboardConstants.KEY_S), "S");
        drawKey(x + (KEY_SIZE + GAP) * 2, row2Y, KEY_SIZE, Keyboard.isKeyDown(KeyboardConstants.KEY_D), "D");

        // Row 3: Spacebar (Spans the exact width of the 3 keys below it)
        int row3Y = row2Y + KEY_SIZE + GAP;
        int spaceWidth = KEY_SIZE * 3 + GAP * 2;
        drawKey(x, row3Y, spaceWidth, Keyboard.isKeyDown(KeyboardConstants.KEY_SPACE), "");
    }

    /**
     * Helper method to draw a single key with its background, border, and label.
     */
    private void drawKey(int x, int y, int width, boolean pressed, String label) {
        // Determine colors based on press state
        int bgColor = pressed ? applyAlpha(clickColor, hoverOpacity) : applyAlpha(backgroundColor, backgroundOpacity);
        int borderColor = pressed ? 0xFFFFFFFF : BORDER_COLOR;

        Gui.drawRect(x - 1, y - 1, x + width + 1, y + KEY_SIZE + 1, borderColor);
        
        Gui.drawRect(x, y, x + width, y + KEY_SIZE, bgColor);

        // Draw label (if it has one)
        if (!label.isEmpty()) {
            fr.drawStringWithShadow(
                    label,
                    x + (width - fr.getStringWidth(label)) / 2,   // Center X
                    y + (KEY_SIZE - fr.FONT_HEIGHT) / 2,          // Center Y
                    textColor
            );
        }
    }

    /**
     * Helper to apply an alpha (opacity) value to an RGB color.
     */
    private int applyAlpha(int color, float alpha) {
        int a = (int) (Math.max(0.0f, Math.min(1.0f, alpha)) * 255.0f);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    @Override
    public int getWidth() {
        return KEY_SIZE * 3 + GAP * 2;
    }

    @Override
    public int getHeight() {
        return KEY_SIZE * 3 + GAP * 2;
    }
}