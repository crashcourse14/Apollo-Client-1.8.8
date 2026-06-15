package net.minecraft.monsoonclient.gui.hud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiClearButton;
import net.minecraft.client.gui.GuiModButton;
import net.minecraft.client.gui.GuiRenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.monsoonclient.Client;
import net.minecraft.monsoonclient.gui.HudMod;

public class GuiModList extends GuiScreen {

    private final GuiScreen parentScreen;

    // --- layout constants ---
    private static final int CARD_WIDTH = 150;
    private static final int CARD_HEIGHT = 104;
    private static final int CARD_SPACING = 10;
    private static final int TAB_Y = 16;
    private static final int TAB_HEIGHT = 20;
    private static final int TAB_SPACING = 6;

    // --- button id ranges ---
    private static final int BACK_BUTTON_ID = 0;
    private static final int OPTIONS_ID_BASE = 1000;
    private static final int TOGGLE_ID_BASE = 2000;

    // --- state ---
    private Category selectedCategory = null; // null = "ALL"
    private final List<HudMod> currentMods = new ArrayList<>();
    private final List<TabInfo> tabs = new ArrayList<>();

    public GuiModList(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.tabs.clear();

        this.buttonList.add(new GuiClearButton(BACK_BUTTON_ID, this.width / 2 - 50, this.height - 26, 100, 20, "Back"));

        buildTabs();

        currentMods.clear();
        for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {
            if (selectedCategory == null || mod.category == selectedCategory) {
                currentMods.add(mod);
            }
        }

        int[] grid = computeGrid();
        int cols = grid[0];
        int gridLeft = grid[1];
        int gridTop = grid[2];

        for (int i = 0; i < currentMods.size(); i++) {
            HudMod mod = currentMods.get(i);
            int col = i % cols;
            int row = i / cols;
            int cardX = gridLeft + col * (CARD_WIDTH + CARD_SPACING);
            int cardY = gridTop + row * (CARD_HEIGHT + CARD_SPACING);

            GuiClearButton optionsButton = new GuiClearButton(OPTIONS_ID_BASE + i, cardX + 4, cardY + 56, CARD_WIDTH - 8, 16, "OPTIONS");
            optionsButton.fontScale = 0.85f;
            this.buttonList.add(optionsButton);

            this.buttonList.add(new GuiModButton(TOGGLE_ID_BASE + i, cardX + 4, cardY + 76, CARD_WIDTH - 8, 18,
                    "ENABLED", "DISABLED", mod.isEnabled()));
        }

        super.initGui();
    }

    private int[] computeGrid() {
        int cols = Math.max(1, (this.width - CARD_SPACING) / (CARD_WIDTH + CARD_SPACING));
        int gridWidth = cols * CARD_WIDTH + (cols - 1) * CARD_SPACING;
        int gridLeft = (this.width - gridWidth) / 2;
        int gridTop = TAB_Y + TAB_HEIGHT + 14;
        return new int[] { cols, gridLeft, gridTop };
    }

