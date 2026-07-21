package net.minecraft.apolloclient.gui.hud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiClearButton; 
import net.minecraft.client.gui.GuiCheckboxButton;
import net.minecraft.client.gui.GuiModButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.apolloclient.Client;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.gui.ApolloBranding;
import net.minecraft.apolloclient.gui.SoundEntry;
import net.minecraft.apolloclient.gui.mods.SoundMod;
import net.minecraft.apolloclient.gui.NotificationManager;
import net.minecraft.client.gui.RoundedRectHelper; 

public class GuiModList extends GuiScreen {

    private static final int HEADER_HEIGHT   = 30;
    private static final int PADDING         = 10;
    private static final int CAT_BTN_H       = 20;
    private static final int CAT_SPACING     = 4;
    private static final int CAT_ICON_W      = 3; 
    private static final int CARD_W          = 92;
    private static final int CARD_H          = 120;
    private static final int CARD_SPACING    = 8;
    private static final int ICON_SIZE       = 30;
    private static final int CARD_BTN_H      = 15;
    private static final int CARD_BTN_W      = 68;
    private static final int CARD_RADIUS_TOP = 3; 
    private static final int FORM_LABEL_W    = 110;
    private static final int FIELD_H         = 15;
    private static final int FIELD_SPACING   = 8;
    private static final int SEARCH_W        = 110;
    private static final int SEARCH_H        = 16;
    private static final int SB_W            = 4;
    private static final int SB_PAD          = 4;
    private static final int SLIDER_H        = 10;
    private static final int SLIDER_LABEL_W  = 96;
    private static final int SLIDER_ROW_H    = SLIDER_H + 8;
    private static final int ID_OPTIONS_BASE = 2000;
    private static final int ID_TOGGLE_BASE  = 1000;
    private static final int ID_CHECKBOX_BASE= 500; 
    private static final int C_PANEL        = 0xFF0E1013; 
    private static final int C_HEADER       = 0xFF16181C; 
    private static final int C_SIDEBAR      = 0xFF121417; 
    private static final int C_CARD         = 0xFF16181C;
    private static final int C_CARD_HOVER   = 0xFF1C1F24;
    private static final int C_CARD_ACTIVE  = 0xFF1A1417; 
    private static final int C_CAT          = 0xFF15171B;
    private static final int C_CAT_HOVER    = 0xFF1C1F24;
    private static final int C_SELECTED_CAT = 0xFF1E1517; 
    private static final int C_ACCENT       = 0xFFB42E30;
    private static final int C_ACCENT_HOVER = 0xFFC93A3C;
    private static final int C_ACCENT_DIM   = 0x55B42E30;
    private static final int C_ACCENT_FAINT = 0x22B42E30;
    private static final int C_TEXTBOX      = 0xFF1B1D21;
    private static final int C_TEXTBOX_FOCUS= 0xFF202226;
    private static final int C_BORDER       = 0xFF24262B;
    private static final int C_BORDER_LIGHT = 0xFF2E3136;
    private static final int C_TEXT         = 0xFFF2F3F5;
    private static final int C_TEXT_DIM     = 0xFF8B8F97;
    private static final int C_TEXT_FAINT   = 0xFF5B5E64;
    private static final int C_ENABLED      = 0xFFB42E30;
    private static final int C_DISABLED     = 0xFF4A4D53;

    private enum ViewMode { LIST, OPTIONS }
    private ViewMode viewMode = ViewMode.LIST;
    private HudMod activeMod;
    private final GuiScreen parentScreen;
    private Category selectedCategory = null;

    private final List<HudMod>     currentMods  = new ArrayList<>();
    private final List<TabInfo>    categoryTabs = new ArrayList<>();
    private final List<CardLayout> cardLayouts  = new ArrayList<>();

    private int panelX, panelY, panelW, panelH;
    private int catColX, catColY, catColW;
    private int modsAreaX, modsAreaY, modsAreaW, modsAreaH;
    private int searchX, searchY;
    private int closeX, closeY, closeSize;
    private int backBtnX, backBtnY, backBtnW, backBtnH;

