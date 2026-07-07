package net.minecraft.apolloclient.gui.mods;

import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer; 
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class WavePointMod extends HudMod {

    private final List<WavePoint> wavePoints = new ArrayList<>();
    private boolean vWasPressed = false;
    private boolean pWasPressed = false;

    // Customizable Variables
    public int BEAM_COLOR = 0xFFFF0000;     // Red by default
    public float BEAM_OPACITY = 0.6f;       // 60% opaque by default
    public String BEAM_TYPE = "corner";     // "corner" or "full"

    public WavePointMod() {
        super("Wave Points", 5, 130, Category.HUD);
        this.supportedOptions = new ModOption[] {
            ModOption.TEXT_COLOR,
            ModOption.TEXT_SCALE,
            ModOption.TEXT_SHADOW,
            ModOption.RENDER_BACKGROUND,
            ModOption.BACKGROUND_COLOR,
            ModOption.BACKGROUND_OPACITY,
            
            // Add the new options here so the GUI shows them
            ModOption.BEAM_COLOR,
            ModOption.BEAM_OPACITY,
            ModOption.BEAM_TYPE
        };
    }

    @Override
    public void draw() {
        super.draw();

        // 1. Handle Keybinds
        boolean vDown = Keyboard.isKeyDown(KeyboardConstants.KEY_V);
        if (vDown && !vWasPressed) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                BlockPos pos = mc.thePlayer.getPosition();
                wavePoints.add(new WavePoint(pos, "Test Wave Point"));
            }
        }
        vWasPressed = vDown;

        boolean pDown = Keyboard.isKeyDown(KeyboardConstants.KEY_P);
        if (pDown && !pWasPressed) {
            if (!wavePoints.isEmpty()) {
                wavePoints.remove(wavePoints.size() - 1);
            }
        }
        pWasPressed = pDown;

        // 2. Draw HUD Text List
        if (mc.thePlayer == null) return;

        int currentY = getY();
        for (WavePoint wp : wavePoints) {
            double dist = mc.thePlayer.getDistance(wp.pos.getX() + 0.5, wp.pos.getY(), wp.pos.getZ() + 0.5);
            String displayText = wp.name + " | " + (int) dist + " blocks";
            
            drawModText(displayText, getX(), currentY);
            currentY += getTextHeight() + 2;
        }
    }

    /**
     * This method is called separately during 3D world rendering.
     */
    public void render3D(float partialTicks) {
        if (!this.isEnabled() || mc.thePlayer == null || mc.theWorld == null || wavePoints.isEmpty()) return;

        double playerX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks;
        double playerY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks;
        double playerZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks;

        // Extract RGB from BEAM_COLOR and apply BEAM_OPACITY
        float r = ((BEAM_COLOR >> 16) & 0xFF) / 255.0f;
        float g = ((BEAM_COLOR >> 8) & 0xFF) / 255.0f;
        float b = (BEAM_COLOR & 0xFF) / 255.0f;
        float a = Math.max(0.0f, Math.min(1.0f, BEAM_OPACITY));

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth(); 
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(r, g, b, a);

        for (WavePoint wp : wavePoints) {
            double relativeX = wp.pos.getX() + 0.5 - playerX;
            double relativeZ = wp.pos.getZ() + 0.5 - playerZ;

            AxisAlignedBB beamBox = new AxisAlignedBB(
                relativeX - 0.5, -playerY, relativeZ - 0.5,
                relativeX + 0.5, 256 - playerY, relativeZ + 0.5
            );

            if (BEAM_TYPE.equalsIgnoreCase("full")) {
                drawFilledBox(beamBox);
            } else {
                // Default to corner/outline
                RenderGlobal.drawSelectionBoundingBox(beamBox);
            }
        }

        // Reset GL state
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    /**
     * Helper method to draw a solid filled box using the Tessellator.
     */
    private void drawFilledBox(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        
        // Use GL_QUADS directly because of the RealOpenGLEnums static import
        wr.begin(GL_QUADS, DefaultVertexFormats.POSITION);

        // Bottom face
        wr.pos(box.minX, box.minY, box.minZ).endVertex();
        wr.pos(box.maxX, box.minY, box.minZ).endVertex();
        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();
        wr.pos(box.minX, box.minY, box.maxZ).endVertex();

        // Top face
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();
        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();

        // North face (-Z)
        wr.pos(box.minX, box.minY, box.minZ).endVertex();
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();
        wr.pos(box.maxX, box.minY, box.minZ).endVertex();

        // South face (+Z)
        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.minX, box.minY, box.maxZ).endVertex();

        // West face (-X)
        wr.pos(box.minX, box.minY, box.maxZ).endVertex();
        wr.pos(box.minX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.minX, box.maxY, box.minZ).endVertex();
        wr.pos(box.minX, box.minY, box.minZ).endVertex();

        // East face (+X)
        wr.pos(box.maxX, box.minY, box.minZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.minZ).endVertex();
        wr.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        wr.pos(box.maxX, box.minY, box.maxZ).endVertex();

        tessellator.draw();
    }

    @Override
    public int getWidth() {
        return getTextWidth("Test Wave Point | 999 blocks");
    }

    @Override
    public int getHeight() {
        int points = Math.max(1, wavePoints.size());
        return (getTextHeight() + 2) * points;
    }

    // =====================================================================
    // --- Generic Option Overrides ---
    // This connects the GUI's dynamic text boxes to our custom variables.
    // =====================================================================

    @Override
    public int getOptionColor(ModOption opt) {
        if (opt == ModOption.BEAM_COLOR) return this.BEAM_COLOR;
        return super.getOptionColor(opt);
    }

    @Override
    public void setOptionColor(ModOption opt, int val) {
        if (opt == ModOption.BEAM_COLOR) this.BEAM_COLOR = val;
        else super.setOptionColor(opt, val);
    }

    @Override
    public float getOptionNumber(ModOption opt) {
        if (opt == ModOption.BEAM_OPACITY) return this.BEAM_OPACITY;
        return super.getOptionNumber(opt);
    }

    @Override
    public void setOptionNumber(ModOption opt, float val) {
        if (opt == ModOption.BEAM_OPACITY) this.BEAM_OPACITY = val;
        else super.setOptionNumber(opt, val);
    }

    @Override
    public String getOptionString(ModOption opt) {
        if (opt == ModOption.BEAM_TYPE) return this.BEAM_TYPE;
        return super.getOptionString(opt);
    }

    @Override
    public void setOptionString(ModOption opt, String val) {
        if (opt == ModOption.BEAM_TYPE) this.BEAM_TYPE = val;
        else super.setOptionString(opt, val);
    }

    // Simple data class to store our wave points
    private static class WavePoint {
        final BlockPos pos;
        final String name;

        WavePoint(BlockPos pos, String name) {
            this.pos = pos;
            this.name = name;
        }
    }
}