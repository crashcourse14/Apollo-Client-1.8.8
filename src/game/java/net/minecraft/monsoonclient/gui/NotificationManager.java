package net.minecraft.monsoonclient.gui;

import java.util.ArrayDeque;
import java.util.Queue;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Renders slide-in notifications in the bottom-right corner of the screen.
 * Uses system time for perfect animations without needing a tick event!
 */
public class NotificationManager extends Gui {

    // Increased WIDTH and PADDING for a larger notification
    private static final int WIDTH       = 220;
    private static final int PADDING     = 10;
    private static final int BORDER_W    = 3;
    
    // Using real time instead of ticks makes the animation buttery smooth
    private static final long SLIDE_MS = 400L; // 0.4 seconds to slide in/out
    private static final long HOLD_MS  = 3000L; // 3.0 seconds to hold

    // Updated Colors as requested
    private static final int COLOR_BG     = 0xCC0D0E10;
    private static final int COLOR_BORDER = 0xFFDD3538;
    private static final int COLOR_TITLE  = 0xFFFFFFFF;
    private static final int COLOR_BODY   = 0xFFAAAAAA;

    private static final Queue<Notification> queue = new ArrayDeque<>();
    private static Notification current = null;
    private static long startTime = 0L;

    private final Minecraft mc;

    public NotificationManager(Minecraft mc) {
        this.mc = mc;
    }

    /** Queue a notification. Safe to call from anywhere. */
    public static void push(String title, String body) {
        queue.add(new Notification(title, body));
    }

    public void render(float partialTicks) {
        long now = System.currentTimeMillis();

        // If there is no active notification, grab one from the queue
        if (current == null && !queue.isEmpty()) {
            current = queue.poll();
            startTime = now;
        }

        if (current == null) return;

        long elapsed = now - startTime;
        long totalLife = SLIDE_MS + HOLD_MS + SLIDE_MS;

        // If the notification has finished its cycle, clear it and wait for next frame
        if (elapsed >= totalLife) {
            current = null;
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        // Measure text to determine height (increased gap between title and body from 3 to 5)
        int bodyLines = mc.fontRendererObj.listFormattedStringToWidth(current.body, WIDTH - PADDING * 2 - BORDER_W).size();
        int height = PADDING + mc.fontRendererObj.FONT_HEIGHT          
                   + 5                                                  
                   + bodyLines * mc.fontRendererObj.FONT_HEIGHT          
                   + PADDING;

        // Calculate slide offset based on real time
        float slideOffset;
        if (elapsed < SLIDE_MS) {
            float t = (float) elapsed / SLIDE_MS;
            slideOffset = (1f - easeOut(t)) * (WIDTH + 4);
        } else if (elapsed < SLIDE_MS + HOLD_MS) {
            slideOffset = 0f;
        } else {
            float t = (float) (elapsed - SLIDE_MS - HOLD_MS) / SLIDE_MS;
            slideOffset = easeIn(t) * (WIDTH + 4);
        }

        int margin = 4;
        int x = (int) (screenW - WIDTH - margin + slideOffset);
        int y = screenH - height - margin;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        // 1. Draw background (shifted right by BORDER_W to make room for left border)
        drawRect(x + BORDER_W, y, x + WIDTH, y + height, COLOR_BG);
        
        // 2. Draw border on the LEFT side
        drawRect(x, y, x + BORDER_W, y + height, COLOR_BORDER);

        // 3. Draw title (shifted right by BORDER_W so it doesn't overlap the border)
        mc.fontRendererObj.drawStringWithShadow(current.title,
                x + BORDER_W + PADDING, y + PADDING, COLOR_TITLE);

        // 4. Draw body (shifted right by BORDER_W)
        int bodyY = y + PADDING + mc.fontRendererObj.FONT_HEIGHT + 5;
        for (String line : mc.fontRendererObj.listFormattedStringToWidth(
                current.body, WIDTH - PADDING * 2 - BORDER_W)) {
            mc.fontRendererObj.drawStringWithShadow(line, x + BORDER_W + PADDING, bodyY, COLOR_BODY);
            bodyY += mc.fontRendererObj.FONT_HEIGHT;
        }

        GlStateManager.disableBlend();
    }

    private static float easeOut(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    private static float easeIn(float t) {
        return t * t;
    }

    private static class Notification {
        final String title;
        final String body;
        Notification(String title, String body) {
            this.title = title;
            this.body  = body;
        }
    }
}