    private GuiTextField searchField;

    private final Map<ModOption, GuiTextField> optionTextFields = new HashMap<>();
    private final Map<ModOption, GuiCheckboxButton> optionCheckboxes = new HashMap<>();
    private final Map<ModOption, GuiDropdown> optionDropdowns = new HashMap<>();

    private int nextDynamicBtnId = ID_CHECKBOX_BASE;

    private int scrollRow    = 0;
    private int totalRows    = 0;
    private int visibleRows  = 0;
    private int cols         = 3;
    private int optionsScrollOffset = 0;
    private int optionsTotalHeight  = 0;
    private int draggingSlider      = -1;

    public GuiModList(GuiScreen parent) { this.parentScreen = parent; }

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
        panelW = (int)(width  * 0.68f);
        panelH = (int)(height * 0.72f);
        panelX = (width  - panelW) / 2;
        panelY = (height - panelH) / 2;

        closeSize = 16;
        closeX = panelX + panelW - PADDING - closeSize;
        closeY = panelY + (HEADER_HEIGHT - closeSize) / 2;

        searchX = closeX - PADDING - SEARCH_W;
        searchY = panelY + (HEADER_HEIGHT - SEARCH_H) / 2;

        catColX = panelX + PADDING;
        catColY = panelY + HEADER_HEIGHT + PADDING;
        catColW = Math.max(72, panelW / 5);

