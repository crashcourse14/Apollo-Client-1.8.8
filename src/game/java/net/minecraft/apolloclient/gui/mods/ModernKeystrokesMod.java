package net.minecraft.apolloclient.gui.mods;

import net.minecraft.client.gui.Gui;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.gui.hud.Category;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class ModernKeystrokesMod extends HudMod {

    private static final int KEY_HEIGHT = 24;
    private static final int UNIT = 24;
    private static final int GAP = 4;

    private static final int KEY_BG = 0xFF000000;
    private static final int KEY_BORDER = 0xFF555555;

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
        super.draw();

        int x = getX();
        int y = getY();

        int row2 = y + KEY_HEIGHT + GAP;

        int px = x;
        drawKey(px, row2, 38, false, "Tab");
        px += 38 + GAP;

        drawKey(px, row2, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_Q), "Q"); px += UNIT + GAP;
        drawKey(px, row2, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_W), "W"); px += UNIT + GAP;
        drawKey(px, row2, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_E), "E"); px += UNIT + GAP;
        drawKey(px, row2, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_R), "R"); px += UNIT + GAP;
        drawKey(px, row2, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_T), "T");

        int row3 = row2 + KEY_HEIGHT + GAP;

        px = x;
        drawKey(px, row3, 44, false, "Caps");
        px += 44 + GAP;

        drawKey(px, row3, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_A), "A"); px += UNIT + GAP;
        drawKey(px, row3, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_S), "S"); px += UNIT + GAP;
        drawKey(px, row3, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_D), "D"); px += UNIT + GAP;
        drawKey(px, row3, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_F), "F");

        int row4 = row3 + KEY_HEIGHT + GAP;

        px = x;
        drawKey(px, row4, 56, false, "Shift");
        px += 56 + GAP;

        drawKey(px, row4, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_Z), "Z"); px += UNIT + GAP;
        drawKey(px, row4, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_X), "X"); px += UNIT + GAP;
        drawKey(px, row4, UNIT, Keyboard.isKeyDown(KeyboardConstants.KEY_C), "C");

        int row5 = row4 + KEY_HEIGHT + GAP;

        px = x;
        drawKey(px, row5, 32, false, "Ctrl");
        px += 32 + GAP;

        drawKey(px, row5, 32, false, "Alt");
        px += 32 + GAP;

        drawKey(
            px,
            row5,
            110,
            Keyboard.isKeyDown(KeyboardConstants.KEY_SPACE),
            "Space"
        );

    }

    private void drawKey(int x, int y, int width, boolean pressed, String label) {
        int fillColor = pressed ? applyAlpha(clickColor, hoverOpacity) : backgroundColor;
        int border = pressed ? 0xFFFFFFFF : KEY_BORDER;

        Gui.drawRect(x - 1, y - 1, x + width + 1, y + KEY_HEIGHT + 1, border);
        Gui.drawRect(x, y, x + width, y + KEY_HEIGHT, fillColor);

        fr.drawStringWithShadow(
                label,
                x + (width - fr.getStringWidth(label)) / 2,
                y + (KEY_HEIGHT - fr.FONT_HEIGHT) / 2,
                textColor
        );
    }

    private int applyAlpha(int color, float alpha) {
        int a = (int) (Math.max(0.0f, Math.min(1.0f, alpha)) * 255.0f);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    @Override
    public int getWidth() {
        return 300;
    }

    @Override
    public int getHeight() {
        return KEY_HEIGHT * 5 + GAP * 4;
    }
}