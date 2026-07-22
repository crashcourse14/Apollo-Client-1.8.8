package net.minecraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;

public class GuiNews extends GuiScreen {

    @Override
    public void initGui() {
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 30, 200, 20, "Done"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        this.fontRendererObj.drawStringWithShadow("§l[Update 7.22.26]", 5, 5, 0xFFFFFF);

        this.fontRendererObj.drawString("Fixed:", 8, 15, 0xCFCFCF);
        this.fontRendererObj.drawString("- Fixed Click GUI modules rendering outside the menu", 8, 25, 0xCFCFCF);
        this.fontRendererObj.drawString("Working on:", 8, 40, 0xCFCFCF);
        this.fontRendererObj.drawString("- Making Waypoints downloadable and uploadable", 8, 50, 0xCFCFCF);


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}