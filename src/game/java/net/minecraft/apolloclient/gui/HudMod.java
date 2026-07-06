package net.minecraft.apolloclient.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.util.ResourceLocation;
import net.minecraft.apolloclient.Client;
import org.json.JSONObject;
import net.minecraft.apolloclient.gui.NotificationManager;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public abstract class HudMod {

    public Minecraft mc = Minecraft.getMinecraft();
    public FontRenderer fr = mc.fontRendererObj;
    
    public String name;
    public int x, y;
    public boolean enabled;
    public Category category;

    public String textFormat = "%VALUE%";
    public int textColor = 0xFFFFFFFF;
    public int clickColor = 0xFFFF0000;
    public float hoverOpacity = 0.95f;
    public boolean textShadow = true;
    public float textScale = 1.0f;
    public boolean renderBackground = false; // Default to false so text mods don't get ugly boxes unless wanted
    public int backgroundColor = 0x000000;   // RGB
    public float backgroundOpacity = 0.5f;   // 0.0 to 1.0

    public ResourceLocation icon = null;

    public ModOption[] supportedOptions = {
        ModOption.TEXT_FORMAT,
        ModOption.TEXT_COLOR,
        ModOption.TEXT_SCALE,
        ModOption.TEXT_SHADOW,
        ModOption.RENDER_BACKGROUND,
        ModOption.BACKGROUND_COLOR,
        ModOption.BACKGROUND_OPACITY
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

    /**
     * Base draw method handles the background. 
     * Subclasses should call super.draw() first, then draw their text.
     */
    public void draw() {
        if (renderBackground) {
            int alpha = (int) (backgroundOpacity * 255.0f);
            // Combine alpha with RGB
            int finalColor = (alpha << 24) | (backgroundColor & 0xFFFFFF);
            
            // Add 1 pixel of padding to the background
            Gui.drawRect(getX() - 1, getY() - 1, getX() + getWidth() + 1, getY() + getHeight() + 1, finalColor);
        }
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (Client.INSTANCE.configManager != null) {
            Client.INSTANCE.configManager.save();
        }
    }

    public void toggle() { 
        this.enabled = !this.enabled; 
        NotificationManager.push("Apollo Client", this.name + " was " + (this.enabled ? "enabled" : "disabled"));
    }
    public boolean isEnabled() { return enabled; }

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

    public void saveCustomOptions(JSONObject modJson) {
        // Do nothing by default
    }

    public void loadCustomOptions(JSONObject modJson) {
        // Do nothing by default
    }

    protected int getTextWidth(String text) {
        return (int) (fr.getStringWidth(text) * textScale);
    }

    protected int getTextHeight() {
        return (int) (fr.FONT_HEIGHT * textScale);
    }


    public String getOptionString(ModOption opt) {
        if (opt == ModOption.TEXT_FORMAT) return textFormat;
        return "";
    }

    public int getOptionColor(ModOption opt) {
        if (opt == ModOption.TEXT_COLOR) return textColor;
        if (opt == ModOption.BACKGROUND_COLOR) return backgroundColor;
        if (opt == ModOption.CLICK_COLOR) return clickColor;
        return 0xFFFFFFFF;
    }

    public float getOptionNumber(ModOption opt) {
        if (opt == ModOption.TEXT_SCALE) return textScale;
        if (opt == ModOption.BACKGROUND_OPACITY) return backgroundOpacity;
        if (opt == ModOption.HOVER_OPACITY) return hoverOpacity;
        return 0;
    }

    public boolean getOptionBoolean(ModOption opt) {
        if (opt == ModOption.TEXT_SHADOW) return textShadow;
        if (opt == ModOption.RENDER_BACKGROUND) return renderBackground;
        return false;
    }

    public void setOptionString(ModOption opt, String val) {
        if (opt == ModOption.TEXT_FORMAT) textFormat = val;
    }

    public void setOptionColor(ModOption opt, int val) {
        if (opt == ModOption.TEXT_COLOR) textColor = val;
        else if (opt == ModOption.BACKGROUND_COLOR) backgroundColor = val;
        else if (opt == ModOption.CLICK_COLOR) clickColor = val;
    }

    public void setOptionNumber(ModOption opt, float val) {
        if (opt == ModOption.TEXT_SCALE) textScale = val;
        else if (opt == ModOption.BACKGROUND_OPACITY) backgroundOpacity = val;
        else if (opt == ModOption.HOVER_OPACITY) hoverOpacity = val;
    }

    public void setOptionBoolean(ModOption opt, boolean val) {
        if (opt == ModOption.TEXT_SHADOW) textShadow = val;
        else if (opt == ModOption.RENDER_BACKGROUND) renderBackground = val;
    }
}