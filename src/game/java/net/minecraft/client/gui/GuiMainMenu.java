package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EagUtils;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftVersion;
import net.lax1dude.eaglercraft.v1_8.Mouse;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.v1_8.crypto.SHA1Digest;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.minecraft.MainMenuSkyboxTexture;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.profile.DefaultSkins;
import net.lax1dude.eaglercraft.v1_8.profile.EaglerProfile;
import net.lax1dude.eaglercraft.v1_8.profile.GuiScreenEditProfile;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenDemoPlayWorldSelection;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerBusy;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerStartup;
import net.lax1dude.eaglercraft.v1_8.update.GuiUpdateCheckerOverlay;
import net.lax1dude.eaglercraft.v1_8.update.UpdateService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.ISaveFormat;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    private static final EaglercraftRandom RANDOM = new EaglercraftRandom();
    private float updateCounter;
    private boolean isDefault;
    private static final int lendef = 5987;
    private static final byte[] sha1def = new byte[] { -107, 77, 108, 49, 11, -100, -8, -119, -1, -100, -85, -55, 18,
            -69, -107, 113, -93, -101, -79, 32 };
    private String splashText;
    private GuiButton buttonResetDemo;
    private int panoramaTimer;
    private static MainMenuSkyboxTexture viewportTexture = null;
    private static MainMenuSkyboxTexture viewportTexture2 = null;
    private boolean field_175375_v = true;
    private String openGLWarning1;
    private String openGLWarning2;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation apolloLogo = new ResourceLocation("apolloclient/title/logo.png");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation(
            "textures/gui/title/minecraft.png");
    private static final ResourceLocation minecraftTitleBlurFlag = new ResourceLocation(
            "textures/gui/title/background/enable_blur.txt");
    private static final ResourceLocation eaglerGuiTextures = new ResourceLocation("eagler:gui/eagler_gui.png");
    
    protected static final ResourceLocation linkTexture = new ResourceLocation("apolloclient/title/link.png");
    protected static final ResourceLocation languageTexture = new ResourceLocation("apolloclient/title/language.png");
    protected static final ResourceLocation gearTexture = new ResourceLocation("apolloclient/title/gear.png");
    
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {
            new ResourceLocation("textures/gui/title/background/panorama_0.png"),
            new ResourceLocation("textures/gui/title/background/panorama_1.png"),
            new ResourceLocation("textures/gui/title/background/panorama_2.png"),
            new ResourceLocation("textures/gui/title/background/panorama_3.png"),
            new ResourceLocation("textures/gui/title/background/panorama_4.png"),
            new ResourceLocation("textures/gui/title/background/panorama_5.png") };
            
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private static ResourceLocation backgroundTexture = null;
    private static ResourceLocation backgroundTexture2 = null;
    private GuiUpdateCheckerOverlay updateCheckerOverlay;
    private GuiButton downloadOfflineButton;
    private boolean enableBlur = true;
    private boolean shouldReload = false;

    private static GuiMainMenu instance = null;

    public GuiMainMenu() {
        instance = this;
        this.splashText = "missingno";
        updateCheckerOverlay = new GuiUpdateCheckerOverlay(false, this);
        BufferedReader bufferedreader = null;

        try {
            ArrayList arraylist = Lists.newArrayList();
            bufferedreader = new BufferedReader(new InputStreamReader(
                    Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(),
                    Charsets.UTF_8));

            String s;
            while ((s = bufferedreader.readLine()) != null) {
                s = s.trim();
                if (!s.isEmpty()) {
                    arraylist.add(s);
                }
            }

            if (!arraylist.isEmpty()) {
                while (true) {
                    this.splashText = (String) arraylist.get(RANDOM.nextInt(arraylist.size()));
                    if (this.splashText.hashCode() != 125780783) {
                        break;
                    }
                }
            }
        } catch (IOException var12) {
            ;
        } finally {
            if (bufferedreader != null) {
                try {
                    bufferedreader.close();
                } catch (IOException var11) {
                    ;
                }
            }
        }

        this.updateCounter = RANDOM.nextFloat();
        reloadResourceFlags();
    }

    private void reloadResourceFlags() {
        if (Minecraft.getMinecraft().isDemo()) {
            this.isDefault = false;
        } else {
            if (!EagRuntime.getConfiguration().isEnableMinceraft()) {
                this.isDefault = false;
            } else {
                try {
                    byte[] bytes = EaglerInputStream.inputStreamToBytesQuiet(Minecraft.getMinecraft()
                            .getResourceManager().getResource(minecraftTitleTextures).getInputStream());
                    if (bytes != null && bytes.length == lendef) {
                        SHA1Digest sha1 = new SHA1Digest();
                        byte[] sha1out = new byte[20];
                        sha1.update(bytes, 0, bytes.length);
                        sha1.doFinal(sha1out, 0);
                        this.isDefault = Arrays.equals(sha1out, sha1def);
                    } else {
                        this.isDefault = false;
                    }
                } catch (IOException e) {
                    this.isDefault = false;
                }
            }
        }

        this.enableBlur = true;
        try {
            byte[] bytes = EaglerInputStream.inputStreamToBytesQuiet(
                    Minecraft.getMinecraft().getResourceManager().getResource(minecraftTitleBlurFlag).getInputStream());
            if (bytes != null) {
                String[] blurCfg = EagUtils.linesArray(new String(bytes, StandardCharsets.UTF_8));
                for (int i = 0; i < blurCfg.length; ++i) {
                    String s = blurCfg[i];
                    if (s.startsWith("enable_blur=")) {
                        s = s.substring(12).trim();
                        this.enableBlur = s.equals("1") || s.equals("true");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            ;
        }
    }

    public static void doResourceReloadHack() {
        if (instance != null) {
            instance.shouldReload = true;
        }
    }

    public void updateScreen() {
        ++this.panoramaTimer;
        if (downloadOfflineButton != null) {
            downloadOfflineButton.enabled = !UpdateService.shouldDisableDownloadButton();
        }
        if (shouldReload) {
            reloadResourceFlags();
            shouldReload = false;
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char parChar1, int parInt1) {
    }

    public void initGui() {
        if (viewportTexture == null) {
            viewportTexture = new MainMenuSkyboxTexture(256, 256);
            backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
            viewportTexture2 = new MainMenuSkyboxTexture(256, 256);
            backgroundTexture2 = this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture2);
        }
        
        this.updateCheckerOverlay.setResolution(mc, width, height);

        final int btnW = 200;
        final int btnH = 20;
        final int btnGap = 6;  
        final int cx = this.width / 2 - btnW / 2;

        int startY = this.height / 2 + 20;

        if (this.mc.isDemo()) {
            this.addDemoButtons(startY, btnH + btnGap);
        } else {
            this.buttonList.add(new GuiClearButton(1, cx, startY, btnW, btnH, "Singleplayer"));
            startY += btnH + btnGap;
            this.buttonList.add(new GuiClearButton(2, cx, startY, btnW, btnH, "Multiplayer"));
            startY += btnH + btnGap;
            
            GuiClearButton websiteBtn = new GuiClearButton(102, cx, startY, btnW, btnH, "Website");
            websiteBtn.bgColor = 0xFF555B66;
            websiteBtn.hoverBgColor = 0xFF7E8794;
            this.buttonList.add(websiteBtn);
            startY += btnH + btnGap;
        }

        
        int profileW = 100;
        int profileX = 5;

        GuiClearButton profileBtn = new GuiClearButton(
            4, profileX, 5, profileW, btnH, EaglerProfile.getName()
        );
        profileBtn.drawCentered = false;
        profileBtn.textPaddingLeft = 24;
        this.buttonList.add(profileBtn);
        

        int linkX = profileX + profileW + 5;
        int linkY = 5;

        int optW = 100;
        int optX = this.width - optW - 5;
        int buttonSize = 20;


        int gearX = this.width - buttonSize - 5;
        int gearY = 5;

        int languageX = gearX - buttonSize - 5;
        int languageY = gearY;


        this.buttonList.add(new GuiLinkButton(101, linkX, linkY, linkTexture));

        this.buttonList.add(new GuiLinkButton(5, languageX, languageY, languageTexture));
        this.buttonList.add(new GuiLinkButton(0, gearX, gearY, gearTexture));


        this.mc.func_181537_a(false);
    }

    private void addDemoButtons(int startY, int step) {
        final int btnW = 200;
        final int btnH = 20;
        final int cx = this.width / 2 - btnW / 2;
        this.buttonList.add(new GuiButton(11, cx, startY, btnW, btnH, "Play Demo World"));
        this.buttonList.add(this.buttonResetDemo = new GuiButton(12, cx, startY + step, btnW, btnH, "Reset Demo World"));
        this.buttonResetDemo.enabled = this.mc.gameSettings.hasCreatedDemoWorld;
    }

    protected void actionPerformed(GuiButton parGuiButton) {
        if (parGuiButton.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (parGuiButton.id == 5) {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (parGuiButton.id == 1) {
            this.mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(this));
        }

        if (parGuiButton.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (parGuiButton.id == 4) {
            this.mc.displayGuiScreen(new GuiScreenEditProfile(this));
        }

        if (parGuiButton.id == 11) {
            this.mc.displayGuiScreen(new GuiScreenDemoPlayWorldSelection(this));
        }

        if (parGuiButton.id == 12) {
            GuiYesNo guiyesno = GuiSelectWorld.func_152129_a(this, "Demo World", 12);
            this.mc.displayGuiScreen(guiyesno);
        }

        if (parGuiButton.id == 15) {
            if (EagRuntime.getConfiguration().isEnableDownloadOfflineButton()) {
                String link = EagRuntime.getConfiguration().getDownloadOfflineButtonLink();
                if (link != null) {
                    EagRuntime.openLink(link);
                } else {
                    UpdateService.quine();
                }
            }
        }

        if (parGuiButton.id == 101) {
            EagRuntime.openLink("https://fluxer.gg/qfHZffiR");
        }
        
        if (parGuiButton.id == 102) {
            EagRuntime.openLink("https://apolloclient.net");
        }
        
        if (parGuiButton.id == 103) {
            EagRuntime.openLink("https://discord.gg/yourdiscordlink");
        }
    }

    public void confirmClicked(boolean flag, int i) {
        if (flag && i == 12) {
            this.mc.gameSettings.hasCreatedDemoWorld = false;
            this.mc.gameSettings.saveOptions();
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.deleteWorldDirectory("Demo World");
            this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(this, "singleplayer.busy.deleting",
                    "singleplayer.failed.deleting", SingleplayerServerController::isReady));
        } else {
            this.mc.displayGuiScreen(this);
        }
    }

    private void drawPanorama(int parInt1, int parInt2, float parFloat1) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        if (enableBlur) {
            GlStateManager.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        } else {
            GlStateManager.gluPerspective(85.0F, (float) width / (float) height, 0.05F, 10.0F);
        }
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        if (enableBlur) {
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        }
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        byte b0 = enableBlur ? (byte) 4 : (byte) 1;

        for (int i = 0; i < b0 * b0; ++i) {
            GlStateManager.pushMatrix();
            float f = ((float) (i % b0) / (float) b0 - 0.5F) / 64.0F;
            float f1 = ((float) (i / b0) / (float) b0 - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, f2);
            GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + parFloat1) / 400.0F) * 25.0F + 20.0F,
                    1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float) this.panoramaTimer + parFloat1) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int j = 0; j < 6; ++j) {
                GlStateManager.pushMatrix();
                if (j == 1) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 3) {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 4) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (j == 5) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(titlePanoramaPaths[j]);
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int k = 255 / (i + 1);
                float f3 = 0.0F;
                worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, k).endVertex();
                worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, k).endVertex();
                worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, k).endVertex();
                worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, k).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private void rotateAndBlurSkybox(float parFloat1) {
        EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        byte b0 = 3;

        for (int i = 0; i < b0; ++i) {
            float f = 1.0F / (float) (i + 1);
            int j = this.width;
            int k = this.height;
            float f1 = (float) (i - b0 / 2) / 256.0F;
            worldrenderer.pos((double) j, (double) k, (double) this.zLevel).tex((double) (0.0F + f1), 1.0D)
                    .color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos((double) j, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 1.0D)
                    .color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 0.0D)
                    .color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, (double) k, (double) this.zLevel).tex((double) (0.0F + f1), 0.0D)
                    .color(1.0F, 1.0F, 1.0F, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(int parInt1, int parInt2, float parFloat1) {
        viewportTexture.bindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.clear(GL_COLOR_BUFFER_BIT);
        this.drawPanorama(parInt1, parInt2, parFloat1);
        viewportTexture2.bindFramebuffer();
        GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.clear(GL_COLOR_BUFFER_BIT);
        this.mc.getTextureManager().bindTexture(backgroundTexture);
        this.rotateAndBlurSkybox(parFloat1);
        viewportTexture.bindFramebuffer();
        this.mc.getTextureManager().bindTexture(backgroundTexture2);
        this.rotateAndBlurSkybox(parFloat1);
        viewportTexture2.bindFramebuffer();
        this.mc.getTextureManager().bindTexture(backgroundTexture);
        this.rotateAndBlurSkybox(parFloat1);
        viewportTexture.bindFramebuffer();
        this.mc.getTextureManager().bindTexture(backgroundTexture2);
        this.rotateAndBlurSkybox(parFloat1);
        viewportTexture2.bindFramebuffer();
        this.mc.getTextureManager().bindTexture(backgroundTexture);
        this.rotateAndBlurSkybox(parFloat1);
        viewportTexture.bindFramebuffer();
        this.mc.getTextureManager().bindTexture(backgroundTexture2);
        this.rotateAndBlurSkybox(parFloat1);

        _wglBindFramebuffer(0x8D40, null);

        this.mc.getTextureManager().bindTexture(backgroundTexture);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
        float f1 = (float) this.height * f / 256.0F;
        float f2 = (float) this.width * f / 256.0F;
        int i = this.width;
        int j = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double) j, (double) this.zLevel).tex((double) (0.5F - f1), (double) (0.5F + f2))
                .color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos((double) i, (double) j, (double) this.zLevel).tex((double) (0.5F - f1), (double) (0.5F - f2))
                .color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos((double) i, 0.0D, (double) this.zLevel).tex((double) (0.5F + f1), (double) (0.5F - f2))
                .color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (0.5F + f1), (double) (0.5F + f2))
                .color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    public void drawScreen(int i, int j, float f) {
        GlStateManager.disableAlpha();
        if (enableBlur) {
            this.renderSkybox(i, j, f);
        } else {
            this.drawPanorama(i, j, f);
        }
        GlStateManager.enableAlpha();

        this.drawGradientRect(0, 0, this.width, this.height, 0x55000000, 0x88000000);

        final int logoSize = 110;
        final int logoX = (this.width - logoSize) / 2;
        final int logoY = this.height / 2 - 110;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(apolloLogo);
        Gui.drawScaledCustomSizeModalRect(logoX, logoY, 0.0F, 0.0F, 256, 256, logoSize, logoSize, 256.0F, 256.0F);

        // Client Title has been removed entirely

        String s = EaglercraftVersion.mainMenuStringB;
        this.drawString(this.fontRendererObj, s, 2, this.height - 10, 0xFFAAAAAA);

        String s1 = EaglercraftVersion.mainMenuStringC;
        this.drawString(this.fontRendererObj, s1,
                this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 20, 0xFFAAAAAA);
        s1 = EaglercraftVersion.mainMenuStringD;
        this.drawString(this.fontRendererObj, s1,
                this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 10, 0xFFAAAAAA);

        this.updateCheckerOverlay.drawScreen(i, j, f);
        super.drawScreen(i, j, f);

        for (Object o : this.buttonList) {
            if (o instanceof GuiClearButton) {
                GuiClearButton btn = (GuiClearButton) o;
                if (btn.id == 4) {
                    ResourceLocation skinTex;
                    if (EaglerProfile.customSkins != null && !EaglerProfile.customSkins.isEmpty() && EaglerProfile.presetSkinId == -1) {
                        skinTex = EaglerProfile.customSkins.get(EaglerProfile.customSkinId).getResource();
                    } else {
                        int id = EaglerProfile.presetSkinId == -1 ? 0 : EaglerProfile.presetSkinId;
                        skinTex = DefaultSkins.getSkinFromId(id).location;
                    }
                    
                    this.mc.getTextureManager().bindTexture(skinTex);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    Gui.drawScaledCustomSizeModalRect(btn.xPosition + 3, btn.yPosition + 2, 8.0F, 8.0F, 8, 8, 16, 16, 64.0F, 64.0F);
                    Gui.drawScaledCustomSizeModalRect(btn.xPosition + 3, btn.yPosition + 2, 40.0F, 8.0F, 8, 8, 16, 16, 64.0F, 64.0F);
                }
            }
        }
    }

    protected void mouseClicked(int par1, int par2, int par3) {
        this.updateCheckerOverlay.mouseClicked(par1, par2, par3);
        super.mouseClicked(par1, par2, par3);
    }
}