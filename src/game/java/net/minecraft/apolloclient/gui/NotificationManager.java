package net.minecraft.apolloclient.gui;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Renders slide-in notifications in the bottom-right corner of the screen.
 * Notifications stack upwards. Uses system time for perfect animations.
 */
public class NotificationManager extends Gui {

    private static final int WIDTH       = 220;
    private static final int PADDING     = 10;
    private static final int BORDER_W    = 3;
    private static final int SPACING     = 4; // Space between stacked notifications
    private static final int MAX_STACK   = 4; // Maximum notifications on screen at once
    
    private static final long SLIDE_MS = 400L; 
    private static final long HOLD_MS  = 3000L; 

    private static final int COLOR_BG     = 0xCC17191D;
    private static final int COLOR_BORDER = 0xFF343A43;
    private static final int COLOR_TITLE  = 0xFFFFFFFF;
    private static final int COLOR_BODY   = 0xFFAAAAAA;

    // Replaced Queue with List to support stacking
    private static final List<ActiveNotification> activeNotifications = new ArrayList<>();

    private final Minecraft mc;

    public NotificationManager(Minecraft mc) {
        this.mc = mc;
    }

    /** Queue a notification. Safe to call from anywhere. */
    public static void push(String title, String body) {
        activeNotifications.add(new ActiveNotification(title, body, System.currentTimeMillis()));
        
        // If we exceed the max stack size, remove the oldest one
        if (activeNotifications.size() > MAX_STACK) {
            activeNotifications.remove(0);
        }
    }

    public void render(float partialTicks) {
        if (activeNotifications.isEmpty()) return;

        long now = System.currentTimeMillis();
        long totalLife = SLIDE_MS + HOLD_MS + SLIDE_MS;

        ScaledResolution sr = new ScaledResolution(mc);
        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();
        int margin = 4;

        // Start drawing from the bottom of the screen
        int currentY = screenH - margin;

        // Loop backwards (bottom to top) so the newest notification appears at the bottom
        for (int i = activeNotifications.size() - 1; i >= 0; i--) {
            ActiveNotification notif = activeNotifications.get(i);
            long elapsed = now - notif.startTime;

            // If the notification has finished its cycle, remove it and skip drawing
            if (elapsed >= totalLife) {
                activeNotifications.remove(i);
                continue;
            }

            // Measure text to determine height
            int bodyLines = mc.fontRendererObj.listFormattedStringToWidth(notif.body, WIDTH - PADDING * 2 - BORDER_W).size();
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

            int x = (int) (screenW - WIDTH - margin + slideOffset);
            
            // Move the Y position up for this notification
            currentY -= height;
            int y = currentY;

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            // 1. Draw background (shifted right by BORDER_W to make room for left border)
            drawRect(x + BORDER_W, y, x + WIDTH, y + height, COLOR_BG);
            
            // 2. Draw border on the LEFT side
            drawRect(x, y, x + BORDER_W, y + height, COLOR_BORDER);

            // 3. Draw title (shifted right by BORDER_W so it doesn't overlap the border)
            mc.fontRendererObj.drawStringWithShadow(notif.title,
                    x + BORDER_W + PADDING, y + PADDING, COLOR_TITLE);

            // 4. Draw body (shifted right by BORDER_W)
            int bodyY = y + PADDING + mc.fontRendererObj.FONT_HEIGHT + 5;
            for (String line : mc.fontRendererObj.listFormattedStringToWidth(
                    notif.body, WIDTH - PADDING * 2 - BORDER_W)) {
                mc.fontRendererObj.drawStringWithShadow(line, x + BORDER_W + PADDING, bodyY, COLOR_BODY);
                bodyY += mc.fontRendererObj.FONT_HEIGHT;
            }

            GlStateManager.disableBlend();

            // Add spacing for the next notification above it
            currentY -= SPACING;
        }
    }

    private static float easeOut(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    private static float easeIn(float t) {
        return t * t;
    }

    // Inner class now stores the time it was created
    private static class ActiveNotification {
        final String title;
        final String body;
        final long startTime;

        ActiveNotification(String title, String body, long startTime) {
            this.title = title;
            this.body  = body;
            this.startTime = startTime;
        }
    }
}