        modsAreaX = catColX + catColW + PADDING;
        modsAreaY = catColY;
        modsAreaW = panelX + panelW - PADDING - modsAreaX - SB_W - SB_PAD;
        modsAreaH = panelY + panelH - PADDING - modsAreaY;
    }

    private void buildTabs() {
        categoryTabs.clear();
        String[]   labels = { "All", "HUD", "Misc", "Mechanic" };
        Category[] cats   = { null, Category.HUD, Category.MISC, Category.MECHANIC };
        int y = catColY;
        for (int i = 0; i < labels.length; i++) {
            categoryTabs.add(new TabInfo(labels[i], cats[i], catColX, y, catColW, CAT_BTN_H));
            y += CAT_BTN_H + CAT_SPACING;
        }
    }

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

        cols = Math.max(1, modsAreaW / (CARD_W + CARD_SPACING));
        totalRows   = (int) Math.ceil((double) currentMods.size() / cols);
        visibleRows = Math.max(1, modsAreaH / (CARD_H + CARD_SPACING));

        for (int i = 0; i < currentMods.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cx  = modsAreaX + col * (CARD_W + CARD_SPACING);
            int cy  = modsAreaY + row * (CARD_H + CARD_SPACING);

            int togBtnX = cx + (CARD_W - CARD_BTN_W) / 2;
            int togBtnY = cy + CARD_H - CARD_BTN_H - 8;
            int optBtnX = togBtnX;
            int optBtnY = togBtnY - CARD_BTN_H - 4;

            cardLayouts.add(new CardLayout(cx, cy, col, row, optBtnX, optBtnY, togBtnX, togBtnY));
            this.buttonList.add(new GuiOptionButton(ID_OPTIONS_BASE + i, optBtnX, optBtnY, CARD_BTN_W, CARD_BTN_H, "Options"));
            this.buttonList.add(new GuiModButton  (ID_TOGGLE_BASE  + i, togBtnX, togBtnY, CARD_BTN_W, CARD_BTN_H, currentMods.get(i).isEnabled()));
        }
    }

    private void buildOptionsForm() {
        buttonList.clear();
        optionTextFields.clear();
        optionCheckboxes.clear();
        optionDropdowns.clear(); 
        nextDynamicBtnId = ID_CHECKBOX_BASE;

        if (activeMod == null) return;

        int bx = panelX + PADDING;
        int bw = panelW - 2 * PADDING;
        backBtnX = bx; backBtnY = panelY + HEADER_HEIGHT + PADDING; backBtnW = 52; backBtnH = CAT_BTN_H;

        int fy = backBtnY + backBtnH + PADDING + 6;

        for (ModOption opt : activeMod.supportedOptions) {
            int fieldX = bx + FORM_LABEL_W;
            int fieldW = bw - FORM_LABEL_W;
            int maxLen = 32;

            if (opt.type == ModOption.OptionType.COLOR) {
                fieldW = 74; maxLen = 9;
            } else if (opt.type == ModOption.OptionType.NUMBER) {
                fieldW = 54; maxLen = 6;
            } else if (opt.type == ModOption.OptionType.STRING) {
                maxLen = 256;
            }

            if (opt.type == ModOption.OptionType.BOOLEAN) {
                GuiCheckboxButton cb = new GuiCheckboxButton(nextDynamicBtnId++, fieldX, fy, FIELD_H, activeMod.getOptionBoolean(opt));
                optionCheckboxes.put(opt, cb);
                buttonList.add(cb);
                fy += FIELD_H + FIELD_SPACING + 4;
            } else if (opt == ModOption.BEAM_TYPE) {
                List<String> opts = Arrays.asList("Corner", "Full");
                int sel = activeMod.getOptionString(opt).equalsIgnoreCase("full") ? 1 : 0;
                optionDropdowns.put(opt, new GuiDropdown(fieldX, fy, fieldW, FIELD_H, opts, sel));
                fy += FIELD_H + FIELD_SPACING + 4;
            } else if (opt.type != ModOption.OptionType.CUSTOM) {
                GuiTextField field = new GuiTextField(nextDynamicBtnId++, fontRendererObj, fieldX, fy, fieldW, FIELD_H);
                field.setEnableBackgroundDrawing(false);
                field.setMaxStringLength(maxLen);

                if (opt.type == ModOption.OptionType.STRING) field.setText(activeMod.getOptionString(opt));
                else if (opt.type == ModOption.OptionType.COLOR) field.setText(String.format("%08X", activeMod.getOptionColor(opt)));
                else if (opt.type == ModOption.OptionType.NUMBER) field.setText(String.valueOf(activeMod.getOptionNumber(opt)));

                optionTextFields.put(opt, field);
                fy += FIELD_H + FIELD_SPACING + 4;
            } else {
                if (opt == ModOption.SOUND_SLIDERS && activeMod instanceof SoundMod) {
                    optionsTotalHeight = ((SoundMod) activeMod).soundEntries.size() * SLIDER_ROW_H;
                }
            }
        }
    }

    private boolean supports(ModOption o) {
        if (activeMod == null) return false;
        for (ModOption s : activeMod.supportedOptions) if (s == o) return true;
        return false;
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        ApolloBranding.render(width, height);
        this.drawDefaultBackground();

        int radius = 5;

        RoundedRectHelper.drawRoundedRect(panelX - 1, panelY - 1, panelX + panelW + 1, panelY + panelH + 1, radius, C_BORDER);
        RoundedRectHelper.drawRoundedRect(panelX, panelY, panelX + panelW, panelY + panelH, radius, C_PANEL);
        RoundedRectHelper.drawRoundedRect(panelX, panelY, panelX + panelW, panelY + HEADER_HEIGHT, radius, C_HEADER);
        RoundedRectHelper.drawRoundedRect(panelX, panelY + HEADER_HEIGHT - 1, panelX + panelW, panelY + HEADER_HEIGHT, radius, C_ACCENT);

        drawString(fontRendererObj, "§cApollo §rClient",
                panelX + PADDING, panelY + (HEADER_HEIGHT - 8) / 2, C_TEXT);

        if (viewMode == ViewMode.LIST) drawListView(mx, my);
        else drawOptionsView(mx, my);

        super.drawScreen(mx, my, pt);
        drawCloseButton(mx, my);

        if (viewMode == ViewMode.LIST) {
            boolean focus = searchField.isFocused();
            RoundedRectHelper.drawRoundedRect(searchX - 1, searchY - 1, searchX + SEARCH_W + 1, searchY + SEARCH_H + 1, 5, 
                    focus ? C_ACCENT : C_BORDER_LIGHT);
            RoundedRectHelper.drawRoundedRect(searchX, searchY, searchX + SEARCH_W, searchY + SEARCH_H, 5,
                    focus ? C_TEXTBOX_FOCUS : C_TEXTBOX);
            if (searchField.getText().isEmpty() && !searchField.isFocused()) {
                drawString(fontRendererObj, "Search mods...", searchX + 5, searchY + (SEARCH_H - 8) / 2, C_TEXT_FAINT);
            } else {
                searchField.xPosition = searchX + 3;
                searchField.drawTextBox();
            }
        }
    }

    private void drawListView(int mx, int my) {
        RoundedRectHelper.drawRoundedRect(catColX - PADDING / 2, catColY - PADDING / 2,
                 catColX + catColW + PADDING / 2, panelY + panelH - PADDING / 2, 5, C_SIDEBAR);

        for (TabInfo t : categoryTabs) {
            boolean active = t.category == selectedCategory;
            boolean hov    = isIn(mx, my, t.x, t.y, t.w, t.h);
            int bg = active ? C_SELECTED_CAT : (hov ? C_CAT_HOVER : C_CAT);
            drawRect(t.x, t.y, t.x + t.w, t.y + t.h, bg);
            if (active) {
                drawRect(t.x, t.y, t.x + CAT_ICON_W, t.y + t.h, C_ACCENT);
            }
            int textColor = active ? C_TEXT : (hov ? C_TEXT : C_TEXT_DIM);
            drawString(fontRendererObj, t.label, t.x + CAT_ICON_W + 8, t.y + (t.h - 8) / 2, textColor);
        }

        if (currentMods.isEmpty()) {
            String msg = "No mods found";
            drawCenteredString(fontRendererObj, msg,
                    modsAreaX + modsAreaW / 2, modsAreaY + modsAreaH / 2 - 4, C_TEXT_FAINT);
        }

        for (int i = 0; i < currentMods.size(); i++) {
            CardLayout c = cardLayouts.get(i);
            if (c.row < scrollRow || c.row >= scrollRow + visibleRows + 1) {
                hideCardButtons(i);
                continue;
            }
            int screenY = modsAreaY + (c.row - scrollRow) * (CARD_H + CARD_SPACING);
            if (screenY + CARD_H < modsAreaY || screenY > modsAreaY + modsAreaH) {
                hideCardButtons(i);
                continue;
            }
            drawCard(i, c.x, screenY, mx, my);
            positionCardButtons(i, c, screenY);
        }

        if (totalRows > visibleRows) {
            int sbX = modsAreaX + modsAreaW + SB_PAD;
            RoundedRectHelper.drawRoundedRect(sbX, modsAreaY, sbX + SB_W, modsAreaY + modsAreaH, 5, C_CAT);
            float ratio = (float) visibleRows / totalRows;
            int thumbH  = Math.max(14, (int)(modsAreaH * ratio));
            int maxScroll = Math.max(1, totalRows - visibleRows);
            int thumbY  = modsAreaY + (int)((modsAreaH - thumbH) * ((float) scrollRow / maxScroll));
            drawRect(sbX, thumbY, sbX + SB_W, thumbY + thumbH, C_ACCENT_DIM);
        }

        RoundedRectHelper.drawRoundedRect(modsAreaX - 1, panelY + HEADER_HEIGHT, modsAreaX + modsAreaW + SB_W + SB_PAD + 1, modsAreaY, 5, C_PANEL);
        RoundedRectHelper.drawRoundedRect(modsAreaX - 1, modsAreaY + modsAreaH, modsAreaX + modsAreaW + SB_W + SB_PAD + 1, panelY + panelH, 5,  C_PANEL);
    }

    private void drawCard(int i, int cx, int cy, int mx, int my) {
        HudMod mod = currentMods.get(i);
        boolean hov = isIn(mx, my, cx, cy, CARD_W, CARD_H);
        boolean on  = mod.isEnabled();

        int cardBg = hov ? C_CARD_HOVER : (on ? C_CARD_ACTIVE : C_CARD);
        int borderCol = on ? C_ACCENT_DIM : (hov ? C_BORDER_LIGHT : C_BORDER);

        RoundedRectHelper.drawRoundedRect(cx - 1, cy - 1, cx + CARD_W + 1, cy + CARD_H + 1, 5, borderCol);
        RoundedRectHelper.drawRoundedRect(cx, cy, cx + CARD_W, cy + CARD_H, 5, cardBg);
        RoundedRectHelper.drawRoundedRect(cx, cy, cx + CARD_W, cy + CARD_RADIUS_TOP, 5, on ? C_ACCENT : C_DISABLED);

        int iconX = cx + (CARD_W - ICON_SIZE) / 2;
        int iconY = cy + 10;
        drawRect(iconX - 1, iconY - 1, iconX + ICON_SIZE + 1, iconY + ICON_SIZE + 1, C_BORDER);
        drawRect(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, C_TEXTBOX);
        if (on) {
            drawRect(iconX, iconY, iconX + ICON_SIZE, iconY + 2, C_ACCENT_FAINT);
        }
        String initial = mod.name.substring(0, 1).toUpperCase();
        drawCenteredString(fontRendererObj, initial, iconX + ICON_SIZE / 2, iconY + (ICON_SIZE - 8) / 2,
                on ? C_TEXT : C_TEXT_DIM);

        int nameY = iconY + ICON_SIZE + 6;
        String trimmed = fontRendererObj.trimStringToWidth(mod.name, CARD_W - 6);
        drawCenteredString(fontRendererObj, trimmed, cx + CARD_W / 2, nameY, C_TEXT);

    }

    private void positionCardButtons(int i, CardLayout c, int screenY) {
        int togBtnY = screenY + CARD_H - CARD_BTN_H - 8;
        int optBtnY = togBtnY - CARD_BTN_H - 4;

        for (Object o : buttonList) {
            GuiClearButton btn = (GuiClearButton) o;
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
            GuiClearButton btn = (GuiClearButton) o;
            if (btn.id == ID_OPTIONS_BASE + i || btn.id == ID_TOGGLE_BASE + i) {
                btn.visible = false;
            }
        }
    }

    private void drawOptionsView(int mx, int my) {
        boolean backHov = isIn(mx, my, backBtnX, backBtnY, backBtnW, backBtnH);
        RoundedRectHelper.drawRoundedRect(backBtnX, backBtnY, backBtnX + backBtnW, backBtnY + backBtnH, 5, backHov ? C_CAT_HOVER : C_CAT);
        RoundedRectHelper.drawRoundedRect(backBtnX, backBtnY, backBtnX + CAT_ICON_W, backBtnY + backBtnH, 5, backHov ? C_ACCENT : C_BORDER_LIGHT);
        drawString(fontRendererObj, "< Back", backBtnX + CAT_ICON_W + 6, backBtnY + (backBtnH - 8) / 2,
                backHov ? C_TEXT : C_TEXT_DIM);

        drawString(fontRendererObj, "§c" + activeMod.name + " §rOptions",
                backBtnX + backBtnW + PADDING, backBtnY + (backBtnH - 8) / 2, C_TEXT);

        int divY = backBtnY + backBtnH + 6;
        RoundedRectHelper.drawRoundedRect(panelX + PADDING, divY, panelX + panelW - PADDING, divY + 1, 5, C_BORDER);

        for (ModOption opt : activeMod.supportedOptions) {
            if (optionTextFields.containsKey(opt)) {
                GuiTextField field = optionTextFields.get(opt);
                drawFormRow(prettifyName(opt.name()), field.xPosition, field.yPosition, field.width, field);

                if (opt.type == ModOption.OptionType.COLOR) {
                    int swX = field.xPosition + field.width + 6;
                    RoundedRectHelper.drawRoundedRect(swX - 1, field.yPosition - 1, swX + FIELD_H + 1, field.yPosition + FIELD_H + 1, 5, C_BORDER);
                    RoundedRectHelper.drawRoundedRect(swX, field.yPosition, swX + FIELD_H, field.yPosition + FIELD_H, 5, activeMod.getOptionColor(opt));
                }
                if (opt == ModOption.WATCH_NAMES) {
                    drawString(fontRendererObj, "§7e.g. friend1,friend2", field.xPosition, field.yPosition + FIELD_H + 3, C_TEXT_FAINT);
                }
            } else if (optionCheckboxes.containsKey(opt)) {
                GuiCheckboxButton cb = optionCheckboxes.get(opt);
                drawString(fontRendererObj, prettifyName(opt.name()), panelX + PADDING, cb.yPosition + (FIELD_H - 8) / 2, C_TEXT_DIM);
            } else if (optionDropdowns.containsKey(opt)) {
                GuiDropdown dd = optionDropdowns.get(opt);
                drawString(fontRendererObj, prettifyName(opt.name()), panelX + PADDING, dd.y + (FIELD_H - 8) / 2, C_TEXT_DIM);
                dd.draw(fontRendererObj, mx, my);
            } else if (opt == ModOption.SOUND_SLIDERS && activeMod instanceof SoundMod) {
                drawSoundSliders((SoundMod) activeMod, mx, my);
            }
        }
    }

    private String prettifyName(String name) {
        String[] parts = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.length() > 0) {
                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void drawFormRow(String label, int fx, int fy, int fw, GuiTextField field) {
        boolean focus = field.isFocused();
        drawString(fontRendererObj, label, panelX + PADDING, fy + (FIELD_H - 8) / 2, C_TEXT_DIM);
        drawRect(fx - 1, fy - 1, fx + fw + 1, fy + FIELD_H + 1, focus ? C_ACCENT : C_BORDER);
        drawRect(fx, fy, fx + fw, fy + FIELD_H, focus ? C_TEXTBOX_FOCUS : C_TEXTBOX);
        field.drawTextBox();
    }

    private void drawSoundSliders(SoundMod mod, int mx, int my) {
        int aX = panelX + PADDING;
        int aY = panelY + HEADER_HEIGHT + PADDING + backBtnH + PADDING + 8;
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
                    aX, sY + (SLIDER_H - 8) / 2, C_TEXT_DIM);
            drawRect(sX - 1, sY - 1, sX + sW + 1, sY + SLIDER_H + 1, C_BORDER);
            drawRect(sX, sY, sX + sW, sY + SLIDER_H, C_CAT);
            int fill = (int)(sW * e.volume);
            if (fill > 0) drawRect(sX, sY, sX + fill, sY + SLIDER_H, C_ACCENT_DIM);
            int tx = sX + fill - 2;
            drawRect(tx, sY - 1, tx + 4, sY + SLIDER_H + 1, C_ACCENT);
            String pct = (int)(e.volume * 100) + "%";
            drawCenteredString(fontRendererObj, pct, sX + sW / 2, sY + (SLIDER_H - 8) / 2, C_TEXT);
        }

        if (optionsTotalHeight > aH) {
            int sbX = aX + aW + SB_PAD;
            drawRect(sbX, aY, sbX + SB_W, aY + aH, C_CAT);
            int tH  = Math.max(14, (int)(aH * (float) aH / optionsTotalHeight));
            int max = optionsTotalHeight - aH;
            int tY  = aY + (max > 0 ? (int)((aH - tH) * (float) optionsScrollOffset / max) : 0);
            drawRect(sbX, tY, sbX + SB_W, tY + tH, C_ACCENT_DIM);
        }
    }

    private void drawCloseButton(int mx, int my) {
        boolean hov = isIn(mx, my, closeX, closeY, closeSize, closeSize);
        RoundedRectHelper.drawRoundedRect(closeX, closeY, closeX + closeSize, closeY + closeSize, 5, hov ? C_ACCENT : C_CAT);
        drawCenteredString(fontRendererObj, "\u2715", closeX + closeSize / 2, closeY + (closeSize - 8) / 2, C_TEXT);
    }


    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = net.lax1dude.eaglercraft.v1_8.Mouse.getEventDWheel();
        if (wheel == 0) return;
        int dir = -Integer.signum(wheel);
        if (viewMode == ViewMode.LIST) {
            scrollRow = Math.max(0, Math.min(scrollRow + dir, Math.max(0, totalRows - visibleRows)));
        } else if (supports(ModOption.SOUND_SLIDERS)) {
            int aY = panelY + HEADER_HEIGHT + PADDING + backBtnH + PADDING + 8;
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
            for (GuiTextField field : optionTextFields.values()) field.mouseClicked(mx, my, btn);

            for (Map.Entry<ModOption, GuiDropdown> entry : optionDropdowns.entrySet()) {
                if (entry.getValue().mouseClicked(mx, my, btn)) {
                    String selectedVal = entry.getValue().options.get(entry.getValue().selected).toLowerCase();
                    activeMod.setOptionString(entry.getKey(), selectedVal);
                    saveConfig();
                    return;
                }
            }

            if (supports(ModOption.SOUND_SLIDERS) && activeMod instanceof SoundMod) {
                int idx = sliderAt(mx, my, (SoundMod) activeMod);
                if (idx >= 0) { draggingSlider = idx; updateSlider(mx, (SoundMod) activeMod, idx); return; }
            }
            if (isIn(mx, my, backBtnX, backBtnY, backBtnW, backBtnH)) {
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
        } else {
            for (Map.Entry<ModOption, GuiCheckboxButton> entry : optionCheckboxes.entrySet()) {
                if (entry.getValue() == button) {
                    boolean newVal = !activeMod.getOptionBoolean(entry.getKey());
                    activeMod.setOptionBoolean(entry.getKey(), newVal);
                    entry.getValue().setChecked(newVal);
                    saveConfig();
                    return;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (viewMode == ViewMode.OPTIONS) {
            for (Map.Entry<ModOption, GuiTextField> entry : optionTextFields.entrySet()) {
                ModOption opt = entry.getKey();
                GuiTextField field = entry.getValue();
                if (field.isFocused()) {
                    if (key == 1) { field.setFocused(false); return; }
                    field.textboxKeyTyped(c, key);
                    saveOptionFromField(opt, field);
                    return;
                }
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

    private void saveOptionFromField(ModOption opt, GuiTextField field) {
        String text = field.getText();
        try {
            switch (opt.type) {
                case STRING: activeMod.setOptionString(opt, text); break;
                case COLOR:
                    Integer p = (opt == ModOption.OUTLINE_COLOR || opt == ModOption.HOVER_COLOR) 
                                ? parseArgbHex(text) : parseHex(text);
                    if (p != null) activeMod.setOptionColor(opt, p);
                    break;
                case NUMBER:
                    float s = Float.parseFloat(text);
                    activeMod.setOptionNumber(opt, s);
                    break;
            }
            saveConfig();
        } catch (Exception e) { /* Ignore invalid input while typing */ }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (viewMode == ViewMode.LIST) {
            searchField.updateCursorCounter();
        } else {
            for (GuiTextField field : optionTextFields.values()) field.updateCursorCounter();
        }
    }

    private int sliderAt(int mx, int my, SoundMod mod) {
        int aX = panelX + PADDING;
        int aY = panelY + HEADER_HEIGHT + PADDING + backBtnH + PADDING + 8;
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

    private Integer parseHex(String h) {
        h = h.trim().replace("#", "");
        if (h.length() != 6 && h.length() != 8) return null;
        try { long v = Long.parseLong(h, 16); return (int)(h.length() == 6 ? v | 0xFF000000L : v); }
        catch (NumberFormatException e) { return null; }
    }

    private Integer parseArgbHex(String h) {
        h = h.trim().replace("#", "");
        if (h.length() != 8) return null;
        try { return (int) Long.parseLong(h, 16); }
        catch (NumberFormatException e) { return null; }
    }

    private void saveConfig() {
        if (Client.INSTANCE.configManager != null) {
            Client.INSTANCE.configManager.save();
        } else {
            NotificationManager.push("Local Storage", "saveConfig() failed. Is the storage key set?");
        }
    }

    private boolean isIn(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

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

    private static class GuiOptionButton extends GuiButton {
        GuiOptionButton(int id, int x, int y, int w, int h, String label) {
            super(id, x, y, w, h, label);
        }
    }
}