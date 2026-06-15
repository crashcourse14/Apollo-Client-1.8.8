package net.minecraft.client.gui;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.minecraft.client.Minecraft;

/**+
 * A flat toggle button for the mod list. Background color reflects an
 * on/off state - cyan "Add" when enabled, grey "Remove" when disabled.
 * Intentionally flat (no border, no rounding) for performance.
 */
public class GuiModButton extends GuiButton {

    public int enabledColor = 0xFF1B1C20; 
    public int enabledTextColor = 0xFFFFFFFF;

    public int disabledColor = 0xFFEE4639;
    public int disabledTextColor = 0xFFFFFFFF;

    private boolean toggled;
    private String enabledText;
    private String disabledText;

    public GuiModButton(int buttonId, int x, int y, int widthIn, int heightIn, boolean toggled) {
        this(buttonId, x, y, widthIn, heightIn, "Remove", "Add", toggled);
    }

    public GuiModButton(int buttonId, int x, int y, int widthIn, int heightIn, String enabledText,
            String disabledText, boolean toggled) {
        super(buttonId, x, y, widthIn, heightIn, toggled ? enabledText : disabledText);
        this.enabledText = enabledText;
        this.disabledText = disabledText;
        this.toggled = toggled;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        this.displayString = toggled ? enabledText : disabledText;
    }

    public void toggle() {
        setToggled(!toggled);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }

        FontRenderer fontrenderer = mc.fontRendererObj;
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;
        if (this.enabled && this.hovered) {
            Mouse.showCursor(EnumCursorType.HAND);
        }

        int bg = toggled ? enabledColor : disabledColor;
        int textColor = toggled ? enabledTextColor : disabledTextColor;

        if (this.enabled && this.hovered) {
            bg = shade(bg, 1.15f);
        }

        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, bg);
        this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2,
                this.yPosition + (this.height - 8) / 2, textColor);
    }

    private int shade(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = clamp((int) (((color >> 16) & 0xFF) * factor));
        int g = clamp((int) (((color >> 8) & 0xFF) * factor));
        int b = clamp((int) ((color & 0xFF) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

}