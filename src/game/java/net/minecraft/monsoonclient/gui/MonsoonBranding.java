package net.minecraft.monsoonclient.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class MonsoonBranding {

    private static final ResourceLocation MONSOON_LOGO = new ResourceLocation("monsoon/title/logo.png");

    public static void render(int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;

        String title = "Monsoon Client";

        int logoWidth = 32;
        int logoHeight = 32;
        int spacing = 5;
        float scale = 2.0F;

        int textWidth = (int)(fr.getStringWidth(title) * scale);

        int totalWidth = logoWidth + spacing + textWidth;

        int x = screenWidth - totalWidth - 5;
        int y = screenHeight - logoHeight - 5;

        mc.getTextureManager().bindTexture(MONSOON_LOGO);

        Gui.drawScaledCustomSizeModalRect(
                x, y,
                0, 0,
                256, 256,
                logoWidth, logoHeight,
                256, 256
        );

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);

        int textX = (int)((x + logoWidth + spacing) / scale);
        int textY = (int)((y + (logoHeight - 8 * scale) / 2) / scale);

        fr.drawString(title, textX, textY, -1);

        GlStateManager.popMatrix();
    }
}