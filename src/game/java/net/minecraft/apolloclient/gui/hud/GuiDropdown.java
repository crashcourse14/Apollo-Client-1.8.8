package net.minecraft.apolloclient.gui.hud;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.util.List;

public class GuiDropdown extends Gui {

    public int x, y, width, height;
    public List<String> options;
    public int selected = 0;
    public boolean open = false;

    private static final int C_BORDER = 0xFF343A43;
    private static final int C_BG = 0xAA1E2228;
    private static final int C_HOVER = 0xDD282D35;
    private static final int C_TEXT = 0xFFF2F4F8;

    public GuiDropdown(int x, int y, int width, int height, List<String> options, int currentSelected) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.options = options;
        this.selected = currentSelected;
    }

    public void draw(FontRenderer fr, int mx, int my) {
        // Draw main button
        drawRect(x - 1, y - 1, x + width + 1, y + height + 1, C_BORDER);
        drawRect(x, y, x + width, y + height, C_BG);

        String text = options.get(selected);
        fr.drawStringWithShadow(text, x + 4, y + (height - 8) / 2, C_TEXT);

        // Draw arrow indicator
        String arrow = open ? "▲" : "▼";
        fr.drawStringWithShadow(arrow, x + width - fr.getStringWidth(arrow) - 4, y + (height - 8) / 2, C_TEXT);

        if (open) {
            int dropY = y + height + 2;
            for (int i = 0; i < options.size(); i++) {
                int oY = dropY + i * (height + 2);
                boolean hover = mx >= x && mx <= x + width && my >= oY && my <= oY + height;

                drawRect(x - 1, oY - 1, x + width + 1, oY + height + 1, C_BORDER);
                drawRect(x, oY, x + width, oY + height, hover ? C_HOVER : C_BG);
                fr.drawStringWithShadow(options.get(i), x + 4, oY + (height - 8) / 2, C_TEXT);
            }
        }
    }

    /**
     * Returns true if the click was handled by this dropdown.
     */
    public boolean mouseClicked(int mx, int my, int button) {
        if (button != 0) return false;

        // Click on the main button toggles the dropdown
        if (mx >= x && mx <= x + width && my >= y && my <= y + height) {
            open = !open;
            return true;
        }

        // If open, check if clicking an option
        if (open) {
            int dropY = y + height + 2;
            for (int i = 0; i < options.size(); i++) {
                int oY = dropY + i * (height + 2);
                if (mx >= x && mx <= x + width && my >= oY && my <= oY + height) {
                    selected = i;
                    open = false; // Close after selecting
                    return true;
                }
            }
        }

        // Clicked outside
        open = false; 
        return false;
    }
}