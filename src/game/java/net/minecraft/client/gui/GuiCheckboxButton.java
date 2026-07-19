package net.minecraft.client.gui;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.minecraft.client.Minecraft;

/**+
 * A flat checkbox for boolean options - filled with checkedColor and an
 * "x" mark when checked, plain uncheckedColor when not. No border.
 */
public class GuiCheckboxButton extends GuiButton {

    public int checkedColor   = 0xFFDD3538;
    public int uncheckedColor = 0xFF1C1D21;
    public int checkmarkColor = 0xFFEDEDED;

    private boolean checked;

    public GuiCheckboxButton(int buttonId, int x, int y, int size, boolean checked) {
        super(buttonId, x, y, size, size, "");
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggle() {
        checked = !checked;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }

        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;
        if (this.enabled && this.hovered) {
            Mouse.showCursor(EnumCursorType.HAND);
        }

        int bg = checked ? checkedColor : uncheckedColor;
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, bg);

        if (checked) {
            FontRenderer fr = mc.fontRendererObj;
            this.drawCenteredString(fr, "x", this.xPosition + this.width / 2,
                    this.yPosition + (this.height - 8) / 2, checkmarkColor);
        }
    }
}