package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

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
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.profile.GuiScreenEditProfile;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenDemoPlayWorldSelection;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerBusy;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerStartup;
import net.lax1dude.eaglercraft.v1_8.update.GuiUpdateCheckerOverlay;
import net.lax1dude.eaglercraft.v1_8.update.GuiUpdateVersionSlot;
import net.lax1dude.eaglercraft.v1_8.update.UpdateCertificate;
import net.lax1dude.eaglercraft.v1_8.update.UpdateService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.ISaveFormat;

import net.minecraft.client.gui.GuiClearButton;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
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
	private boolean field_175375_v = true;
	private String openGLWarning1;
	private String openGLWarning2;
	private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
	private static final ResourceLocation monsoonLogo = new ResourceLocation("monsoon/title/logo.png");
	private static final ResourceLocation minecraftTitleTextures = new ResourceLocation(
			"textures/gui/title/minecraft.png");
	private static final ResourceLocation eaglerGuiTextures = new ResourceLocation("eagler:gui/eagler_gui.png");
	private static final ResourceLocation backgroundImage = new ResourceLocation("monsoon/textures/background.png");
	private int field_92024_r;
	private int field_92023_s;
	private int field_92022_t;
	private int field_92021_u;
	private int field_92020_v;
	private int field_92019_w;
	private GuiUpdateCheckerOverlay updateCheckerOverlay;
	private GuiButton downloadOfflineButton;
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
	}

	public static void doResourceReloadHack() {
		if (instance != null) {
			instance.shouldReload = true;
		}
	}

	/**+
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		if (downloadOfflineButton != null) {
			downloadOfflineButton.enabled = !UpdateService.shouldDisableDownloadButton();
		}
		if (shouldReload) {
			reloadResourceFlags();
			shouldReload = false;
		}
	}

	/**+
	 * Returns true if this GUI should pause the game when it is
	 * displayed in single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**+
	 * Fired when a key is typed (except F11 which toggles full
	 * screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char parChar1, int parInt1) {
	}

	/**+
	 * Adds the buttons (and other controls) to the screen in
	 * question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		this.updateCheckerOverlay.setResolution(mc, width, height);

		// Clean centered column layout
		// Buttons stack vertically in the lower-center of the screen
		final int btnW = 200;
		final int btnH = 20;
		final int btnGap = 6;  // gap between buttons
		final int cx = this.width / 2 - btnW / 2;

		// Start buttons below vertical midpoint so logo/title has breathing room
		int startY = this.height / 2 + 20;

		if (this.mc.isDemo()) {
			this.addDemoButtons(startY, btnH + btnGap);
		
		} else {
			// Singleplayer
			this.buttonList.add(new GuiClearButton(1, cx, startY, btnW, btnH,
					I18n.format("menu.singleplayer", new Object[0])));
			startY += btnH + btnGap;
			// Multiplayer
			this.buttonList.add(new GuiClearButton(2, cx, startY, btnW, btnH,
					I18n.format("menu.multiplayer", new Object[0])));
			startY += btnH + btnGap;
		}

		// Options | Edit Profile — side by side, half-width each
		final int halfW = (btnW - 4) / 2;
		this.buttonList.add(new GuiClearButton(0, cx, startY, halfW, btnH,
				I18n.format("menu.options", new Object[0])));
		this.buttonList.add(new GuiClearButton(4, cx + halfW + 4, startY, halfW, btnH,
				I18n.format("menu.editProfile", new Object[0])));
		startY += btnH + btnGap;

		// Social — full width
		this.buttonList.add(new GuiClearButton(101, cx, startY, btnW, btnH, "Social"));

		this.mc.func_181537_a(false);
	}


	/**+
	 * Adds Demo buttons on Main Menu for players who are playing
	 * Demo.
	 */
	private void addDemoButtons(int startY, int step) {
		final int btnW = 200;
		final int btnH = 20;
		final int cx = this.width / 2 - btnW / 2;
		this.buttonList.add(new GuiButton(11, cx, startY, btnW, btnH,
				I18n.format("menu.playdemo", new Object[0])));
		this.buttonList.add(this.buttonResetDemo = new GuiButton(12, cx, startY + step, btnW, btnH,
				I18n.format("menu.resetdemo", new Object[0])));
		this.buttonResetDemo.enabled = this.mc.gameSettings.hasCreatedDemoWorld;
	}

	/**+
	 * Called by the controls from the buttonList when activated.
	 * (Mouse pressed for buttons)
	 */
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

	/**+
	 * Draws the screen and all the components in it. Args : mouseX,
	 * mouseY, renderPartialTicks
	 */
	public void drawScreen(int i, int j, float f) {
		// Draw fullscreen background image (animated red wallpaper)
		GlStateManager.disableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(backgroundImage);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(0, this.height, this.zLevel).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos(this.width, this.height, this.zLevel).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos(this.width, 0, this.zLevel).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos(0, 0, this.zLevel).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
		GlStateManager.enableAlpha();

		// Subtle dark vignette so text and buttons pop cleanly
		this.drawGradientRect(0, 0, this.width, this.height, 0x55000000, 0x88000000);

		// ── Logo ──────────────────────────────────────────────────────────────
		final int logoSize = 72;
		final int logoX = (this.width - logoSize) / 2;
		// Position logo in upper third, well above the buttons
		final int logoY = this.height / 2 - 110;

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(monsoonLogo);
		Gui.drawScaledCustomSizeModalRect(logoX, logoY, 0.0F, 0.0F, 256, 256, logoSize, logoSize, 256.0F, 256.0F);

		// ── Title text (2× scale, centered below logo) ────────────────────────
		final String title = "Monsoon Client";
		GlStateManager.pushMatrix();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		int titleWidth = this.fontRendererObj.getStringWidth(title);
		int titleX = (this.width - titleWidth * 2) / 4;
		int titleY = (logoY + logoSize + 6) / 2;
		this.drawString(this.fontRendererObj, title, titleX, titleY, 0xFFFFFFFF);
		GlStateManager.popMatrix();

		// ── OpenGL warning (if present) ────────────────────────────────────────
		boolean isForkLabel = ((this.openGLWarning1 != null && this.openGLWarning1.length() > 0)
				|| (this.openGLWarning2 != null && this.openGLWarning2.length() > 0));

		if (isForkLabel) {
			drawRect(this.field_92022_t - 3, this.field_92021_u - 3, this.field_92020_v + 3, this.field_92019_w,
					1428160512);
			if (this.openGLWarning1 != null)
				this.drawString(this.fontRendererObj, this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
			if (this.openGLWarning2 != null)
				this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.field_92024_r) / 2,
						this.field_92021_u + 12, -1);
		}


		// ── Footer version strings ─────────────────────────────────────────────
		String s = EaglercraftVersion.mainMenuStringB;
		this.drawString(this.fontRendererObj, s, 2, this.height - 10, 0xFFAAAAAA);

		String s1 = EaglercraftVersion.mainMenuStringC;
		this.drawString(this.fontRendererObj, s1,
				this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 20, 0xFFAAAAAA);
		s1 = EaglercraftVersion.mainMenuStringD;
		this.drawString(this.fontRendererObj, s1,
				this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 10, 0xFFAAAAAA);

		// ── CREDITS link (top-right, minimal) ─────────────────────────────────
		String lbl = "CREDITS.txt";
		int w = fontRendererObj.getStringWidth(lbl) * 3 / 4;

		if (i >= (this.width - w - 4) && i <= this.width && j >= 0 && j <= 9) {
			Mouse.showCursor(EnumCursorType.HAND);
			drawRect((this.width - w - 4), 0, this.width, 10, 0x55000099);
		} else {
			drawRect((this.width - w - 4), 0, this.width, 10, 0x33000000);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate((this.width - w - 2), 2.0f, 0.0f);
		GlStateManager.scale(0.75f, 0.75f, 0.75f);
		drawString(fontRendererObj, lbl, 0, 0, 0xFFCCCCCC);
		GlStateManager.popMatrix();

		this.updateCheckerOverlay.drawScreen(i, j, f);
		super.drawScreen(i, j, f);
	}

	/**+
	 * Called when the mouse is clicked. Args : mouseX, mouseY,
	 * clickedButton
	 */
	protected void mouseClicked(int par1, int par2, int par3) {
		if (par3 == 0) {
			String lbl = "CREDITS.txt";
			int w = fontRendererObj.getStringWidth(lbl) * 3 / 4;
			if (par1 >= (this.width - w - 4) && par1 <= this.width && par2 >= 0 && par2 <= 10) {
				String resStr = EagRuntime.getResourceString("/assets/eagler/CREDITS.txt");
				if (resStr != null) {
					EagRuntime.openCreditsPopup(resStr);
				}
				mc.getSoundHandler()
						.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
				return;
			}
		}
		this.updateCheckerOverlay.mouseClicked(par1, par2, par3);
		super.mouseClicked(par1, par2, par3);
	}
}