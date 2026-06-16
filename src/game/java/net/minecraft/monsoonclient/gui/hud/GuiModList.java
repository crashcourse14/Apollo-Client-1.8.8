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

    // ── Layout ────────────────────────────────────────────────────────────
    private static final int HEADER_HEIGHT   = 24;
    private static final int PADDING         = 8;
    private static final int CAT_BTN_H       = 18;
    private static final int CAT_SPACING     = 3;

    // Card new design
    // Each card is a tall tile: icon row + name + two buttons
    private static final int CARD_W          = 80;  // fixed width, 3 cols typical
    private static final int CARD_H          = 90;
    private static final int CARD_SPACING    = 6;
    private static final int ICON_SIZE       = 28;
    private static final int CARD_BTN_H      = 14;
    private static final int CARD_BTN_W      = 60;

    // Options form
    private static final int FORM_LABEL_W    = 100;
    private static final int FIELD_H         = 14;
    private static final int FIELD_SPACING   = 6;

    // Search
    private static final int SEARCH_W        = 90;
    private static final int SEARCH_H        = 14;

    // Scrollbar
    private static final int SB_W            = 5;
    private static final int SB_PAD          = 3;

    // Sound sliders
    private static final int SLIDER_H        = 10;
    private static final int SLIDER_LABEL_W  = 90;
    private static final int SLIDER_ROW_H    = SLIDER_H + 6;

    // Button IDs
    private static final int ID_OPTIONS_BASE = 2000; // OPTIONS_BASE + i → open options
    private static final int ID_TOGGLE_BASE  = 1000; // TOGGLE_BASE  + i → enable/disable
    private static final int ID_SHADOW_CB    = 500;

    // ── Colors ────────────────────────────────────────────────────────────
    private static final int C_PANEL         = 0xCC0D0E10;
    private static final int C_HEADER        = 0xDD0A0B0D;
    private static final int C_CARD          = 0xAA141618;
    private static final int C_CARD_HOVER    = 0xAA1E2024;
    private static final int C_ACCENT        = 0xFFDD3538;
    private static final int C_ACCENT_DIM    = 0x55DD3538;
    private static final int C_SELECTED_CAT  = 0x88DD3538;
    private static final int C_CAT           = 0x661B1B1F;
    private static final int C_TEXTBOX       = 0x551C1D21;
    private static final int C_BORDER        = 0xFF2A2B2F;
    private static final int C_TEXT          = 0xFFEEEEEE;
    private static final int C_TEXT_DIM      = 0xFF888888;
    private static final int C_ENABLED       = 0xFF44BB66;
    private static final int C_DISABLED      = 0xFF884444;

    // ── State ─────────────────────────────────────────────────────────────
    private enum ViewMode { LIST, OPTIONS }
    private ViewMode viewMode = ViewMode.LIST;
    private HudMod activeMod;

    private final GuiScreen parentScreen;
    private Category selectedCategory = null;
    private final List<HudMod>     currentMods  = new ArrayList<>();
    private final List<TabInfo>    categoryTabs = new ArrayList<>();
    private final List<CardLayout> cardLayouts  = new ArrayList<>();

    // Computed layout
    private int panelX, panelY, panelW, panelH;
    private int catColX, catColY, catColW;
    private int modsAreaX, modsAreaY, modsAreaW, modsAreaH;
    private int searchX, searchY;
    private int closeX, closeY, closeSize;

    // Options view layout
    private int backBtnX, backBtnY, backBtnSize;
    private int formatFX, formatFY, formatFW;
    private int colorFX,  colorFY,  colorFW;
    private int scaleFX,  scaleFY,  scaleFW;
    private int shadowCY;
    private int watchFX,  watchFY,  watchFW;

    // Text fields
    private GuiTextField searchField;
    private GuiTextField formatField;
    private GuiTextField colorField;
    private GuiTextField scaleField;
    private GuiTextField watchNamesField;

    // Scrolling — list view
    private int scrollRow    = 0; // first fully-visible ROW index
    private int totalRows    = 0;
    private int visibleRows  = 0;
    private int cols         = 3;

    // Scrolling — options (sound sliders)
    private int optionsScrollOffset = 0;
    private int optionsTotalHeight  = 0;
    private int draggingSlider      = -1;

    // ── Constructor ───────────────────────────────────────────────────────
    public GuiModList(GuiScreen parent) { this.parentScreen = parent; }

    // ── Init ──────────────────────────────────────────────────────────────
    @Override
    public void initGui() {
        computeLayout();

        if (searchField == null) {
            searchField = new GuiTextField(0, fontRendererObj, searchX, searchY, SEARCH_W, SEARCH_H);
            searchField.setMaxStringLength(32);
            searchField.setEnableBackgroundDrawing(false);
        } else {
            searchField.xPosition = searchX;
            searchField.yPosition = searchY;
        }

        if (viewMode == ViewMode.LIST) {
            buildTabs();
            rebuildMods();
        } else {
            optionsScrollOffset = 0;
            buildOptionsForm();
        }

        super.initGui();
    }

    private void computeLayout() {
        panelW = (int)(width  * 0.65f);
        panelH = (int)(height * 0.70f);
        panelX = (width  - panelW) / 2;
        panelY = (height - panelH) / 2;

        closeSize = 14;
        closeX = panelX + panelW - PADDING - closeSize;
        closeY = panelY + (HEADER_HEIGHT - closeSize) / 2;

        searchX = closeX - PADDING - SEARCH_W;
        searchY = panelY + (HEADER_HEIGHT - SEARCH_H) / 2;

        catColX = panelX + PADDING;
        catColY = panelY + HEADER_HEIGHT + PADDING;
        catColW = Math.max(60, panelW / 5);

        modsAreaX = catColX + catColW + PADDING;
        modsAreaY = catColY;
        modsAreaW = panelX + panelW - PADDING - modsAreaX - SB_W - SB_PAD;
        modsAreaH = panelY + panelH - PADDING - modsAreaY;
    }

    // ── Category tabs ─────────────────────────────────────────────────────
    private void buildTabs() {
        categoryTabs.clear();
        String[]   labels = { "ALL", "HUD", "MISC", "MECHANIC" };
        Category[] cats   = { null, Category.HUD, Category.MISC, Category.MECHANIC };
        int y = catColY;
        for (int i = 0; i < labels.length; i++) {
            categoryTabs.add(new TabInfo(labels[i], cats[i], catColX, y, catColW, CAT_BTN_H));
            y += CAT_BTN_H + CAT_SPACING;
        }
    }

    // ── Mod card layout ───────────────────────────────────────────────────
    private void rebuildMods() {
        buttonList.clear();
        currentMods.clear();
        cardLayouts.clear();
        scrollRow = 0;

        String q = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        for (HudMod m : Client.INSTANCE.hudManager.hudMods) {
            if ((selectedCategory == null || m.category == selectedCategory)
                    && (q.isEmpty() || m.name.toLowerCase().contains(q))) {
                currentMods.add(m);
            }
        }

        // How many cards fit per row given fixed CARD_W
        cols = Math.max(1, modsAreaW / (CARD_W + CARD_SPACING));
        totalRows   = (int) Math.ceil((double) currentMods.size() / cols);
        visibleRows = modsAreaH / (CARD_H + CARD_SPACING);

        for (int i = 0; i < currentMods.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cx  = modsAreaX + col * (CARD_W + CARD_SPACING);
            int cy  = modsAreaY + row * (CARD_H + CARD_SPACING);

            // Options button: centered top half of card
            int optBtnX = cx + (CARD_W - CARD_BTN_W) / 2;
            int optBtnY = cy + ICON_SIZE + PADDING + fontRendererObj.FONT_HEIGHT + 3;

            // Toggle button: centered below options button
            int togBtnX = cx + (CARD_W - CARD_BTN_W) / 2;
            int togBtnY = optBtnY + CARD_BTN_H + 3;

            cardLayouts.add(new CardLayout(cx, cy, col, row, optBtnX, optBtnY, togBtnX, togBtnY));

            // Add buttons — visibility set each frame
            this.buttonList.add(new GuiOptionButton(ID_OPTIONS_BASE + i, optBtnX, optBtnY, CARD_BTN_W, CARD_BTN_H, "Options"));
            this.buttonList.add(new GuiModButton  (ID_TOGGLE_BASE  + i, togBtnX, togBtnY, CARD_BTN_W, CARD_BTN_H, currentMods.get(i).isEnabled()));
        }
    }

    // ── Options form ──────────────────────────────────────────────────────
    private void buildOptionsForm() {
        buttonList.clear();
        if (activeMod == null) return;

        int bx = panelX + PADDING;
        int bw = panelW - 2 * PADDING;
        backBtnX = bx; backBtnY = panelY + HEADER_HEIGHT + PADDING; backBtnSize = CAT_BTN_H;

        int fy = backBtnY + backBtnSize + PADDING + 2;

        if (supports(ModOption.TEXT_FORMAT)) {
            formatFX = bx + FORM_LABEL_W; formatFY = fy; formatFW = bw - FORM_LABEL_W;
            formatField = new GuiTextField(1, fontRendererObj, formatFX, formatFY, formatFW, FIELD_H);
            formatField.setEnableBackgroundDrawing(false); formatField.setMaxStringLength(64);
            formatField.setText(activeMod.textFormat);
            fy += FIELD_H + FIELD_SPACING + 4;
        }
        if (supports(ModOption.TEXT_COLOR)) {
            colorFX = bx + FORM_LABEL_W; colorFY = fy; colorFW = 70;
            colorField = new GuiTextField(2, fontRendererObj, colorFX, colorFY, colorFW, FIELD_H);
            colorField.setEnableBackgroundDrawing(false); colorField.setMaxStringLength(9);
            colorField.setText(String.format("%06X", activeMod.textColor & 0xFFFFFF));
            fy += FIELD_H + FIELD_SPACING + 4;
        }
        if (supports(ModOption.TEXT_SCALE)) {
            scaleFX = bx + FORM_LABEL_W; scaleFY = fy; scaleFW = 50;
            scaleField = new GuiTextField(3, fontRendererObj, scaleFX, scaleFY, scaleFW, FIELD_H);
            scaleField.setEnableBackgroundDrawing(false); scaleField.setMaxStringLength(6);
            scaleField.setText(String.valueOf(activeMod.textScale));
            fy += FIELD_H + FIELD_SPACING + 4;
        }
        if (supports(ModOption.TEXT_SHADOW)) {
            shadowCY = fy;
            buttonList.add(new GuiCheckboxButton(ID_SHADOW_CB, bx + FORM_LABEL_W, shadowCY, FIELD_H, activeMod.textShadow));
            fy += FIELD_H + FIELD_SPACING + 4;
        }
        if (supports(ModOption.WATCH_NAMES) && activeMod instanceof net.minecraft.monsoonclient.gui.mods.FriendAlertMod) {
            watchFX = bx + FORM_LABEL_W; watchFY = fy; watchFW = bw - FORM_LABEL_W;
            watchNamesField = new GuiTextField(4, fontRendererObj, watchFX, watchFY, watchFW, FIELD_H);
            watchNamesField.setEnableBackgroundDrawing(false); watchNamesField.setMaxStringLength(256);
            watchNamesField.setText(((net.minecraft.monsoonclient.gui.mods.FriendAlertMod) activeMod).watchNames);
            fy += FIELD_H + FIELD_SPACING + 4;
        }
        if (supports(ModOption.SOUND_SLIDERS) && activeMod instanceof SoundMod) {
            optionsTotalHeight = ((SoundMod) activeMod).soundEntries.size() * SLIDER_ROW_H;
        } else {
            optionsTotalHeight = 0;
        }
    }

    private boolean supports(ModOption o) {
        if (activeMod == null) return false;
        for (ModOption s : activeMod.supportedOptions) if (s == o) return true;
        return false;
    }

    // ── Draw ──────────────────────────────────────────────────────────────
    @Override
    public void drawScreen(int mx, int my, float pt) {
        MonsoonBranding.render(width, height);
        this.drawDefaultBackground();

        // Panel
        drawRect(panelX, panelY, panelX + panelW, panelY + panelH, C_PANEL);
        // Header bar
        drawRect(panelX, panelY, panelX + panelW, panelY + HEADER_HEIGHT, C_HEADER);
        // Header accent line
        drawRect(panelX, panelY + HEADER_HEIGHT - 1, panelX + panelW, panelY + HEADER_HEIGHT, C_ACCENT);
        // Title
        drawString(fontRendererObj, "§cMonsoon§r Client", panelX + PADDING, panelY + (HEADER_HEIGHT - 8) / 2, C_TEXT);

        if (viewMode == ViewMode.LIST) {
            drawListView(mx, my);
        } else {
            drawOptionsView(mx, my);
        }

        // Render buttons + fields on top
        super.drawScreen(mx, my, pt);

        // Close button drawn last (always on top)
        drawCloseButton(mx, my);

        // Search field drawn on top of everything
        if (viewMode == ViewMode.LIST) {
            drawRect(searchX - 1, searchY - 1, searchX + SEARCH_W + 1, searchY + SEARCH_H + 1, C_BORDER);
            drawRect(searchX, searchY, searchX + SEARCH_W, searchY + SEARCH_H, C_TEXTBOX);
            if (searchField.getText().isEmpty() && !searchField.isFocused()) {
                drawString(fontRendererObj, "Search...", searchX + 3, searchY + (SEARCH_H - 8) / 2, C_TEXT_DIM);
            } else {
                searchField.drawTextBox();
            }
        }
    }

    private void drawListView(int mx, int my) {
        // Category sidebar
        for (TabInfo t : categoryTabs) {
            boolean active = t.category == selectedCategory;
            boolean hov    = isIn(mx, my, t.x, t.y, t.w, t.h);
            drawRect(t.x, t.y, t.x + t.w, t.y + t.h, active ? C_SELECTED_CAT : (hov ? C_CARD_HOVER : C_CAT));
            if (active) drawRect(t.x, t.y, t.x + 2, t.y + t.h, C_ACCENT); // left accent
            drawCenteredString(fontRendererObj, t.label, t.x + t.w / 2, t.y + (t.h - 8) / 2, C_TEXT);
        }

        // Cards — only render rows that are fully within the visible area
        for (int i = 0; i < currentMods.size(); i++) {
            CardLayout c = cardLayouts.get(i);
            // Skip rows above or at/beyond visible window
            if (c.row < scrollRow || c.row >= scrollRow + visibleRows) {
                hideCardButtons(i);
                continue;
            }
            // Screen-space Y for this card
            int screenY = modsAreaY + (c.row - scrollRow) * (CARD_H + CARD_SPACING);
            drawCard(i, c.x, screenY, mx, my);
            positionCardButtons(i, c, screenY);
        }

        // Scrollbar
        if (totalRows > visibleRows) {
            int sbX = modsAreaX + modsAreaW + SB_PAD;
            drawRect(sbX, modsAreaY, sbX + SB_W, modsAreaY + modsAreaH, C_CAT);
            float ratio = (float) visibleRows / totalRows;
            int thumbH  = Math.max(12, (int)(modsAreaH * ratio));
            int maxScroll = totalRows - visibleRows;
            int thumbY  = modsAreaY + (int)((modsAreaH - thumbH) * ((float) scrollRow / maxScroll));
            drawRect(sbX + 1, thumbY, sbX + SB_W - 1, thumbY + thumbH, C_ACCENT);
        }

        // Overdraw mask — covers any partial card bleed at top/bottom of mods area
        drawRect(modsAreaX - 1, panelY + HEADER_HEIGHT, modsAreaX + modsAreaW + SB_W + SB_PAD + 1, modsAreaY, C_PANEL);
        drawRect(modsAreaX - 1, modsAreaY + modsAreaH, modsAreaX + modsAreaW + SB_W + SB_PAD + 1, panelY + panelH, C_PANEL);
    }

    private void drawCard(int i, int cx, int cy, int mx, int my) {
        HudMod mod = currentMods.get(i);
        boolean hov = isIn(mx, my, cx, cy, CARD_W, CARD_H);

        // Card background + border
        drawRect(cx - 1, cy - 1, cx + CARD_W + 1, cy + CARD_H + 1, C_BORDER);
        drawRect(cx, cy, cx + CARD_W, cy + CARD_H, hov ? C_CARD_HOVER : C_CARD);
        // Top accent line
        drawRect(cx, cy, cx + CARD_W, cy + 2, mod.isEnabled() ? C_ACCENT : C_DISABLED);

        // Icon area (centered)
        int iconX = cx + (CARD_W - ICON_SIZE) / 2;
        int iconY = cy + 6;
        drawRect(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, C_TEXTBOX);
        drawRect(iconX - 1, iconY - 1, iconX + ICON_SIZE + 1, iconY + ICON_SIZE + 1, C_BORDER);
        // Placeholder icon label (first letter)
        String initial = mod.name.substring(0, 1).toUpperCase();
        drawCenteredString(fontRendererObj, initial, iconX + ICON_SIZE / 2, iconY + (ICON_SIZE - 8) / 2, C_TEXT);

        // Mod name (centered, trimmed)
        int nameY = iconY + ICON_SIZE + 4;
        String trimmed = fontRendererObj.trimStringToWidth(mod.name, CARD_W - 4);
        drawCenteredString(fontRendererObj, trimmed, cx + CARD_W / 2, nameY, C_TEXT);
    }

    private void positionCardButtons(int i, CardLayout c, int screenY) {
        // Recalculate button Y based on current scroll
        int optBtnY = screenY + ICON_SIZE + PADDING + fontRendererObj.FONT_HEIGHT + 3;
        int togBtnY = optBtnY + CARD_BTN_H + 3;

        for (Object o : buttonList) {
            GuiButton btn = (GuiButton) o;
            if (btn.id == ID_OPTIONS_BASE + i) {
                btn.xPosition = c.x + (CARD_W - CARD_BTN_W) / 2;
                btn.yPosition = optBtnY;
                btn.visible   = true;
            } else if (btn.id == ID_TOGGLE_BASE + i) {
                btn.xPosition = c.x + (CARD_W - CARD_BTN_W) / 2;
                btn.yPosition = togBtnY;
                btn.visible   = true;
            }
        }
    }

    private void hideCardButtons(int i) {
        for (Object o : buttonList) {
            GuiButton btn = (GuiButton) o;
            if (btn.id == ID_OPTIONS_BASE + i || btn.id == ID_TOGGLE_BASE + i) {
                btn.visible = false;
            }
        }
    }

    // ── Options view ──────────────────────────────────────────────────────
    private void drawOptionsView(int mx, int my) {
        // Back button
        boolean backHov = isIn(mx, my, backBtnX, backBtnY, backBtnSize * 3, backBtnSize);
        drawRect(backBtnX, backBtnY, backBtnX + backBtnSize * 3, backBtnY + backBtnSize, backHov ? C_SELECTED_CAT : C_CAT);
        drawString(fontRendererObj, "< Back", backBtnX + 4, backBtnY + (backBtnSize - 8) / 2, C_TEXT);

        // Mod name header
        drawString(fontRendererObj, "§c" + activeMod.name + " §rOptions",
                backBtnX + backBtnSize * 3 + PADDING, backBtnY + (backBtnSize - 8) / 2, C_TEXT);

        // Divider
        int divY = backBtnY + backBtnSize + 4;
        drawRect(panelX + PADDING, divY, panelX + panelW - PADDING, divY + 1, C_BORDER);

        if (supports(ModOption.TEXT_FORMAT) && formatField != null)
            drawFormRow("Text Format",  formatFX,  formatFY,  formatFW,  formatField);
        if (supports(ModOption.TEXT_COLOR) && colorField != null)
            drawFormRow("Text Color",   colorFX,   colorFY,   colorFW,   colorField);
        if (supports(ModOption.TEXT_SCALE) && scaleField != null)
            drawFormRow("Text Scale",   scaleFX,   scaleFY,   scaleFW,   scaleField);
        if (supports(ModOption.TEXT_SHADOW))
            drawString(fontRendererObj, "Text Shadow", panelX + PADDING, shadowCY + (FIELD_H - 8) / 2, 0xFFCFCFCF);
        if (supports(ModOption.WATCH_NAMES) && watchNamesField != null) {
            drawFormRow("Watch Names", watchFX, watchFY, watchFW, watchNamesField);
            drawString(fontRendererObj, "§7e.g. friend1,friend2", watchFX, watchFY + FIELD_H + 2, 0xFFAAAAAA);
        }
        if (supports(ModOption.SOUND_SLIDERS) && activeMod instanceof SoundMod)
            drawSoundSliders((SoundMod) activeMod, mx, my);
    }

    private void drawFormRow(String label, int fx, int fy, int fw, GuiTextField field) {
        drawString(fontRendererObj, label, panelX + PADDING, fy + (FIELD_H - 8) / 2, 0xFFCFCFCF);
        drawRect(fx - 1, fy - 1, fx + fw + 1, fy + FIELD_H + 1, C_BORDER);
        drawRect(fx, fy, fx + fw, fy + FIELD_H, C_TEXTBOX);
        field.drawTextBox();
    }

    private void drawSoundSliders(SoundMod mod, int mx, int my) {
        int aX = panelX + PADDING;
        int aY = panelY + HEADER_HEIGHT + PADDING + backBtnSize + PADDING + 6;
        int aW = panelW - 2 * PADDING - SB_W - SB_PAD;
        int aH = panelY + panelH - PADDING - aY;
        int sW = aW - SLIDER_LABEL_W - PADDING;

        for (int i = 0; i < mod.soundEntries.size(); i++) {
            SoundEntry e   = mod.soundEntries.get(i);
            int rowY       = aY + i * SLIDER_ROW_H - optionsScrollOffset;
            if (rowY + SLIDER_H < aY || rowY > aY + aH) continue;
            int sX = aX + SLIDER_LABEL_W + PADDING;
            int sY = rowY + (SLIDER_ROW_H - SLIDER_H) / 2;

            drawString(fontRendererObj, fontRendererObj.trimStringToWidth(e.name, SLIDER_LABEL_W - 4),
                    aX, sY + (SLIDER_H - 8) / 2, 0xFFCFCFCF);
            drawRect(sX - 1, sY - 1, sX + sW + 1, sY + SLIDER_H + 1, C_BORDER);
            drawRect(sX, sY, sX + sW, sY + SLIDER_H, C_CAT);
            int fill = (int)(sW * e.volume);
            if (fill > 0) drawRect(sX, sY, sX + fill, sY + SLIDER_H, C_ACCENT_DIM);
            int tx = sX + fill - 2;
            drawRect(tx, sY - 1, tx + 4, sY + SLIDER_H + 1, C_TEXT);
            String pct = (int)(e.volume * 100) + "%";
            drawCenteredString(fontRendererObj, pct, sX + sW / 2, sY + (SLIDER_H - 8) / 2, C_TEXT);
        }

        if (optionsTotalHeight > aH) {
            int sbX = aX + aW + SB_PAD;
            drawRect(sbX, aY, sbX + SB_W, aY + aH, C_CAT);
            int tH  = Math.max(12, (int)(aH * (float) aH / optionsTotalHeight));
            int max = optionsTotalHeight - aH;
            int tY  = aY + (max > 0 ? (int)((aH - tH) * (float) optionsScrollOffset / max) : 0);
            drawRect(sbX + 1, tY, sbX + SB_W - 1, tY + tH, C_ACCENT);
        }
    }

    private void drawCloseButton(int mx, int my) {
        boolean hov = isIn(mx, my, closeX, closeY, closeSize, closeSize);
        drawRect(closeX, closeY, closeX + closeSize, closeY + closeSize, hov ? C_ACCENT : C_CAT);
        drawCenteredString(fontRendererObj, "✕", closeX + closeSize / 2, closeY + (closeSize - 8) / 2, C_TEXT);
    }

    // ── Input ─────────────────────────────────────────────────────────────
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = net.lax1dude.eaglercraft.v1_8.Mouse.getEventDWheel();
        if (wheel == 0) return;
        int dir = -Integer.signum(wheel);
        if (viewMode == ViewMode.LIST) {
            scrollRow = Math.max(0, Math.min(scrollRow + dir, Math.max(0, totalRows - visibleRows)));
        } else if (supports(ModOption.SOUND_SLIDERS)) {
            int aY = panelY + HEADER_HEIGHT + PADDING + backBtnSize + PADDING + 6;
            int aH = panelY + panelH - PADDING - aY;
            int max = Math.max(0, optionsTotalHeight - aH);
            optionsScrollOffset = Math.max(0, Math.min(optionsScrollOffset + dir * SLIDER_ROW_H, max));
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int btn) {
        super.mouseClicked(mx, my, btn);
        if (btn != 0) return;

        if (isIn(mx, my, closeX, closeY, closeSize, closeSize)) {
            mc.displayGuiScreen(parentScreen);
            return;
        }

        if (viewMode == ViewMode.OPTIONS) {
            if (supports(ModOption.TEXT_FORMAT)  && formatField    != null) formatField.mouseClicked(mx, my, btn);
            if (supports(ModOption.TEXT_COLOR)   && colorField     != null) colorField.mouseClicked(mx, my, btn);
            if (supports(ModOption.TEXT_SCALE)   && scaleField     != null) scaleField.mouseClicked(mx, my, btn);
            if (supports(ModOption.WATCH_NAMES)  && watchNamesField!= null) watchNamesField.mouseClicked(mx, my, btn);
            if (supports(ModOption.SOUND_SLIDERS) && activeMod instanceof SoundMod) {
                int idx = sliderAt(mx, my, (SoundMod) activeMod);
                if (idx >= 0) { draggingSlider = idx; updateSlider(mx, (SoundMod) activeMod, idx); return; }
            }
            if (isIn(mx, my, backBtnX, backBtnY, backBtnSize * 3, backBtnSize)) {
                viewMode = ViewMode.LIST; draggingSlider = -1; initGui();
            }
            return;
        }

        searchField.mouseClicked(mx, my, btn);

        for (TabInfo t : categoryTabs) {
            if (isIn(mx, my, t.x, t.y, t.w, t.h)) {
                selectedCategory = t.category;
                rebuildMods();
                return;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mx, int my, int btn, long time) {
        super.mouseClickMove(mx, my, btn, time);
        if (draggingSlider >= 0 && activeMod instanceof SoundMod)
            updateSlider(mx, (SoundMod) activeMod, draggingSlider);
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        super.mouseReleased(mx, my, state);
        draggingSlider = -1;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        int id = button.id;
        if (viewMode == ViewMode.LIST) {
            if (id >= ID_OPTIONS_BASE) {
                int i = id - ID_OPTIONS_BASE;
                if (i < currentMods.size()) {
                    activeMod = currentMods.get(i);
                    viewMode  = ViewMode.OPTIONS;
                    initGui();
                }
            } else if (id >= ID_TOGGLE_BASE) {
                int i = id - ID_TOGGLE_BASE;
                if (i < currentMods.size()) {
                    currentMods.get(i).toggle();
                    ((GuiModButton) button).setToggled(currentMods.get(i).isEnabled());
                }
            }
        } else if (id == ID_SHADOW_CB) {
            activeMod.textShadow = !activeMod.textShadow;
            ((GuiCheckboxButton) button).setChecked(activeMod.textShadow);
        }
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (viewMode == ViewMode.OPTIONS) {
            if (supports(ModOption.TEXT_FORMAT)  && formatField    != null && formatField.isFocused()) {
                if (key == 1) { formatField.setFocused(false); return; }
                formatField.textboxKeyTyped(c, key); activeMod.textFormat = formatField.getText(); return;
            }
            if (supports(ModOption.TEXT_COLOR)   && colorField     != null && colorField.isFocused()) {
                if (key == 1) { colorField.setFocused(false); return; }
                colorField.textboxKeyTyped(c, key);
                Integer p = parseHex(colorField.getText()); if (p != null) activeMod.textColor = p; return;
            }
            if (supports(ModOption.TEXT_SCALE)   && scaleField     != null && scaleField.isFocused()) {
                if (key == 1) { scaleField.setFocused(false); return; }
                scaleField.textboxKeyTyped(c, key);
                try { float s = Float.parseFloat(scaleField.getText()); if (s > 0) activeMod.textScale = s; } catch (NumberFormatException ignored) {} return;
            }
            if (supports(ModOption.WATCH_NAMES)  && watchNamesField!= null && watchNamesField.isFocused()) {
                if (key == 1) { watchNamesField.setFocused(false); return; }
                watchNamesField.textboxKeyTyped(c, key);
                ((net.minecraft.monsoonclient.gui.mods.FriendAlertMod) activeMod).watchNames = watchNamesField.getText(); return;
            }
            if (key == 1) { viewMode = ViewMode.LIST; draggingSlider = -1; initGui(); }
            return;
        }
        if (searchField.isFocused()) {
            if (key == 1) { searchField.setFocused(false); return; }
            searchField.textboxKeyTyped(c, key); rebuildMods(); return;
        }
        if (key == 1) mc.displayGuiScreen(parentScreen);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (viewMode == ViewMode.LIST) {
            searchField.updateCursorCounter();
        } else {
            if (supports(ModOption.TEXT_FORMAT)  && formatField    != null) formatField.updateCursorCounter();
            if (supports(ModOption.TEXT_COLOR)   && colorField     != null) colorField.updateCursorCounter();
            if (supports(ModOption.TEXT_SCALE)   && scaleField     != null) scaleField.updateCursorCounter();
            if (supports(ModOption.WATCH_NAMES)  && watchNamesField!= null) watchNamesField.updateCursorCounter();
        }
    }

    // ── Slider helpers ────────────────────────────────────────────────────
    private int sliderAt(int mx, int my, SoundMod mod) {
        int aX = panelX + PADDING;
        int aY = panelY + HEADER_HEIGHT + PADDING + backBtnSize + PADDING + 6;
        int aW = panelW - 2 * PADDING - SB_W - SB_PAD;
        int sX = aX + SLIDER_LABEL_W + PADDING;
        int sW = aW - SLIDER_LABEL_W - PADDING;
        for (int i = 0; i < mod.soundEntries.size(); i++) {
            int sY = aY + i * SLIDER_ROW_H - optionsScrollOffset + (SLIDER_ROW_H - SLIDER_H) / 2;
            if (isIn(mx, my, sX, sY - 2, sW, SLIDER_H + 4)) return i;
        }
        return -1;
    }

    private void updateSlider(int mx, SoundMod mod, int i) {
        int aX = panelX + PADDING;
        int aW = panelW - 2 * PADDING - SB_W - SB_PAD;
        int sX = aX + SLIDER_LABEL_W + PADDING;
        int sW = aW - SLIDER_LABEL_W - PADDING;
        float t = Math.max(0f, Math.min(1f, (float)(mx - sX) / sW));
        mod.soundEntries.get(i).volume = Math.round(t * 20) / 20.0f;
    }

    // ── Misc helpers ──────────────────────────────────────────────────────
    private Integer parseHex(String h) {
        h = h.trim().replace("#", "");
        if (h.length() != 6 && h.length() != 8) return null;
        try { long v = Long.parseLong(h, 16); return (int)(h.length() == 6 ? v | 0xFF000000L : v); }
        catch (NumberFormatException e) { return null; }
    }

    private boolean isIn(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // ── Inner classes ─────────────────────────────────────────────────────
    private static class TabInfo {
        final String label; final Category category; final int x, y, w, h;
        TabInfo(String l, Category c, int x, int y, int w, int h) { label=l; category=c; this.x=x; this.y=y; this.w=w; this.h=h; }
    }

    private static class CardLayout {
        final int x, y, col, row, optBtnX, optBtnY, togBtnX, togBtnY;
        CardLayout(int x, int y, int col, int row, int ox, int oy, int tx, int ty) {
            this.x=x; this.y=y; this.col=col; this.row=row;
            optBtnX=ox; optBtnY=oy; togBtnX=tx; togBtnY=ty;
        }
    }

    /** Simple labeled button for "Options" — reuses GuiButton styling. */
    private static class GuiOptionButton extends GuiButton {
        GuiOptionButton(int id, int x, int y, int w, int h, String label) {
            super(id, x, y, w, h, label);
        }
    }
}

