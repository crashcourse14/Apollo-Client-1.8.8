package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class GuiClearButton extends GuiButton {
	protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	public float fontScale = 1.0f;

	public GuiClearButton(int buttonId, int x, int y, String buttonText) {
		super(buttonId, x, y, buttonText);
	}

	public GuiClearButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}

	/**+
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT
	 * hovering over this button and 2 if it IS hovering over this
	 * button.
	 */
	protected int getHoverState(boolean mouseOver) {
		byte b0 = 1;
		if (!this.enabled) {
			b0 = 0;
		} else if (mouseOver) {
			b0 = 2;
		}

		return b0;
	}

	/**+
	 * Draws this button to the screen.
	 */
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
					0x801E1E1E, 0x800A0A0A);
			this.mouseDragged(mc, mouseX, mouseY);

			int j = 14737632;
			if (!this.enabled) {
				j = 10526880;
			} else if (this.hovered) {
				j = 16777120;
			}

			if (fontScale == 1.0f) {
				this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2,
						this.yPosition + (this.height - 8) / 2, j);
			} else {
				float xScale = fontScale;
				float yScale = 1.0f + (fontScale - 1.0f) * 0.7f;
				float strWidth = fontrenderer.getStringWidth(displayString) / xScale;
				GlStateManager.pushMatrix();
				GlStateManager.translate(this.xPosition + this.width / 2,
						this.yPosition + (this.height - 8 * yScale) / 2, 1.0f);
				GlStateManager.scale(xScale, yScale, 1.0f);
				GlStateManager.translate(-strWidth * 0.5f * xScale, 0.0f, 0.0f);
				fontrenderer.drawStringWithShadow(displayString, 0, 0, j);
				GlStateManager.popMatrix();
			}
		}
	}

	private void drawRoundedButtonBackground(int x, int y, int width, int height, int backgroundColor,
			int borderColor) {
		this.drawRoundedBorder(x, y, x + width, y + height, 5, 2, borderColor);
	}


	private void drawRoundedBorder(int left, int top, int right, int bottom, int radius, int borderWidth, int color) {
		int r = Math.max(1, Math.min(radius, Math.min(right - left, bottom - top) / 2));
		int innerRadius = Math.max(0, r - borderWidth);
		for (int y = top; y < bottom; ++y) {
			int runStart = -1;
			for (int x = left; x <= right; ++x) {
				boolean inBorder = false;
				if (x < right) {
					boolean outer = this.isInsideRoundedRect(x, y, left, top, right, bottom, r);
					boolean inner = this.isInsideRoundedRect(x, y, left + borderWidth, top + borderWidth,
							right - borderWidth, bottom - borderWidth, innerRadius);
					inBorder = outer && !inner;
				}
				if (inBorder) {
					if (runStart < 0) {
						runStart = x;
					}
				} else if (runStart >= 0) {
					drawRect(runStart, y, x, y + 1, color);
					runStart = -1;
				}
			}
		}
	}

	private boolean isInsideRoundedRect(int x, int y, int left, int top, int right, int bottom, int radius) {
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

	/**+
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
	}

	/**+
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	public void mouseReleased(int mouseX, int mouseY) {
	}

	/**+
	 * Returns true if the mouse has been pressed on this control.
	 * Equivalent of MouseListener.mousePressed(MouseEvent e).
	 */
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition
				&& mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}

	/**+
	 * Whether the mouse cursor is currently over the button.
	 */
	public boolean isMouseOver() {
		return this.hovered;
	}

	public void drawButtonForegroundLayer(int mouseX, int mouseY) {
	}

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