package net.minecraft.monsoonclient.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.monsoonclient.gui.hud.CategoryManager;
import net.minecraft.monsoonclient.gui.ModOption;
import net.minecraft.util.ResourceLocation;
import net.minecraft.monsoonclient.Client;





public class HudMod {

    public Minecraft mc = Minecraft.getMinecraft();


    public FontRenderer fr = mc.fontRendererObj;
    public String name;
    public int x, y;
    public boolean enabled;
    public Category category;

    // --- Display customization (text-based HUD elements) ---
    public String textFormat = "%VALUE%";
    public int textColor = 0xFFFFFFFF;
    public boolean textShadow = true;
    public float textScale = 1.0f;

    // --- Mod list presentation ---
    // Icon shown on the mod's card. Leave null to use the placeholder.
    public ResourceLocation icon = null;

    /**
     * Declares which option controls GuiModList should show for this mod.
     * Override in subclasses to restrict or extend the available controls.
     * Default: all standard text customization options.
     */
    public ModOption[] supportedOptions = {
        ModOption.TEXT_FORMAT,
        ModOption.TEXT_COLOR,
        ModOption.TEXT_SCALE,
        ModOption.TEXT_SHADOW
    };

    public HudMod(String name, int x, int y, Category category) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.enabled = false;
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

        if (Client.INSTANCE.configManager != null) {
            Client.INSTANCE.configManager.save();
        }
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected String formatText(String value) {
        return textFormat.replace("%VALUE%", value);
    }

    protected void drawModText(String text, int x, int y) {
        if (textScale == 1.0f) {
            if (textShadow) {
                fr.drawStringWithShadow(text, x, y, textColor);
            } else {
                fr.drawString(text, x, y, textColor);
            }
        } else {
            net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.pushMatrix();
            net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate(x, y, 0.0f);
            net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.scale(textScale, textScale, 1.0f);
            if (textShadow) {
                fr.drawStringWithShadow(text, 0, 0, textColor);
            } else {
                fr.drawString(text, 0, 0, textColor);
            }
            net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.popMatrix();
        }
    }

    protected int getTextWidth(String text) {
        return (int) (fr.getStringWidth(text) * textScale);
    }

    protected int getTextHeight() {
        return (int) (fr.FONT_HEIGHT * textScale);
    }

}