package net.minecraft.monsoonclient.gui.hud;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.Client;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiClearButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiHudEditor extends GuiScreen {

    private static final ResourceLocation monsoonLogo = new ResourceLocation("monsoon/title/logo.png");
    
    private HudMod draggingMod;
    private int dragOffsetX;
    private int dragOffsetY;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        final int logoSize = 72;
        final int logoX = (this.width - logoSize) / 2;
        final int logoY = this.height / 2 - 110;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(monsoonLogo);
        Gui.drawScaledCustomSizeModalRect(logoX, logoY, 0.0F, 0.0F, 256, 256, logoSize, logoSize, 256.0F, 256.0F);

        final String title = "Monsoon Client";
        final String helper = "Drag enabled mods to your liking";
        
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);

        int titleWidth = this.fontRendererObj.getStringWidth(title);
        int titleX = (this.width - titleWidth * 2) / 4;
        int titleY = (logoY + logoSize + 6) / 2;

        this.drawString(this.fontRendererObj, title, titleX, titleY, 0xFFFFFFFF);
        GlStateManager.popMatrix();

        int helperWidth = this.fontRendererObj.getStringWidth(helper);
        int helperX = (this.width - helperWidth) / 2;
        int helperY = (logoY + logoSize + 6) + 24; // Position it below the scaled title
        this.drawString(this.fontRendererObj, helper, helperX, helperY, 0xFFAAAAAA);

        for(HudMod mod : Client.INSTANCE.hudManager.hudMods) {
            if(!mod.isEnabled()) {
                continue;
            }
            
            int x = mod.getX();
            int y = mod.getY();
            String text = mod.name;
            int w = fontRendererObj.getStringWidth(text);

            fontRendererObj.drawStringWithShadow(text, x, y, 0x00FF00);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        buttonList.clear();

        final int btnW = 200;
        final int cx = this.width / 2 - btnW / 2;
        int startY = this.height / 2 + 20;

        this.buttonList.add(
            new GuiClearButton(100, cx, startY, 200, 20, I18n.format("Monsoon Settings", new Object[0])));
    }

    

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 100) {
            this.mc.displayGuiScreen(new GuiModList(this));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        // Removed the "if (button == 100)" check from here!

        if(button == 0) { // 0 means left click
            for(HudMod mod : Client.INSTANCE.hudManager.hudMods) {
                int x = mod.getX();
                int y = mod.getY();

                int w = fontRendererObj.getStringWidth(mod.name);
                int h = 10;

                if(mouseX >= x &&
                   mouseX <= x + w &&
                   mouseY >= y &&
                   mouseY <= y + h) {

                    draggingMod = mod;
                    dragOffsetX = mouseX - x;
                    dragOffsetY = mouseY - y;
                    break;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if(draggingMod != null) {
            draggingMod.setX(mouseX - dragOffsetX);
            draggingMod.setY(mouseY - dragOffsetY);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        if(draggingMod != null) {
            Client.INSTANCE.configManager.save();
            draggingMod = null;
        }
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}