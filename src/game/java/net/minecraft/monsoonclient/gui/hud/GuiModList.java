package net.minecraft.monsoonclient.gui.hud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCheckboxButton;
import net.minecraft.client.gui.GuiModButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.monsoonclient.Client;
import net.minecraft.monsoonclient.gui.HudMod;

public class GuiModList extends GuiScreen {

    // --- layout constants ---
    private static final int HEADER_HEIGHT = 22;
    private static final int PADDING = 6;
    private static final int CATEGORY_BUTTON_HEIGHT = 16;
    private static final int CATEGORY_SPACING = 3;
    private static final int CARD_HEIGHT = 42;
    private static final int CARD_SPACING = 4;
    private static final int ICON_SIZE = 22;
    private static final int TOGGLE_WIDTH = 44;
    private static final int TOGGLE_HEIGHT = 14;
    private static final int SEARCH_WIDTH = 90;
    private static final int SEARCH_HEIGHT = 14;
    private static final int MIN_CARD_WIDTH = 115;
    private static final int FORM_LABEL_WIDTH = 100;
    private static final int FIELD_HEIGHT = 14;
    private static final int FIELD_SPACING = 4;

    private static final int TOGGLE_ID_BASE = 1000;
    private static final int SHADOW_CHECKBOX_ID = 500;

    // --- colors (fully opaque - no alpha) ---
    private static final int COLOR_PANEL_BG = 0xFF2E2925;
    private static final int COLOR_CARD_BG = 0xFF53554E;
    private static final int COLOR_SELECTED = 0xFF2CADDC;

    private enum ViewMode {
        LIST, OPTIONS
    }

    private final GuiScreen parentScreen;

    private ViewMode viewMode = ViewMode.LIST;
    private HudMod activeMod;

    private Category selectedCategory = null; // null = "ALL"
    private final List<HudMod> currentMods = new ArrayList<>();
    private final List<TabInfo> categoryTabs = new ArrayList<>();
    private final List<CardLayout> cardLayouts = new ArrayList<>();

    private GuiTextField searchField;
    private GuiTextField formatField;
    private GuiTextField colorField;
    private GuiTextField scaleField;

    // computed layout
    private int panelX, panelY, panelWidth, panelHeight;
    private int catColX, catColY, catColWidth;
    private int modsAreaX, modsAreaY, modsAreaWidth, modsAreaHeight;
    private int searchX, searchY;
    private int closeX, closeY, closeButtonSize;

    private int backButtonX, backButtonY, backButtonSize;
    private int formatFieldX, formatFieldY, formatFieldWidth;
    private int colorFieldX, colorFieldY, colorFieldWidth;
    private int scaleFieldX, scaleFieldY, scaleFieldWidth;
    private int shadowCheckY;

    public GuiModList(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        computePanelLayout();

        if (searchField == null) {
            searchField = new GuiTextField(0, this.fontRendererObj, searchX, searchY, SEARCH_WIDTH, SEARCH_HEIGHT);
            searchField.setMaxStringLength(32);
            searchField.setEnableBackgroundDrawing(false);
        } else {
            searchField.xPosition = searchX;
            searchField.yPosition = searchY;
        }

        if (viewMode == ViewMode.LIST) {
            buildCategoryTabs();
            rebuildMods();
        } else {
            buildOptionsForm();
        }

        super.initGui();
    }

    private void computePanelLayout() {
        panelWidth = (int) (this.width * 0.6);
        panelHeight = (int) (this.height * 0.6);
        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;

        closeButtonSize = 14;
        closeX = panelX + panelWidth - PADDING - closeButtonSize;
        closeY = panelY + (HEADER_HEIGHT - closeButtonSize) / 2;

        searchX = closeX - PADDING - SEARCH_WIDTH;
        searchY = panelY + (HEADER_HEIGHT - SEARCH_HEIGHT) / 2;

        catColX = panelX + PADDING;
        catColY = panelY + HEADER_HEIGHT + PADDING;
        catColWidth = Math.max(60, panelWidth / 4);

        modsAreaX = catColX + catColWidth + PADDING;
        modsAreaY = catColY;
        modsAreaWidth = panelX + panelWidth - PADDING - modsAreaX;
        modsAreaHeight = panelY + panelHeight - PADDING - modsAreaY;
    }

    private void buildCategoryTabs() {
        categoryTabs.clear();
        String[] labels = { "ALL", "HUD", "MISC", "MECHANIC"};
        Category[] cats = { null, Category.HUD, Category.MISC, Category.MECHANIC };

        int y = catColY;
        for (int i = 0; i < labels.length; i++) {
            categoryTabs.add(new TabInfo(labels[i], cats[i], catColX, y, catColWidth, CATEGORY_BUTTON_HEIGHT));
            y += CATEGORY_BUTTON_HEIGHT + CATEGORY_SPACING;
        }
    }

