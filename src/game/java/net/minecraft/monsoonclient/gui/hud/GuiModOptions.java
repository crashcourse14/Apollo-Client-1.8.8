package net.minecraft.monsoonclient.gui.hud;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiClearButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.monsoonclient.gui.HudMod;

public class GuiModOptions extends GuiScreen {

    private final GuiScreen parentScreen;
    private final HudMod mod;

    public GuiModOptions(GuiScreen parentScreen, HudMod mod) {
        this.parentScreen = parentScreen;
        this.mod = mod;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiClearButton(0, this.width / 2 - 50, this.height - 26, 100, 20, "Back"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, mod.name + " Options", this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Text format, color, shadow and scale controls coming soon",
                this.width / 2, this.height / 2, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }
}