package net.minecraft.client.gui;

/**+
 * Shared rounded-rect drawing helpers, used by GuiClearButton/GuiModButton
 * and by GuiModList for its mod cards.
 */
public class GuiRenderUtils {

    private GuiRenderUtils() {
    }

    public static void drawRoundedRect(int left, int top, int right, int bottom, int radius, int color) {
        int r = Math.max(1, Math.min(radius, Math.min(right - left, bottom - top) / 2));
        for (int y = top; y < bottom; ++y) {
            int runStart = -1;
            for (int x = left; x <= right; ++x) {
                boolean inside = x < right && isInsideRoundedRect(x, y, left, top, right, bottom, r);
                if (inside) {
                    if (runStart < 0) {
                        runStart = x;
                    }
                } else if (runStart >= 0) {
                    Gui.drawRect(runStart, y, x, y + 1, color);
                    runStart = -1;
                }
            }
        }
    }

    public static void drawRoundedBorder(int left, int top, int right, int bottom, int radius, int borderWidth, int color) {
        int r = Math.max(1, Math.min(radius, Math.min(right - left, bottom - top) / 2));
        int innerRadius = Math.max(0, r - borderWidth);
        for (int y = top; y < bottom; ++y) {
            int runStart = -1;
            for (int x = left; x <= right; ++x) {
                boolean inBorder = false;
                if (x < right) {
                    boolean outer = isInsideRoundedRect(x, y, left, top, right, bottom, r);
                    boolean inner = isInsideRoundedRect(x, y, left + borderWidth, top + borderWidth,
                            right - borderWidth, bottom - borderWidth, innerRadius);
                    inBorder = outer && !inner;
                }
                if (inBorder) {
                    if (runStart < 0) {
                        runStart = x;
                    }
                } else if (runStart >= 0) {
                    Gui.drawRect(runStart, y, x, y + 1, color);
                    runStart = -1;
                }
            }
        }
    }

    private static boolean isInsideRoundedRect(int x, int y, int left, int top, int right, int bottom, int radius) {
        if (x < left || x >= right || y < top || y >= bottom) {
            return false;
        }
        if (radius <= 0) {
            return true;
        }
        int r = Math.min(radius, Math.min(right - left, bottom - top) / 2);
        int cornerRadius = Math.max(1, r);
        if (x < left + cornerRadius && y < top + cornerRadius) {
            return (x - left) * (x - left) + (y - top) * (y - top) <= cornerRadius * cornerRadius;
        }
        if (x >= right - cornerRadius && y < top + cornerRadius) {
            return (x - (right - 1)) * (x - (right - 1)) + (y - top) * (y - top) <= cornerRadius * cornerRadius;
        }
        if (x < left + cornerRadius && y >= bottom - cornerRadius) {
            return (x - left) * (x - left) + (y - (bottom - 1)) * (y - (bottom - 1)) <= cornerRadius * cornerRadius;
        }
        if (x >= right - cornerRadius && y >= bottom - cornerRadius) {
            return (x - (right - 1)) * (x - (right - 1)) + (y - (bottom - 1)) * (y - (bottom - 1))
                    <= cornerRadius * cornerRadius;
        }
        return true;
    }
}