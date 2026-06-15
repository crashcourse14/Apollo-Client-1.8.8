package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;

/**+
 * A rounded toggle button for the mod list, styled after GuiClearButton,
 * but with a background color that reflects an on/off state - green when
 * enabled, red/dark when disabled - matching the ENABLED/DISABLED pills
 * on each mod card.
 */
public class GuiModButton extends GuiClearButton {

	// --- Colors, tweak these to match your theme ---
	public int enabledColor = 0xFF3BA55C;
	public int enabledBorder = 0xFF2D7D46;
	public int enabledTextColor = 0xFFFFFFFF;

	public int disabledColor = 0xFFB23B3B;
	public int disabledBorder = 0xFF7A2828;
	public int disabledTextColor = 0xFFFFFFFF;

	private boolean toggled;
	private String enabledText;
	private String disabledText;

	public GuiModButton(int buttonId, int x, int y, String enabledText, String disabledText, boolean toggled) {
		super(buttonId, x, y, toggled ? enabledText : disabledText);
		this.enabledText = enabledText;
		this.disabledText = disabledText;
		this.toggled = toggled;
	}

	public GuiModButton(int buttonId, int x, int y, int widthIn, int heightIn, String enabledText,
			String disabledText, boolean toggled) {
		super(buttonId, x, y, widthIn, heightIn, toggled ? enabledText : disabledText);
		this.enabledText = enabledText;
		this.disabledText = disabledText;
		this.toggled = toggled;
	}

	public boolean isToggled() {
		return toggled;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		this.displayString = toggled ? enabledText : disabledText;
	}

	public void toggle() {
		setToggled(!toggled);
	}

	public void setLabels(String enabledText, String disabledText) {
		this.enabledText = enabledText;
		this.disabledText = disabledText;
		this.displayString = toggled ? enabledText : disabledText;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!this.visible) {
			return;
		}

		FontRenderer fontrenderer = mc.fontRendererObj;
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
				&& mouseY < this.yPosition + this.height;
		if (this.enabled && this.hovered) {
			Mouse.showCursor(EnumCursorType.HAND);
		}

		int bg = toggled ? enabledColor : disabledColor;
		int border = toggled ? enabledBorder : disabledBorder;
		int textColor = toggled ? enabledTextColor : disabledTextColor;

		if (this.enabled && this.hovered) {
			bg = shade(bg, 1.15f);
		}

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		this.drawRoundedButtonBackground(this.xPosition, this.yPosition, this.width, this.height, bg, border);
		this.mouseDragged(mc, mouseX, mouseY);

		if (fontScale == 1.0f) {
			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2,
					this.yPosition + (this.height - 8) / 2, textColor);
		} else {
			float xScale = fontScale;
			float yScale = 1.0f + (fontScale - 1.0f) * 0.7f;
			float strWidth = fontrenderer.getStringWidth(displayString) / xScale;
			GlStateManager.pushMatrix();
			GlStateManager.translate(this.xPosition + this.width / 2,
					this.yPosition + (this.height - 8 * yScale) / 2, 1.0f);
			GlStateManager.scale(xScale, yScale, 1.0f);
			GlStateManager.translate(-strWidth * 0.5f * xScale, 0.0f, 0.0f);
			fontrenderer.drawStringWithShadow(displayString, 0, 0, textColor);
			GlStateManager.popMatrix();
		}
	}

	/**+
	 * Multiplies each RGB channel by factor (preserving alpha), clamped to 0-255.
	 * factor > 1 brightens, factor < 1 darkens.
	 */
	private int shade(int color, float factor) {
		int a = (color >> 24) & 0xFF;
		int r = clamp((int) (((color >> 16) & 0xFF) * factor));
		int g = clamp((int) (((color >> 8) & 0xFF) * factor));
		int b = clamp((int) ((color & 0xFF) * factor));
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	private int clamp(int v) {
		return Math.max(0, Math.min(255, v));
	}

}