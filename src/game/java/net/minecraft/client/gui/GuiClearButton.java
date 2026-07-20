package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

public class GuiClearButton extends GuiButton {
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    public float fontScale = 1.0f;

    public int bgColor = 0xFF0E1013;  
    public int hoverBgColor = 0xFF181A1D; 
    public int borderColor = 0xFF000000;
    public int hoverBorderColor = 0xFF000000;
    
    public boolean drawCentered = true;
    public int textPaddingLeft = 0;

    public GuiClearButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public GuiClearButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    protected int getHoverState(boolean mouseOver) {
        byte b0 = 1;
        if (!this.enabled) {
            b0 = 0;
        } else if (mouseOver) {
            b0 = 2;
        }
        return b0;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRendererObj;
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
                    && mouseY < this.yPosition + this.height;
            if (this.enabled && this.hovered) {
                Mouse.showCursor(EnumCursorType.HAND);
            }

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            
            this.drawRoundedButtonBackground(this.xPosition, this.yPosition, this.width, this.height,
                    this.hovered ? this.hoverBgColor : this.bgColor);
            
            this.mouseDragged(mc, mouseX, mouseY);

            int j = 14737632;
            if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            if (fontScale == 1.0f) {
                if (drawCentered) {
                    this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2,
                            this.yPosition + (this.height - 8) / 2, j);
                } else {
                    fontrenderer.drawStringWithShadow(this.displayString, this.xPosition + textPaddingLeft,
                            this.yPosition + (this.height - 8) / 2, j);
                }
            } else {
                float xScale = fontScale;
                float yScale = 1.0f + (fontScale - 1.0f) * 0.7f;
                float strWidth = fontrenderer.getStringWidth(displayString) / xScale;
                GlStateManager.pushMatrix();
                if (drawCentered) {
                    GlStateManager.translate(this.xPosition + this.width / 2,
                            this.yPosition + (this.height - 8 * yScale) / 2, 1.0f);
                    GlStateManager.scale(xScale, yScale, 1.0f);
                    GlStateManager.translate(-strWidth * 0.5f * xScale, 0.0f, 0.0f);
                } else {
                    GlStateManager.translate(this.xPosition + textPaddingLeft,
                            this.yPosition + (this.height - 8 * yScale) / 2, 1.0f);
                    GlStateManager.scale(xScale, yScale, 1.0f);
                }
                fontrenderer.drawStringWithShadow(displayString, 0, 0, j);
                GlStateManager.popMatrix();
            }
        }
    }

    protected void drawRoundedButtonBackground(int x, int y, int width, int height, int backgroundColor) {
        RoundedRectHelper.drawRoundedRect(x, y, x + width, y + height, 8, backgroundColor);
    }

    

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {}

    public void mouseReleased(int mouseX, int mouseY) {}

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {}

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isSliderTouchEvents() {
        return false;
    }
}