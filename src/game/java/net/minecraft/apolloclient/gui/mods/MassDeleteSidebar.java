package net.minecraft.apolloclient.gui.mods;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiClearButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.RoundedRectHelper;

import java.util.List;

public class MassDeleteSidebar extends GuiScreen {

    private static final int MENU_BG   = 0xFF0E1013;
    private static final int TEXT_MAIN = 0xFFF2F3F5;
    private static final int TEXT_DIM  = 0xFF8B8F97;
    private static final int BORDER    = 0xFF24262B;

    private static final int SIDEBAR_W = 160;
    private static final int RADIUS    = 5;
    private static final int ROW_H     = 14;
    private static final int PADDING   = 10;

    private final GuiScreen parentScreen;
    private final MassDeleteCallback callback;

    private GuiClearButton deleteButton;
    private GuiClearButton cancelButton;

    private int sidebarX, sidebarY, sidebarH;

    public interface MassDeleteCallback {
        List<String> getWayPointNames();
        void onDeleteAll();
        void onCancel();
    }

    public MassDeleteSidebar(GuiScreen parent, MassDeleteCallback callback) {
        this.parentScreen = parent;
        this.callback = callback;
    }

    @Override
    public void initGui() {
        sidebarX = width - SIDEBAR_W;
        sidebarY = 0;
        sidebarH = height;

        int btnW = SIDEBAR_W - (PADDING * 2);
        int btnY = height - 20 - 8 - 20;

        this.deleteButton = new GuiClearButton(0, sidebarX + PADDING, btnY, btnW, 20, "DELETE");
        this.cancelButton = new GuiClearButton(1, sidebarX + PADDING, btnY + 20 + 8, btnW, 20, "Cancel");

        this.buttonList.clear();
        this.buttonList.add(this.deleteButton);
        this.buttonList.add(this.cancelButton);
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        this.drawDefaultBackground();

        RoundedRectHelper.drawRoundedRect(sidebarX - 1, sidebarY, sidebarX, sidebarY + sidebarH, 0, BORDER);
        RoundedRectHelper.drawRoundedRect(sidebarX, sidebarY, sidebarX + SIDEBAR_W, sidebarY + sidebarH, 0, MENU_BG);

        drawString(fontRendererObj, "Mass Delete", sidebarX + PADDING, PADDING, TEXT_MAIN);

        List<String> names = this.callback != null ? this.callback.getWayPointNames() : null;

        int listY = PADDING + 16;
        int listBottom = this.deleteButton.yPosition - 10;

        if (names == null || names.isEmpty()) {
            drawString(fontRendererObj, "No way points", sidebarX + PADDING, listY, TEXT_DIM);
        } else {
            for (String name : names) {
                if (listY + ROW_H > listBottom) {
                    drawString(fontRendererObj, "...", sidebarX + PADDING, listY, TEXT_DIM);
                    break;
                }
                String trimmed = fontRendererObj.trimStringToWidth(name, SIDEBAR_W - (PADDING * 2));
                drawString(fontRendererObj, trimmed, sidebarX + PADDING, listY, TEXT_MAIN);
                listY += ROW_H;
            }
        }

        super.drawScreen(mx, my, pt);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            if (this.callback != null) this.callback.onDeleteAll();
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 1) {
            if (this.callback != null) this.callback.onCancel();
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (key == 1) {
            if (this.callback != null) this.callback.onCancel();
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}