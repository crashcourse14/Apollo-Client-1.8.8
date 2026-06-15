package net.minecraft.monsoonclient.gui;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.monsoonclient.gui.hud.CategoryManager;

public class HudMod {

    public Minecraft mc = Minecraft.getMinecraft();

    public FontRenderer fr = mc.fontRendererObj;
    public String name;
    public int x, y;
    public boolean enabled;
    public Category category;

    //%VALUE% uses the live value of the mod, e.g. "FPS: %VALUE%" -> "FPS: 400",
    // or "%VALUE% FPS" -> "400 FPS".
    public String textFormat = "%VALUE%";
    // 0xAARRGGBB color, vanilla-style int (0xFFFFFFFF / -1 = white).
    public int textColor = 0xFFFFFFFF;
    public boolean textShadow = true;
    public float textScale = 1.0f;

    public HudMod(String name, int x, int y, Category category) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.enabled = true;
        this.category = category;
    }

    public int getWidth() {
        return 50;
    }

    public int getHeight() {
        return 50;
    }

    public void draw() {

    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**+
     * Replaces the %VALUE% placeholder in textFormat with the given value.
     */
    protected String formatText(String value) {
        return textFormat.replace("%VALUE%", value);
    }

    /**+
     * Draws text at (x, y) using this mod's textColor/textShadow/textScale.
     */
    protected void drawModText(String text, int x, int y) {
        if (textScale == 1.0f) {
            if (textShadow) {
                fr.drawStringWithShadow(text, x, y, textColor);
            } else {
                fr.drawString(text, x, y, textColor);
            }
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0.0f);
            GlStateManager.scale(textScale, textScale, 1.0f);
            if (textShadow) {
                fr.drawStringWithShadow(text, 0, 0, textColor);
            } else {
                fr.drawString(text, 0, 0, textColor);
            }
            GlStateManager.popMatrix();
        }
    }

    /**+
     * Width of the given text after textScale is applied.
     */
    protected int getTextWidth(String text) {
        return (int) (fr.getStringWidth(text) * textScale);
    }

    /**+
     * Height of a single line of text after textScale is applied.
     */
    protected int getTextHeight() {
        return (int) (fr.FONT_HEIGHT * textScale);
    }

}