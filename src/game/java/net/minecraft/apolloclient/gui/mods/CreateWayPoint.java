package net.minecraft.apolloclient.gui.mods;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiClearButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.RoundedRectHelper;

public class CreateWayPoint extends GuiScreen {

    private static final int MENU_BG   = 0xFF0E1013;
    private static final int TEXT_MAIN = 0xFFF2F3F5;

    private static final int MENU_W = 200;
    private static final int MENU_H = 100;
    private static final int RADIUS = 5;

    private final GuiScreen parentScreen;
    private final WayPointCreateCallback callback;

    private GuiTextField nameField;
    private GuiClearButton doneButton;
    private GuiClearButton cancelButton;

    private int menuX, menuY;

    public interface WayPointCreateCallback {
        void onCreate(String name);
        void onCancel();
    }

    public CreateWayPoint(GuiScreen parent, WayPointCreateCallback callback) {
        this.parentScreen = parent;
        this.callback = callback;
    }

    @Override
    public void initGui() {
        menuX = (width - MENU_W) / 2;
        menuY = (height - MENU_H) / 2;

        int fieldW = MENU_W - 40;
        int fieldX = menuX + 20;
        int fieldY = menuY + (MENU_H / 2) - 8;

        this.nameField = new GuiTextField(0, fontRendererObj, fieldX, fieldY, fieldW, 16);
        this.nameField.setMaxStringLength(32);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setFocused(true);

        int btnW = (MENU_W - 60) / 2;
        int btnY = menuY + MENU_H - 28;

        this.cancelButton = new GuiClearButton(0, menuX + 20, btnY, btnW, 18, "Cancel");
        this.doneButton   = new GuiClearButton(1, menuX + 20 + btnW + 20, btnY, btnW, 18, "Done");

        this.buttonList.clear();
        this.buttonList.add(this.cancelButton);
        this.buttonList.add(this.doneButton);
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        this.drawDefaultBackground();

        RoundedRectHelper.drawRoundedRect(menuX - 1, menuY - 1, menuX + MENU_W + 1, menuY + MENU_H + 1, RADIUS, 0xFF24262B);
        RoundedRectHelper.drawRoundedRect(menuX, menuY, menuX + MENU_W, menuY + MENU_H, RADIUS, MENU_BG);

        drawString(fontRendererObj, "Create Way Point", menuX + 10, menuY + 8, TEXT_MAIN);

        boolean focus = this.nameField.isFocused();
        RoundedRectHelper.drawRoundedRect(
                this.nameField.xPosition - 2, this.nameField.yPosition - 2,
                this.nameField.xPosition + this.nameField.width + 2, this.nameField.yPosition + this.nameField.height + 2,
                RADIUS, focus ? 0xFF202226 : 0xFF1B1D21);
        RoundedRectHelper.drawRoundedRect(
                this.nameField.xPosition, this.nameField.yPosition,
                this.nameField.xPosition + this.nameField.width, this.nameField.yPosition + this.nameField.height,
                RADIUS, MENU_BG);

        if (this.nameField.getText().isEmpty() && !focus) {
            drawString(fontRendererObj, "Waypoint name",
                    this.nameField.xPosition + 4, this.nameField.yPosition + (this.nameField.height - 8) / 2, 0xFF5B5E64);
        } else {
            drawString(fontRendererObj, this.nameField.getText(),
                    this.nameField.xPosition + 4, this.nameField.yPosition + (this.nameField.height - 8) / 2, 0xFFFFFFFF);
        }

        super.drawScreen(mx, my, pt);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            String name = this.nameField.getText().trim();
            if (name.isEmpty()) name = "Way Point";
            if (this.callback != null) this.callback.onCreate(name);
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 0) {
            if (this.callback != null) this.callback.onCancel();
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int btn) {
        super.mouseClicked(mx, my, btn);
        this.nameField.mouseClicked(mx, my, btn);
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (key == 1) {
            if (this.callback != null) this.callback.onCancel();
            this.mc.displayGuiScreen(this.parentScreen);
            return;
        }
        if (key == 28 || key == 156) {
            String name = this.nameField.getText().trim();
            if (name.isEmpty()) name = "Way Point";
            if (this.callback != null) this.callback.onCreate(name);
            this.mc.displayGuiScreen(this.parentScreen);
            return;
        }
        this.nameField.textboxKeyTyped(c, key);
    }

    @Override
    public void updateScreen() {
        this.nameField.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}