    private void buildTabs() {
        String[] labels = { "ALL", "HUD", "PLAYER", "MISC", "RENDER", "GENERAL" };
        Category[] cats = { null, Category.HUD, Category.PLAYER, Category.MISC, Category.RENDER, Category.GENERAL };

        int[] widths = new int[labels.length];
        int totalWidth = 0;
        for (int i = 0; i < labels.length; i++) {
            widths[i] = this.fontRendererObj.getStringWidth(labels[i]) + 20;
            totalWidth += widths[i];
        }
        totalWidth += TAB_SPACING * (labels.length - 1);

        int x = (this.width - totalWidth) / 2;
        for (int i = 0; i < labels.length; i++) {
            tabs.add(new TabInfo(labels[i], cats[i], x, TAB_Y, widths[i], TAB_HEIGHT));
            x += widths[i] + TAB_SPACING;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        int[] grid = computeGrid();
        int cols = grid[0];
        int gridLeft = grid[1];
        int gridTop = grid[2];

        for (int i = 0; i < currentMods.size(); i++) {
            HudMod mod = currentMods.get(i);
            int col = i % cols;
            int row = i / cols;
            int cardX = gridLeft + col * (CARD_WIDTH + CARD_SPACING);
            int cardY = gridTop + row * (CARD_HEIGHT + CARD_SPACING);
            drawCard(mod, cardX, cardY, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        drawTabs(mouseX, mouseY);

        this.drawCenteredString(this.fontRendererObj, "Mods", this.width / 2, 4, 0xFFFFFF);
    }

    private void drawCard(HudMod mod, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + CARD_WIDTH && mouseY >= y && mouseY <= y + CARD_HEIGHT;

        int bg = hovered ? 0x90202020 : 0x80161616;
        GuiRenderUtils.drawRoundedRect(x, y, x + CARD_WIDTH, y + CARD_HEIGHT, 6, bg);
        GuiRenderUtils.drawRoundedBorder(x, y, x + CARD_WIDTH, y + CARD_HEIGHT, 6, 1, 0x40FFFFFF);

        // icon placeholder, color-coded by category
        int iconSize = 28;
        int iconX = x + CARD_WIDTH / 2 - iconSize / 2;
        int iconY = y + 8;
        GuiRenderUtils.drawRoundedRect(iconX, iconY, iconX + iconSize, iconY + iconSize, 6, getCategoryColor(mod.category));

        String glyph = mod.name.length() > 0 ? mod.name.substring(0, 1).toUpperCase() : "?";
        this.drawCenteredString(this.fontRendererObj, glyph, iconX + iconSize / 2, iconY + iconSize / 2 - 4, 0xFFFFFF);

        this.drawCenteredString(this.fontRendererObj, mod.name, x + CARD_WIDTH / 2, iconY + iconSize + 6, 0xE0E0E0);

        drawRect(x + 4, y + 52, x + CARD_WIDTH - 4, y + 53, 0x40FFFFFF);
    }

    private void drawTabs(int mouseX, int mouseY) {
        for (TabInfo tab : tabs) {
            boolean active = tab.category == selectedCategory;
            boolean hovered = mouseX >= tab.x && mouseX <= tab.x + tab.width && mouseY >= tab.y && mouseY <= tab.y + tab.height;

            int bg;
            if (active) {
                bg = 0xFF4A90D9;
            } else if (hovered) {
                bg = 0x90202020;
            } else {
                bg = 0x80161616;
            }

            GuiRenderUtils.drawRoundedRect(tab.x, tab.y, tab.x + tab.width, tab.y + tab.height, 4, bg);
            int textColor = active ? 0xFFFFFF : 0xC0C0C0;
            this.drawCenteredString(this.fontRendererObj, tab.label, tab.x + tab.width / 2, tab.y + (tab.height - 8) / 2, textColor);
        }
    }

    private int getCategoryColor(Category category) {
        switch (category) {
            case HUD: return 0xFF4A90D9;
            case PLAYER: return 0xFF9B59B6;
            case RENDER: return 0xFFE67E22;
            case MISC: return 0xFF7F8C8D;
            case GENERAL: return 0xFF2ECC71;
            default: return 0xFF555555;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            for (TabInfo tab : tabs) {
                if (mouseX >= tab.x && mouseX <= tab.x + tab.width && mouseY >= tab.y && mouseY <= tab.y + tab.height) {
                    if (selectedCategory != tab.category) {
                        selectedCategory = tab.category;
                        this.initGui();
                    }
                    return;
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == BACK_BUTTON_ID) {
            this.mc.displayGuiScreen(this.parentScreen);
            return;
        }

        if (button.id >= TOGGLE_ID_BASE) {
            int index = button.id - TOGGLE_ID_BASE;
            if (index >= 0 && index < currentMods.size()) {
                HudMod mod = currentMods.get(index);
                mod.toggle();
                ((GuiModButton) button).setToggled(mod.isEnabled());
            }
            return;
        }

        if (button.id >= OPTIONS_ID_BASE) {
            int index = button.id - OPTIONS_ID_BASE;
            if (index >= 0 && index < currentMods.size()) {
                this.mc.displayGuiScreen(new GuiModOptions(this, currentMods.get(index)));
            }
        }
    }

    private static class TabInfo {
        final String label;
        final Category category;
        final int x, y, width, height;

        TabInfo(String label, Category category, int x, int y, int width, int height) {
            this.label = label;
            this.category = category;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}