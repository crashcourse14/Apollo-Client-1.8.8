package net.minecraft.client.gui;

public class RoundedRectHelper {

    public static void drawRoundedRect(int left, int top, int right, int bottom, int radius, int color) {
        int w = right - left;
        int h = bottom - top;
        int r = Math.max(0, Math.min(radius, Math.min(w, h) / 2));

        if (r <= 0) {
            Gui.drawRect(left, top, right, bottom, color);
            return;
        }

        Gui.drawRect(left, top + r, right, bottom - r, color);

        for (int i = 0; i < r; i++) {
            int dy = r - i; 
            int dx = (int) Math.round(Math.sqrt((double) r * r - (double) dy * dy));

            Gui.drawRect(left + r - dx, top + i, right - r + dx, top + i + 1, color);
            Gui.drawRect(left + r - dx, bottom - i - 1, right - r + dx, bottom - i, color);
        }
    }
}