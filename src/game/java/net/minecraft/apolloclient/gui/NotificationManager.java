package net.minecraft.apolloclient.gui;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.RoundedRectHelper; 


/**
 * Renders slide-in notifications in the bottom-right corner of the screen.
 * Notifications stack upwards. Uses system time for perfect animations.
 */
public class NotificationManager extends Gui {

    private static final int WIDTH       = 220;
    private static final int PADDING     = 10;
    private static final int BORDER_W    = 3;
    private static final int SPACING     = 4; // Space between stacked notifications
    private static final int MAX_STACK   = 4; 
    
    private static final long SLIDE_MS = 400L; 
    private static final long HOLD_MS  = 3000L; 

    private static final int COLOR_BG     = 0xFF0E1013;
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

        int currentY = screenH - margin;

        for (int i = activeNotifications.size() - 1; i >= 0; i--) {
            ActiveNotification notif = activeNotifications.get(i);
            long elapsed = now - notif.startTime;

            if (elapsed >= totalLife) {
                activeNotifications.remove(i);
                continue;
            }

            int bodyLines = mc.fontRendererObj.listFormattedStringToWidth(notif.body, WIDTH - PADDING * 2 - BORDER_W).size();
            int height = PADDING + mc.fontRendererObj.FONT_HEIGHT + 5 + bodyLines * mc.fontRendererObj.FONT_HEIGHT + PADDING;


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
        
            currentY -= height;
            int y = currentY;

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            // BACKGROUND
            RoundedRectHelper.drawRoundedRect(x + BORDER_W, y, x + WIDTH, y + height, 5, COLOR_BG);
            
            // TITLE
            mc.fontRendererObj.drawStringWithShadow(notif.title, x + BORDER_W + PADDING, y + PADDING, COLOR_TITLE);

            // BODY
            int bodyY = y + PADDING + mc.fontRendererObj.FONT_HEIGHT + 5;
            for (String line : mc.fontRendererObj.listFormattedStringToWidth(notif.body, WIDTH - PADDING * 2 - BORDER_W)) {
                mc.fontRendererObj.drawStringWithShadow(line, x + BORDER_W + PADDING, bodyY, COLOR_BODY);
                bodyY += mc.fontRendererObj.FONT_HEIGHT;
            }

            GlStateManager.disableBlend();

            currentY -= SPACING;
        }
    }

    private static float easeOut(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    private static float easeIn(float t) {
        return t * t;
    }

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