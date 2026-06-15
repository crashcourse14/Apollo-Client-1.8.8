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
import net.minecraft.monsoonclient.gui.ModOption;
import net.minecraft.monsoonclient.gui.MonsoonBranding;
import net.minecraft.monsoonclient.gui.SoundEntry;
import net.minecraft.monsoonclient.gui.mods.SoundMod;


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
    private static final int COLOR_PANEL_BG = 0x66111214; //No opacity: 0xFF11121
    private static final int COLOR_CARD_BG = 0x331B1B1B; //No opacity: 0xFF1B1B1B
    private static final int COLOR_TEXTBOX_BG = 0x331C1D21; //No opacity 0xFF1C1D21
    private static final int COLOR_SELECTED = 0x33DD3538; //No opacity 0xFFDD3538

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

    // scrolling
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_PADDING = 3;
    private int scrollOffset = 0;
    private int totalContentHeight = 0;

    // sound sliders
    private static final int SLIDER_HEIGHT = 10;
    private static final int SLIDER_LABEL_WIDTH = 90;
    private static final int SLIDER_ROW_SPACING = 6;
    private static final int SLIDER_ROW_HEIGHT = SLIDER_HEIGHT + SLIDER_ROW_SPACING;
    // options-view scroll
    private int optionsScrollOffset = 0;
    private int optionsTotalHeight = 0;
    // dragging a sound slider
    private int draggingSliderIndex = -1;
    

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
            optionsScrollOffset = 0;
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
        modsAreaWidth = panelX + panelWidth - PADDING - modsAreaX - SCROLLBAR_WIDTH - SCROLLBAR_PADDING;
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
        scrollOffset = 0;

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
        int rows = (int) Math.ceil((double) currentMods.size() / cols);
        totalContentHeight = rows * (CARD_HEIGHT + CARD_SPACING);

        for (int i = 0; i < currentMods.size(); i++) {
            HudMod mod = currentMods.get(i);
            int col = i % cols;
            int row = i / cols;
            int cardX = modsAreaX + col * (cardWidth + CARD_SPACING);
            int cardY = modsAreaY + row * (CARD_HEIGHT + CARD_SPACING);

            int toggleX = cardX + cardWidth - PADDING - TOGGLE_WIDTH;
            int toggleY = cardY + (CARD_HEIGHT - TOGGLE_HEIGHT) / 2;

            cardLayouts.add(new CardLayout(cardX, cardY, cardWidth, CARD_HEIGHT, toggleX, toggleY));

            this.buttonList.add(new GuiModButton(TOGGLE_ID_BASE + i, toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT, mod.isEnabled()));
        }
    }

    private void buildOptionsForm() {
        this.buttonList.clear();

        if (activeMod == null) return;

        int bodyX = panelX + PADDING;
        int bodyY = panelY + HEADER_HEIGHT + PADDING;
        int bodyWidth = panelWidth - 2 * PADDING;

        backButtonX = bodyX;
        backButtonY = bodyY;
        backButtonSize = CATEGORY_BUTTON_HEIGHT;

        int formY = bodyY + backButtonSize + PADDING + 2;

        if (supports(ModOption.TEXT_FORMAT)) {
            formatFieldX = bodyX + FORM_LABEL_WIDTH;
            formatFieldY = formY;
            formatFieldWidth = bodyWidth - FORM_LABEL_WIDTH;
            formatField = new GuiTextField(1, this.fontRendererObj, formatFieldX, formatFieldY, formatFieldWidth, FIELD_HEIGHT);
            formatField.setEnableBackgroundDrawing(false);
            formatField.setMaxStringLength(64);
            formatField.setText(activeMod.textFormat);
            formY += FIELD_HEIGHT + FIELD_SPACING + 4;
        }

        if (supports(ModOption.TEXT_COLOR)) {
            colorFieldX = bodyX + FORM_LABEL_WIDTH;
            colorFieldY = formY;
            colorFieldWidth = 70;
            colorField = new GuiTextField(2, this.fontRendererObj, colorFieldX, colorFieldY, colorFieldWidth, FIELD_HEIGHT);
            colorField.setEnableBackgroundDrawing(false);
            colorField.setMaxStringLength(9);
            colorField.setText(String.format("%06X", activeMod.textColor & 0xFFFFFF));
            formY += FIELD_HEIGHT + FIELD_SPACING + 4;
        }

        if (supports(ModOption.TEXT_SCALE)) {
            scaleFieldX = bodyX + FORM_LABEL_WIDTH;
            scaleFieldY = formY;
            scaleFieldWidth = 50;
            scaleField = new GuiTextField(3, this.fontRendererObj, scaleFieldX, scaleFieldY, scaleFieldWidth, FIELD_HEIGHT);
            scaleField.setEnableBackgroundDrawing(false);
            scaleField.setMaxStringLength(6);
            scaleField.setText(String.valueOf(activeMod.textScale));
            formY += FIELD_HEIGHT + FIELD_SPACING + 4;
        }

        if (supports(ModOption.TEXT_SHADOW)) {
            shadowCheckY = formY;
            this.buttonList.add(new GuiCheckboxButton(SHADOW_CHECKBOX_ID, bodyX + FORM_LABEL_WIDTH, shadowCheckY,
                    FIELD_HEIGHT, activeMod.textShadow));
            formY += FIELD_HEIGHT + FIELD_SPACING + 4;
        }

        // Compute total scrollable height for the options view (used by sound sliders)
        if (supports(ModOption.SOUND_SLIDERS) && activeMod instanceof SoundMod) {
            int sliderCount = ((SoundMod) activeMod).soundEntries.size();
            optionsTotalHeight = sliderCount * SLIDER_ROW_HEIGHT;
        } else {
            optionsTotalHeight = 0;
        }
    }

    /** Returns true if the active mod declares support for the given option. */
    private boolean supports(ModOption option) {
        if (activeMod == null) return false;
        for (ModOption o : activeMod.supportedOptions) {
            if (o == option) return true;
        }
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        MonsoonBranding.render(this.width, this.height);
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

            int scaleFactor = this.mc.gameSettings.guiScale;
            if (scaleFactor == 0) scaleFactor = 4; // auto

            for (int i = 0; i < currentMods.size(); i++) {
                CardLayout c = cardLayouts.get(i);
                CardLayout scrolled = new CardLayout(c.x, c.y - scrollOffset, c.width, c.height, c.toggleX, c.toggleY - scrollOffset);
                // only draw if potentially visible
                if (scrolled.y + scrolled.height > modsAreaY && scrolled.y < modsAreaY + modsAreaHeight) {
                    drawCard(currentMods.get(i), scrolled);
                }
            }

            // Update button positions to match scroll offset
            for (int i = 0; i < cardLayouts.size(); i++) {
                CardLayout c = cardLayouts.get(i);
                GuiButton btn = null;
                for (Object b : this.buttonList) {
                    if (((GuiButton) b).id == TOGGLE_ID_BASE + i) { btn = (GuiButton) b; break; }
                }
                if (btn != null) {
                    btn.yPosition = c.toggleY - scrollOffset;
                    // hide button if outside the visible area
                    btn.visible = btn.yPosition >= modsAreaY && btn.yPosition + TOGGLE_HEIGHT <= modsAreaY + modsAreaHeight;
                }
            }

            // Draw scrollbar
            if (totalContentHeight > modsAreaHeight) {
                int sbX = modsAreaX + modsAreaWidth + SCROLLBAR_PADDING;
                int sbY = modsAreaY;
                int sbHeight = modsAreaHeight;
                // track
                drawRect(sbX, sbY, sbX + SCROLLBAR_WIDTH, sbY + sbHeight, COLOR_CARD_BG);
            }
        } else {
            drawOptionsView(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCloseButton(mouseX, mouseY);

        if (viewMode == ViewMode.LIST) {
            drawRect(searchX, searchY, searchX + SEARCH_WIDTH, searchY + SEARCH_HEIGHT, COLOR_TEXTBOX_BG);
            if (searchField.getText().isEmpty() && !searchField.isFocused()) {
                this.drawString(this.fontRendererObj, "Search...", searchX + 3, searchY + (SEARCH_HEIGHT - this.fontRendererObj.FONT_HEIGHT) / 2, 0xFF8A8A8A);
            }
            if (searchField.isFocused() || !searchField.getText().isEmpty()) {
                String txt = searchField.getText();

                int textX = searchX + (SEARCH_WIDTH / 2) - (fontRendererObj.getStringWidth(txt) / 2);

                drawString(fontRendererObj, txt, textX, searchY + (SEARCH_HEIGHT - 8) / 2, 0xFFFFFFFF);
            }
        }
    }

    private void drawCard(HudMod mod, CardLayout c) {
        // Draw border first, then card background on top so border shows as just an outline
        drawRect(c.x, c.y, c.x + c.width, c.y + c.height, COLOR_CARD_BG);

        int iconX = c.x + 4;
        int iconY = c.y + (c.height - ICON_SIZE) / 2;

        if (mod.icon != null) {
            GlStateManager.color(1F, 1F, 1F, 1F);
            this.mc.getTextureManager().bindTexture(mod.icon);
             drawScaledCustomSizeModalRect(iconX, iconY, 0, 0, 50, 50, 22, 22, 50, 50);
        } else {
            drawRect(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, COLOR_SELECTED);
            String glyph = mod.name.length() > 0 ? mod.name.substring(0, 1).toUpperCase() : "?";
            this.drawCenteredString(this.fontRendererObj, glyph, iconX + ICON_SIZE / 2, iconY + ICON_SIZE / 2 - 4, 0xFFFFFFFF);
        }

        int textX = iconX + ICON_SIZE + 6;
        int maxTextWidth = c.toggleX - textX - 4;

        String name = this.fontRendererObj.trimStringToWidth(mod.name, maxTextWidth);
        this.drawString(this.fontRendererObj, name, textX, c.y + 5, 0xFFFFFFFF);

    }

    private void drawOptionsView(int mouseX, int mouseY) {
        // Back button
        boolean backHovered = isInside(mouseX, mouseY, backButtonX, backButtonY, backButtonSize, backButtonSize);
        drawRect(backButtonX, backButtonY, backButtonX + backButtonSize, backButtonY + backButtonSize,
                backHovered ? COLOR_SELECTED : COLOR_CARD_BG);
        this.drawCenteredString(this.fontRendererObj, "<", backButtonX + backButtonSize / 2,
                backButtonY + (backButtonSize - 8) / 2, 0xFFFFFFFF);
        this.drawString(this.fontRendererObj, activeMod.name + " Options",
                backButtonX + backButtonSize + PADDING,
                backButtonY + (backButtonSize - 8) / 2, 0xFFFFFFFF);

        // Standard text options (only rendered when supported)
        if (supports(ModOption.TEXT_FORMAT) && formatField != null) {
            drawFormRow("Text Format", formatFieldX, formatFieldY, formatFieldWidth, formatField);
        }
        if (supports(ModOption.TEXT_COLOR) && colorField != null) {
            drawFormRow("Text Color (hex)", colorFieldX, colorFieldY, colorFieldWidth, colorField);
        }
        if (supports(ModOption.TEXT_SCALE) && scaleField != null) {
            drawFormRow("Text Scale", scaleFieldX, scaleFieldY, scaleFieldWidth, scaleField);
        }
        if (supports(ModOption.TEXT_SHADOW)) {
            this.drawString(this.fontRendererObj, "Text Shadow", panelX + PADDING,
                    shadowCheckY + (FIELD_HEIGHT - 8) / 2, 0xFFCFCFCF);
        }

        // Sound sliders
        if (supports(ModOption.SOUND_SLIDERS) && activeMod instanceof SoundMod) {
            drawSoundSliders((SoundMod) activeMod, mouseX, mouseY);
        }
    }

    private void drawSoundSliders(SoundMod mod, int mouseX, int mouseY) {
        // The sliders live inside the same panel body, below the back button row.
        // They scroll independently using optionsScrollOffset.
        int areaX = panelX + PADDING;
        int areaY = panelY + HEADER_HEIGHT + PADDING + backButtonSize + PADDING + 2;
        int areaWidth = panelWidth - 2 * PADDING - SCROLLBAR_WIDTH - SCROLLBAR_PADDING;
        int areaHeight = panelY + panelHeight - PADDING - areaY;

        int sliderWidth = areaWidth - SLIDER_LABEL_WIDTH - PADDING;

        // Scissor not available in this Eaglercraft build — sliders are clipped by bounds check below
        int scaleFactor = this.mc.gameSettings.guiScale;
        if (scaleFactor == 0) scaleFactor = 4;

        for (int i = 0; i < mod.soundEntries.size(); i++) {
            SoundEntry entry = mod.soundEntries.get(i);
            int rowY = areaY + i * SLIDER_ROW_HEIGHT - optionsScrollOffset;

            if (rowY + SLIDER_HEIGHT < areaY || rowY > areaY + areaHeight) continue;

            int sliderX = areaX + SLIDER_LABEL_WIDTH + PADDING;
            int sliderY = rowY + (SLIDER_ROW_HEIGHT - SLIDER_HEIGHT) / 2;

            // Label
            String label = this.fontRendererObj.trimStringToWidth(entry.name, SLIDER_LABEL_WIDTH - 4);
            this.drawString(this.fontRendererObj, label, areaX, sliderY + (SLIDER_HEIGHT - 8) / 2, 0xFFCFCFCF);

            // Track
            drawRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + SLIDER_HEIGHT, COLOR_CARD_BG);


            // Fill
            int fillWidth = (int) (sliderWidth * entry.volume);
            if (fillWidth > 0) {
                drawRect(sliderX, sliderY, sliderX + fillWidth, sliderY + SLIDER_HEIGHT, COLOR_SELECTED);
            }

            // Thumb
            int thumbX = sliderX + fillWidth - 2;
            drawRect(thumbX, sliderY - 1, thumbX + 4, sliderY + SLIDER_HEIGHT + 1, 0xFFCFCFCF);

            // Percentage label on slider
            String pct = (int) (entry.volume * 100) + "%";
            int pctX = sliderX + (sliderWidth / 2) - (fontRendererObj.getStringWidth(pct) / 2);
            this.drawString(this.fontRendererObj, pct, pctX, sliderY + (SLIDER_HEIGHT - 8) / 2, 0xFFFFFFFF);
        }

        // Scrollbar for the slider list
        if (optionsTotalHeight > areaHeight) {
            int sbX = areaX + areaWidth + SCROLLBAR_PADDING;
            int sbY = areaY;
            int sbH = areaHeight;
            drawRect(sbX, sbY, sbX + SCROLLBAR_WIDTH, sbY + sbH, COLOR_CARD_BG);
            float thumbRatio = (float) areaHeight / optionsTotalHeight;
            int thumbH = Math.max(16, (int) (sbH * thumbRatio));
            int maxScroll = optionsTotalHeight - areaHeight;
            int thumbY = sbY + (maxScroll > 0 ? (int) ((sbH - thumbH) * ((float) optionsScrollOffset / maxScroll)) : 0);
        }
    }

    private void drawFormRow(String label, int fieldX, int fieldY, int fieldWidth, GuiTextField field) {
        this.drawString(this.fontRendererObj, label, panelX + PADDING, fieldY + (FIELD_HEIGHT - 8) / 2, 0xFFCFCFCF);
        drawRect(fieldX, fieldY, fieldX + fieldWidth, fieldY + FIELD_HEIGHT, COLOR_TEXTBOX_BG);
        String txt = field.getText();

        int centeredX = fieldX + (fieldWidth / 2) - (fontRendererObj.getStringWidth(txt) / 2);

        drawString(fontRendererObj, txt, centeredX, fieldY + (FIELD_HEIGHT - 8) / 2, 0xFFFFFFFF);
    }

    private void drawCloseButton(int mouseX, int mouseY) {
        boolean hovered = isInside(mouseX, mouseY, closeX, closeY, closeButtonSize, closeButtonSize);
        int bg = hovered ? COLOR_SELECTED : COLOR_CARD_BG;
        drawRect(closeX, closeY, closeX + closeButtonSize, closeY + closeButtonSize, bg);
        this.drawCenteredString(this.fontRendererObj, "x", closeX + closeButtonSize / 2,
                closeY + (closeButtonSize - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = net.lax1dude.eaglercraft.v1_8.Mouse.getEventDWheel();
        if (wheel != 0) {
            if (viewMode == ViewMode.LIST) {
                int maxScroll = Math.max(0, totalContentHeight - modsAreaHeight);
                scrollOffset -= Integer.signum(wheel) * (CARD_HEIGHT + CARD_SPACING);
                scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            } else if (viewMode == ViewMode.OPTIONS && supports(ModOption.SOUND_SLIDERS)) {
                int areaHeight = getSoundSliderAreaHeight();
                int maxScroll = Math.max(0, optionsTotalHeight - areaHeight);
                optionsScrollOffset -= Integer.signum(wheel) * SLIDER_ROW_HEIGHT;
                optionsScrollOffset = Math.max(0, Math.min(optionsScrollOffset, maxScroll));
            }
        }
    }

    /** Height of the sound slider viewport inside the options panel. */
    private int getSoundSliderAreaHeight() {
        int areaY = panelY + HEADER_HEIGHT + PADDING + backButtonSize + PADDING + 2;
        return panelY + panelHeight - PADDING - areaY;
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
            if (supports(ModOption.TEXT_FORMAT) && formatField != null)
                formatField.mouseClicked(mouseX, mouseY, mouseButton);
            if (supports(ModOption.TEXT_COLOR) && colorField != null)
                colorField.mouseClicked(mouseX, mouseY, mouseButton);
            if (supports(ModOption.TEXT_SCALE) && scaleField != null)
                scaleField.mouseClicked(mouseX, mouseY, mouseButton);

            // Sound slider — begin drag
            if (supports(ModOption.SOUND_SLIDERS) && activeMod instanceof SoundMod) {
                int sliderDrag = getSliderIndexAt(mouseX, mouseY, (SoundMod) activeMod);
                if (sliderDrag >= 0) {
                    draggingSliderIndex = sliderDrag;
                    updateSliderFromMouse(mouseX, (SoundMod) activeMod, draggingSliderIndex);
                    return;
                }
            }

            if (isInside(mouseX, mouseY, backButtonX, backButtonY, backButtonSize, backButtonSize)) {
                viewMode = ViewMode.LIST;
                draggingSliderIndex = -1;
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
            int scrolledY = c.y - scrollOffset;
            int scrolledToggleY = c.toggleY - scrollOffset;
            if (isInside(mouseX, mouseY, c.x, scrolledY, c.width, c.height)) {
                if (!isInside(mouseX, mouseY, c.toggleX, scrolledToggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT)) {
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
            if (supports(ModOption.TEXT_FORMAT) && formatField != null && formatField.isFocused()) {
                if (keyCode == 1) { formatField.setFocused(false); return; }
                formatField.textboxKeyTyped(typedChar, keyCode);
                activeMod.textFormat = formatField.getText();
                return;
            }
            if (supports(ModOption.TEXT_COLOR) && colorField != null && colorField.isFocused()) {
                if (keyCode == 1) { colorField.setFocused(false); return; }
                colorField.textboxKeyTyped(typedChar, keyCode);
                Integer parsed = parseHexColor(colorField.getText());
                if (parsed != null) activeMod.textColor = parsed;
                return;
            }
            if (supports(ModOption.TEXT_SCALE) && scaleField != null && scaleField.isFocused()) {
                if (keyCode == 1) { scaleField.setFocused(false); return; }
                scaleField.textboxKeyTyped(typedChar, keyCode);
                try {
                    float scale = Float.parseFloat(scaleField.getText());
                    if (scale > 0.0f) activeMod.textScale = scale;
                } catch (NumberFormatException ignored) {}
                return;
            }
            if (keyCode == 1) {
                viewMode = ViewMode.LIST;
                draggingSliderIndex = -1;
                this.initGui();
            }
            return;
        }

        if (this.searchField.isFocused()) {
            if (keyCode == 1) { this.searchField.setFocused(false); return; }
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
            if (supports(ModOption.TEXT_FORMAT) && formatField != null) formatField.updateCursorCounter();
            if (supports(ModOption.TEXT_COLOR)  && colorField  != null) colorField.updateCursorCounter();
            if (supports(ModOption.TEXT_SCALE)  && scaleField  != null) scaleField.updateCursorCounter();
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

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (draggingSliderIndex >= 0 && activeMod instanceof SoundMod) {
            updateSliderFromMouse(mouseX, (SoundMod) activeMod, draggingSliderIndex);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        draggingSliderIndex = -1;
    }

    /**
     * Returns the index of the SoundEntry whose slider track was clicked,
     * or -1 if the click doesn't land on any slider.
     */
    private int getSliderIndexAt(int mouseX, int mouseY, SoundMod mod) {
        int areaX    = panelX + PADDING;
        int areaY    = panelY + HEADER_HEIGHT + PADDING + backButtonSize + PADDING + 2;
        int areaWidth = panelWidth - 2 * PADDING - SCROLLBAR_WIDTH - SCROLLBAR_PADDING;
        int sliderX  = areaX + SLIDER_LABEL_WIDTH + PADDING;
        int sliderWidth = areaWidth - SLIDER_LABEL_WIDTH - PADDING;

        for (int i = 0; i < mod.soundEntries.size(); i++) {
            int rowY   = areaY + i * SLIDER_ROW_HEIGHT - optionsScrollOffset;
            int sliderY = rowY + (SLIDER_ROW_HEIGHT - SLIDER_HEIGHT) / 2;
            if (isInside(mouseX, mouseY, sliderX, sliderY - 2, sliderWidth, SLIDER_HEIGHT + 4)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Maps the mouse X position onto [0, 1] and writes it into the entry's volume.
     */
    private void updateSliderFromMouse(int mouseX, SoundMod mod, int index) {
        int areaX    = panelX + PADDING;
        int areaWidth = panelWidth - 2 * PADDING - SCROLLBAR_WIDTH - SCROLLBAR_PADDING;
        int sliderX  = areaX + SLIDER_LABEL_WIDTH + PADDING;
        int sliderWidth = areaWidth - SLIDER_LABEL_WIDTH - PADDING;

        float t = (float)(mouseX - sliderX) / sliderWidth;
        t = Math.max(0.0f, Math.min(1.0f, t));
        // Snap to 5% increments for easier control
        t = Math.round(t * 20) / 20.0f;
        mod.soundEntries.get(index).volume = t;
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