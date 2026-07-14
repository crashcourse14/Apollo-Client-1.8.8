package net.minecraft.client.gui;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiLinkButton extends GuiClearButton {

    private final ResourceLocation linkTexture;

    public GuiLinkButton(int buttonId, int x, int y, ResourceLocation texture) {
        super(buttonId, x, y, 20, 20, "");
        this.linkTexture = texture;
        
        this.bgColor = 0xFF14161A;
        this.hoverBgColor = 0xFF292D35;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);

        if (this.visible) {

            mc.getTextureManager().bindTexture(this.linkTexture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            
            Gui.drawScaledCustomSizeModalRect(
                this.xPosition, this.yPosition, 
                0.0F, 0.0F, 
                128, 128, 
                20, 20, 
                64.0F, 64.0F
            );
        }
    }
}