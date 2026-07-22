package net.minecraft.apolloclient.gui.hud;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiClearButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.RoundedRectHelper;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabListSidebar extends GuiScreen {

    private static final int MENU_BG   = 0xFF0E1013;
    private static final int TEXT_MAIN = 0xFFF2F3F5;
    private static final int TEXT_DIM  = 0xFF8B8F97;
    private static final int BORDER    = 0xFF24262B;
    private static final int TEXTBOX   = 0xFF1B1D21;

    private static final int SIDEBAR_W = 170;
    private static final int PADDING   = 10;
    private static final int ROW_H     = 20;
    private static final int FACE_SIZE = 16;

    private final GuiScreen parentScreen;
    private final List<NetworkPlayerInfo> allPlayers = new ArrayList<>();

    private int page = 0;
    private int pageSize;
    private int totalPages;

    private int sidebarX, sidebarY, sidebarH;
    private int listTop, listBottom;

    private GuiClearButton backButton;
    private GuiClearButton nextButton;

    public TabListSidebar(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        sidebarX = width - SIDEBAR_W;
        sidebarY = 0;
        sidebarH = height;

        refreshPlayers();

        listTop = PADDING + 12 + 8 + 20 + 10;
        int navButtonsY = height - 20 - PADDING;
        listBottom = navButtonsY - 10;

        pageSize = Math.max(1, (listBottom - listTop) / ROW_H);
        totalPages = Math.max(1, (int) Math.ceil(allPlayers.size() / (double) pageSize));
        if (page >= totalPages) page = totalPages - 1;
        if (page < 0) page = 0;

        int navBtnW = (SIDEBAR_W - (PADDING * 2) - 8) / 2;

        this.backButton = new GuiClearButton(0, sidebarX + PADDING, navButtonsY, navBtnW, 20, "Back");
        this.nextButton = new GuiClearButton(1, sidebarX + PADDING + navBtnW + 8, navButtonsY, navBtnW, 20, "Next");

        this.buttonList.clear();
        this.buttonList.add(this.backButton);
        this.buttonList.add(this.nextButton);
    }

    private void refreshPlayers() {
        allPlayers.clear();
        if (this.mc.getNetHandler() != null) {
            Collection<NetworkPlayerInfo> infos = this.mc.getNetHandler().getPlayerInfoMap();
            if (infos != null) allPlayers.addAll(infos);
        }
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        this.drawDefaultBackground();

        RoundedRectHelper.drawRoundedRect(sidebarX - 1, sidebarY, sidebarX, sidebarY + sidebarH, 0, BORDER);
        RoundedRectHelper.drawRoundedRect(sidebarX, sidebarY, sidebarX + SIDEBAR_W, sidebarY + sidebarH, 0, MENU_BG);

        drawString(fontRendererObj, "Server tab list", sidebarX + PADDING, PADDING, TEXT_MAIN);

        String pageLabel = "Page " + (page + 1) + " / " + totalPages;
        drawString(fontRendererObj, pageLabel, sidebarX + PADDING, PADDING + 12, TEXT_DIM);

        int rowY = listTop;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allPlayers.size());

        if (allPlayers.isEmpty()) {
            drawString(fontRendererObj, "No players", sidebarX + PADDING, rowY, TEXT_DIM);
        } else {
            for (int i = start; i < end; i++) {
                NetworkPlayerInfo info = allPlayers.get(i);
                drawPlayerRow(info, sidebarX + PADDING, rowY);
                rowY += ROW_H;
            }
        }

        super.drawScreen(mx, my, pt);
    }

    private void drawPlayerRow(NetworkPlayerInfo info, int x, int y) {
        GameProfile profile = info.getGameProfile();
        String name = profile != null ? profile.getName() : "Unknown";
        int ping = info.getResponseTime();

        RoundedRectHelper.drawRoundedRect(x, y, x + FACE_SIZE, y + FACE_SIZE, 2, TEXTBOX);

        try {
            ResourceLocation skin = info.getLocationSkin();
            if (skin != null) {
                this.mc.getTextureManager().bindTexture(skin);
                GlStateManager.enableBlend();
                drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, FACE_SIZE, FACE_SIZE, 64, 64);
                drawScaledCustomSizeModalRect(x, y, 40, 8, 8, 8, FACE_SIZE, FACE_SIZE, 64, 64);
                GlStateManager.disableBlend();
            }
        } catch (Exception ignored) {
            // fall back to plain box if skin isn't loaded yet
        }

        int textX = x + FACE_SIZE + 6;
        int maxNameW = SIDEBAR_W - (PADDING * 2) - FACE_SIZE - 6 - 30;
        String trimmedName = fontRendererObj.trimStringToWidth(name, maxNameW);
        drawString(fontRendererObj, trimmedName, textX, y + 1, TEXT_MAIN);

        String pingStr = ping + "ms";
        int pingColor = ping < 0 ? TEXT_DIM : (ping < 150 ? 0xFF4CAF50 : (ping < 300 ? 0xFFE0A030 : 0xFFD1453D));
        drawString(fontRendererObj, pingStr, textX, y + 1 + 9, pingColor);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            if (page > 0) page--;
        } else if (button.id == 1) {
            if (page < totalPages - 1) page++;
        }
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (key == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}