    private void rebuildMods() {
        this.buttonList.clear();
        currentMods.clear();
        cardLayouts.clear();

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";

        for (HudMod mod : Client.INSTANCE.hudManager.hudMods) {
            boolean categoryMatch = selectedCategory == null || mod.category == selectedCategory;
            boolean searchMatch = query.isEmpty() || mod.name.toLowerCase().contains(query);
            if (categoryMatch && searchMatch) {
                currentMods.add(mod);
            }
        }

        int cols = modsAreaWidth >= 2 * MIN_CARD_WIDTH + CARD_SPACING ? 2 : 1;
        int cardWidth = (modsAreaWidth - (cols - 1) * CARD_SPACING) / cols;

        for (int i = 0; i < currentMods.size(); i++) {
            HudMod mod = currentMods.get(i);
            int col = i % cols;
            int row = i / cols;
            int cardX = modsAreaX + col * (cardWidth + CARD_SPACING);
            int cardY = modsAreaY + row * (CARD_HEIGHT + CARD_SPACING);

            int toggleX = cardX + cardWidth - PADDING - TOGGLE_WIDTH;
            int toggleY = cardY + (CARD_HEIGHT - TOGGLE_HEIGHT) / 2;

            cardLayouts.add(new CardLayout(cardX, cardY, cardWidth, CARD_HEIGHT, toggleX, toggleY));

            this.buttonList.add(new GuiModButton(TOGGLE_ID_BASE + i, toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT,
                    mod.isEnabled()));
        }
    }

    private void buildOptionsForm() {
        this.buttonList.clear();

        if (activeMod == null) {
            return;
        }

        int bodyX = panelX + PADDING;
        int bodyY = panelY + HEADER_HEIGHT + PADDING;
        int bodyWidth = panelWidth - 2 * PADDING;

        backButtonX = bodyX;
        backButtonY = bodyY;
        backButtonSize = CATEGORY_BUTTON_HEIGHT;

        int formY = bodyY + backButtonSize + PADDING + 2;

        // Text format
        formatFieldX = bodyX + FORM_LABEL_WIDTH;
        formatFieldY = formY;
        formatFieldWidth = bodyWidth - FORM_LABEL_WIDTH;
        formatField = new GuiTextField(1, this.fontRendererObj, formatFieldX, formatFieldY, formatFieldWidth, FIELD_HEIGHT);
        formatField.setEnableBackgroundDrawing(false);
        formatField.setMaxStringLength(64);
        formatField.setText(activeMod.textFormat);

        formY += FIELD_HEIGHT + FIELD_SPACING + 4;

        // Text color (hex)
        colorFieldX = bodyX + FORM_LABEL_WIDTH;
        colorFieldY = formY;
        colorFieldWidth = 70;
        colorField = new GuiTextField(2, this.fontRendererObj, colorFieldX, colorFieldY, colorFieldWidth, FIELD_HEIGHT);
        colorField.setEnableBackgroundDrawing(false);
        colorField.setMaxStringLength(9);
        colorField.setText(String.format("%06X", activeMod.textColor & 0xFFFFFF));

        formY += FIELD_HEIGHT + FIELD_SPACING + 4;

        // Text scale
        scaleFieldX = bodyX + FORM_LABEL_WIDTH;
        scaleFieldY = formY;
        scaleFieldWidth = 50;
        scaleField = new GuiTextField(3, this.fontRendererObj, scaleFieldX, scaleFieldY, scaleFieldWidth, FIELD_HEIGHT);
        scaleField.setEnableBackgroundDrawing(false);
        scaleField.setMaxStringLength(6);
        scaleField.setText(String.valueOf(activeMod.textScale));

        formY += FIELD_HEIGHT + FIELD_SPACING + 4;

        // Text shadow (checkbox)
        shadowCheckY = formY;
        this.buttonList.add(new GuiCheckboxButton(SHADOW_CHECKBOX_ID, bodyX + FORM_LABEL_WIDTH, shadowCheckY,
                FIELD_HEIGHT, activeMod.textShadow));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, COLOR_PANEL_BG);
        this.drawString(this.fontRendererObj, Client.INSTANCE.name, panelX + PADDING,
                panelY + (HEADER_HEIGHT - 8) / 2, 0xFFFFFFFF);

