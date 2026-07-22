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

        this.fontRendererObj.drawStringWithShadow("§lUpdate 7.22.26", 5, 5, 0xFFFFFF);
        this.fontRendererObj.drawString("- Fixed Click GUI modules rendering outside the menu", 8, 15, 0xCFCFCF);
        this.fontRendererObj.drawString("- Added this screen", 8, 25, 0xCFCFCF);
        this.fontRendererObj.drawString("- Click GUI now shows more modules", 8, 35, 0xCFCFCF);
        this.fontRendererObj.drawString("- Changed the shirt icon to newspaper (to access this screen)", 8, 45, 0xCFCFCF);
        this.fontRendererObj.drawString("- Fixed F3 screen (left side)", 8, 55, 0xCFCFCF);
        this.fontRendererObj.drawString("§lClient Links", 5, 70, 0xCFCFCF);
        this.fontRendererObj.drawString("- https://github.com/crashcourse14/Apollo-Client-1.8.8", 8, 85, 0xCFCFCF);
        this.fontRendererObj.drawString("- https://apolloclientmc.vercel.app/game.html", 8, 95, 0xCFCFCF);
        this.fontRendererObj.drawString("§lLICENSE", 5, 110, 0xCFCFCF);
        this.fontRendererObj.drawString("GPL 3.0", 8, 125, 0xCFCFCF);






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