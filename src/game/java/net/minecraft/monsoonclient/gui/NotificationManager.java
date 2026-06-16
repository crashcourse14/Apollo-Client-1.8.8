package net.minecraft.monsoonclient.gui;

import java.util.ArrayDeque;
import java.util.Queue;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Renders slide-in notifications in the top-right corner of the screen.
 * Call NotificationManager.push(title, body) from anywhere to send one.
 *
 * Layout (all values in GUI pixels):
 *   WIDTH       = 160
 *   PADDING     = 6
 *   BORDER_W    = 3   (right edge, accent color)
 *   SLIDE_TICKS = 8   (ticks to fully slide in / out)
 *   HOLD_TICKS  = 60  (ticks the notification stays fully visible)
 */
public class NotificationManager extends Gui {

    private static final int WIDTH       = 160;
    private static final int PADDING     = 6;
    private static final int BORDER_W    = 3;
    private static final int SLIDE_TICKS = 8;
    private static final int HOLD_TICKS  = 60;

    private static final int COLOR_BG     = 0x66111214;
    private static final int COLOR_BORDER = 0x33EE4639;
    private static final int COLOR_TITLE  = 0xFFFFFFFF;
    private static final int COLOR_BODY   = 0xFFAAAAAA;

    private static final Queue<Notification> queue = new ArrayDeque<>();
    private static Notification current = null;
    private static int ticksAlive = 0;   // ticks since current notification appeared

    private final Minecraft mc;

    public NotificationManager(Minecraft mc) {
        this.mc = mc;
    }


    /** Queue a notification. Safe to call from any thread/tick. */
    public static void push(String title, String body) {
        queue.add(new Notification(title, body));
    }


    public void tick() {
        if (current == null) {
            current = queue.poll();
            ticksAlive = 0;
        } else {
            ticksAlive++;
            int total = SLIDE_TICKS + HOLD_TICKS + SLIDE_TICKS;
            if (ticksAlive >= total) {
                current = null;
            }
        }
    }


    public void render(float partialTicks) {
        if (current == null) return;

        ScaledResolution sr = mc.scaledResolution;
        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        // Measure text to determine height
        int bodyLines = mc.fontRendererObj.listFormattedStringToWidth(current.body, WIDTH - PADDING * 2 - BORDER_W).size();
        int height = PADDING + mc.fontRendererObj.FONT_HEIGHT          
                   + 3                                                  
                   + bodyLines * mc.fontRendererObj.FONT_HEIGHT          
                   + PADDING;

        // Slide offset: 0 = fully visible, WIDTH = fully off-screen right
        float animTick = ticksAlive + partialTicks;
        float slideOffset;
        if (animTick < SLIDE_TICKS) {
            float t = animTick / SLIDE_TICKS;
            slideOffset = (1f - easeOut(t)) * (WIDTH + 4);
        } else if (animTick < SLIDE_TICKS + HOLD_TICKS) {
            slideOffset = 0f;
        } else {
            float t = (animTick - SLIDE_TICKS - HOLD_TICKS) / SLIDE_TICKS;
            slideOffset = easeIn(t) * (WIDTH + 4);
        }

        int margin = 4;
        int x = (int) (screenW - WIDTH - margin + slideOffset);
        int y = margin;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);


        drawRect(x, y, x + WIDTH - BORDER_W, y + height, COLOR_BG);
        drawRect(x + WIDTH - BORDER_W, y, x + WIDTH, y + height, COLOR_BORDER);

        mc.fontRendererObj.drawStringWithShadow(current.title,
                x + PADDING, y + PADDING, COLOR_TITLE);

        // Body (word-wrapped)
        int bodyY = y + PADDING + mc.fontRendererObj.FONT_HEIGHT + 3;
        for (String line : mc.fontRendererObj.listFormattedStringToWidth(
                current.body, WIDTH - PADDING * 2 - BORDER_W)) {
            mc.fontRendererObj.drawStringWithShadow(line, x + PADDING, bodyY, COLOR_BODY);
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