        if (viewMode == ViewMode.LIST) {
            for (TabInfo tab : categoryTabs) {
                boolean active = tab.category == selectedCategory;
                int bg = active ? COLOR_SELECTED : COLOR_CARD_BG;
                drawRect(tab.x, tab.y, tab.x + tab.width, tab.y + tab.height, bg);
                this.drawCenteredString(this.fontRendererObj, tab.label, tab.x + tab.width / 2,
                        tab.y + (tab.height - 8) / 2, 0xFFFFFFFF);
            }

            for (int i = 0; i < currentMods.size(); i++) {
                drawCard(currentMods.get(i), cardLayouts.get(i));
            }
        } else {
            drawOptionsView(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCloseButton(mouseX, mouseY);

        if (viewMode == ViewMode.LIST) {
            drawRect(searchX, searchY, searchX + SEARCH_WIDTH, searchY + SEARCH_HEIGHT, COLOR_CARD_BG);
            if (searchField.getText().isEmpty() && !searchField.isFocused()) {
                this.drawString(this.fontRendererObj, "Search...", searchX, searchY + (SEARCH_HEIGHT - 8) / 2, 0xFF8A8A8A);
            }
            searchField.drawTextBox();
        }
    }

    private void drawCard(HudMod mod, CardLayout c) {
        drawRect(c.x, c.y, c.x + c.width, c.y + c.height, COLOR_CARD_BG);

        int iconX = c.x + 4;
        int iconY = c.y + (c.height - ICON_SIZE) / 2;

        if (mod.icon != null) {
            GlStateManager.color(1F, 1F, 1F, 1F);
            this.mc.getTextureManager().bindTexture(mod.icon);
            drawModalRectWithCustomSizedTexture(iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        } else {
            drawRect(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, COLOR_SELECTED);
            String glyph = mod.name.length() > 0 ? mod.name.substring(0, 1).toUpperCase() : "?";
            this.drawCenteredString(this.fontRendererObj, glyph, iconX + ICON_SIZE / 2, iconY + ICON_SIZE / 2 - 4, 0xFFFFFFFF);
        }

        int textX = iconX + ICON_SIZE + 6;
        int maxTextWidth = c.toggleX - textX - 4;

        String name = this.fontRendererObj.trimStringToWidth(mod.name, maxTextWidth);
        this.drawString(this.fontRendererObj, name, textX, c.y + 5, 0xFFFFFFFF);

        if (mod.description != null && !mod.description.isEmpty()) {
            List<String> lines = this.fontRendererObj.listFormattedStringToWidth(mod.description, (int) (maxTextWidth / 0.7f));
            GlStateManager.pushMatrix();
            GlStateManager.translate(textX, c.y + 16, 0);
            GlStateManager.scale(0.7f, 0.7f, 1.0f);
            for (int i = 0; i < Math.min(2, lines.size()); i++) {
                this.fontRendererObj.drawString(lines.get(i), 0, i * 9, 0xFFAFAFAF);
            }
            GlStateManager.popMatrix();
        }
    }

    private void drawOptionsView(int mouseX, int mouseY) {
        boolean backHovered = isInside(mouseX, mouseY, backButtonX, backButtonY, backButtonSize, backButtonSize);
        drawRect(backButtonX, backButtonY, backButtonX + backButtonSize, backButtonY + backButtonSize,
                backHovered ? COLOR_SELECTED : COLOR_CARD_BG);
        this.drawCenteredString(this.fontRendererObj, "<", backButtonX + backButtonSize / 2,
                backButtonY + (backButtonSize - 8) / 2, 0xFFFFFFFF);

        this.drawString(this.fontRendererObj, activeMod.name + " Options", backButtonX + backButtonSize + PADDING,
                backButtonY + (backButtonSize - 8) / 2, 0xFFFFFFFF);

        drawFormRow("Text Format", formatFieldX, formatFieldY, formatFieldWidth, formatField);
        drawFormRow("Text Color (hex)", colorFieldX, colorFieldY, colorFieldWidth, colorField);
        drawFormRow("Text Scale", scaleFieldX, scaleFieldY, scaleFieldWidth, scaleField);

        this.drawString(this.fontRendererObj, "Text Shadow", panelX + PADDING, shadowCheckY + (FIELD_HEIGHT - 8) / 2, 0xFFCFCFCF);
    }

    private void drawFormRow(String label, int fieldX, int fieldY, int fieldWidth, GuiTextField field) {
        this.drawString(this.fontRendererObj, label, panelX + PADDING, fieldY + (FIELD_HEIGHT - 8) / 2, 0xFFCFCFCF);
        drawRect(fieldX, fieldY, fieldX + fieldWidth, fieldY + FIELD_HEIGHT, COLOR_CARD_BG);
        field.drawTextBox();
    }

    private void drawCloseButton(int mouseX, int mouseY) {
        boolean hovered = isInside(mouseX, mouseY, closeX, closeY, closeButtonSize, closeButtonSize);
        int bg = hovered ? COLOR_SELECTED : COLOR_CARD_BG;
        drawRect(closeX, closeY, closeX + closeButtonSize, closeY + closeButtonSize, bg);
        this.drawCenteredString(this.fontRendererObj, "x", closeX + closeButtonSize / 2,
                closeY + (closeButtonSize - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) {
            return;
        }

        if (isInside(mouseX, mouseY, closeX, closeY, closeButtonSize, closeButtonSize)) {
            this.mc.displayGuiScreen(this.parentScreen);
            return;
        }

        if (viewMode == ViewMode.OPTIONS) {
            formatField.mouseClicked(mouseX, mouseY, mouseButton);
            colorField.mouseClicked(mouseX, mouseY, mouseButton);
            scaleField.mouseClicked(mouseX, mouseY, mouseButton);

            if (isInside(mouseX, mouseY, backButtonX, backButtonY, backButtonSize, backButtonSize)) {
                viewMode = ViewMode.LIST;
                this.initGui();
            }
            return;
        }

        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);

        for (TabInfo tab : categoryTabs) {
            if (isInside(mouseX, mouseY, tab.x, tab.y, tab.width, tab.height)) {
                if (selectedCategory != tab.category) {
                    selectedCategory = tab.category;
                    rebuildMods();
                }
                return;
            }
        }

        for (int i = 0; i < cardLayouts.size(); i++) {
            CardLayout c = cardLayouts.get(i);
            if (isInside(mouseX, mouseY, c.x, c.y, c.width, c.height)) {
                if (!isInside(mouseX, mouseY, c.toggleX, c.toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT)) {
                    activeMod = currentMods.get(i);
                    viewMode = ViewMode.OPTIONS;
                    this.initGui();
                }
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (viewMode == ViewMode.OPTIONS) {
            if (formatField.isFocused()) {
                if (keyCode == 1) {
                    formatField.setFocused(false);
                    return;
                }
                formatField.textboxKeyTyped(typedChar, keyCode);
                activeMod.textFormat = formatField.getText();
                return;
            }

            if (colorField.isFocused()) {
                if (keyCode == 1) {
                    colorField.setFocused(false);
                    return;
                }
                colorField.textboxKeyTyped(typedChar, keyCode);
                Integer parsed = parseHexColor(colorField.getText());
                if (parsed != null) {
                    activeMod.textColor = parsed;
                }
                return;
            }

            if (scaleField.isFocused()) {
                if (keyCode == 1) {
                    scaleField.setFocused(false);
                    return;
                }
                scaleField.textboxKeyTyped(typedChar, keyCode);
                try {
                    float scale = Float.parseFloat(scaleField.getText());
                    if (scale > 0.0f) {
                        activeMod.textScale = scale;
                    }
                } catch (NumberFormatException ignored) {
                }
                return;
            }

            if (keyCode == 1) {
                viewMode = ViewMode.LIST;
                this.initGui();
            }
            return;
        }

        if (this.searchField.isFocused()) {
            if (keyCode == 1) {
                this.searchField.setFocused(false);
                return;
            }
            this.searchField.textboxKeyTyped(typedChar, keyCode);
            rebuildMods();
            return;
        }

        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (viewMode == ViewMode.LIST) {
            this.searchField.updateCursorCounter();
        } else {
            formatField.updateCursorCounter();
            colorField.updateCursorCounter();
            scaleField.updateCursorCounter();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (viewMode == ViewMode.LIST && button.id >= TOGGLE_ID_BASE) {
            int index = button.id - TOGGLE_ID_BASE;
            if (index >= 0 && index < currentMods.size()) {
                HudMod mod = currentMods.get(index);
                mod.toggle();
                ((GuiModButton) button).setToggled(mod.isEnabled());
            }
            return;
        }

        if (viewMode == ViewMode.OPTIONS && button.id == SHADOW_CHECKBOX_ID) {
            activeMod.textShadow = !activeMod.textShadow;
            ((GuiCheckboxButton) button).setChecked(activeMod.textShadow);
        }
    }

    private Integer parseHexColor(String hex) {
        String h = hex.trim();
        if (h.startsWith("#")) {
            h = h.substring(1);
        }
        if (h.length() != 6 && h.length() != 8) {
            return null;
        }
        try {
            long value = Long.parseLong(h, 16);
            if (h.length() == 6) {
                value |= 0xFF000000L;
            }
            return (int) value;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isInside(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
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

    private static class CardLayout {
        final int x, y, width, height, toggleX, toggleY;

        CardLayout(int x, int y, int width, int height, int toggleX, int toggleY) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.toggleX = toggleX;
            this.toggleY = toggleY;
        }
    }
}