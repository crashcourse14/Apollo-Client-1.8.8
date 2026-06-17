package net.minecraft.monsoonclient.gui.mods;

import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.ModOption;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;

/**
 * BlockOverlayMod
 *
 * Renders a custom-coloured outline around the block the player is looking at,
 * and fills its faces with a translucent hover colour.
 *
 * Both colours are ARGB ints exposed as public fields so GuiModList /
 * ConfigManager can read and write them directly.
 */
public class BlockOverlayMod extends HudMod {

    // ── Configurable colours ───────────────────────────────────────────────
    /** ARGB colour of the block outline wireframe. Default: white, fully opaque. */
    public int outlineColor = 0xFFFFFFFF;

    /**
     * ARGB colour of the translucent face-fill drawn over the hovered block.
     * Default: white at ~25% opacity.
     */
    public int hoverColor = 0x40FFFFFF;

    // ── Constructor ────────────────────────────────────────────────────────
    public BlockOverlayMod() {
        super("Block Overlay", 0, 0, Category.MECHANIC);
        this.icon = new ResourceLocation("monsoonclient/textures/block_overlay.png");

        // No text options — only the two colour pickers.
        this.supportedOptions = new ModOption[] {
            ModOption.OUTLINE_COLOR,
            ModOption.HOVER_COLOR
        };
    }

    // ── HudMod overrides ───────────────────────────────────────────────────

    @Override
    public void draw() {
        MovingObjectPosition mop = mc.objectMouseOver;
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        BlockPos pos   = mop.getBlockPos();
        Block    block = mc.theWorld.getBlockState(pos).getBlock();

        if (block.getMaterial() == Material.air) return;

        // Required before getSelectedBoundingBox so non-uniform blocks
        // (fences, stairs, slabs…) report the correct shape.
        block.setBlockBoundsBasedOnState(mc.theWorld, pos);

        AxisAlignedBB bb = block.getSelectedBoundingBox(mc.theWorld, pos);
        if (bb == null) return;

        // Interpolated camera position
        Entity viewer = mc.getRenderViewEntity();
        double px = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * mc.timer.renderPartialTicks;
        double py = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * mc.timer.renderPartialTicks;
        double pz = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * mc.timer.renderPartialTicks;

        // Camera-relative BB, expanded 0.002 so the overlay sits outside the face.
        AxisAlignedBB rel = bb
                .expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
                .offset(-px, -py, -pz);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        int ha = (hoverColor >> 24) & 0xFF;
        if (ha > 0) {
            GlStateManager.disableDepth();
            drawFilledBox(rel,
                (hoverColor >> 16) & 0xFF,
                (hoverColor >>  8) & 0xFF,
                 hoverColor        & 0xFF,
                ha);
            GlStateManager.enableDepth();
        }

        EaglercraftGPU.glLineWidth(2.0F);
        RenderGlobal.func_181563_a(rel,
            (outlineColor >> 16) & 0xFF,
            (outlineColor >>  8) & 0xFF,
             outlineColor        & 0xFF,
            (outlineColor >> 24) & 0xFF);

        // ── Restore state ─────────────────────────────────────────────────
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Draws a solid-coloured filled box using GL_QUADS (mode 7).
     * Mirrors the pattern used in RenderGlobal for sky/horizon geometry.
     * Colour components are 0-255 ints, matching WorldRenderer.color().
     */
    private static void drawFilledBox(AxisAlignedBB b, int r, int g, int bl, int a) {
        Tessellator   tess = Tessellator.getInstance();
        WorldRenderer wr   = tess.getWorldRenderer();

        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);

        double x0 = b.minX, y0 = b.minY, z0 = b.minZ;
        double x1 = b.maxX, y1 = b.maxY, z1 = b.maxZ;

        // Bottom (-Y)
        wr.pos(x0, y0, z0).color(r,g,bl,a).endVertex();
        wr.pos(x1, y0, z0).color(r,g,bl,a).endVertex();
        wr.pos(x1, y0, z1).color(r,g,bl,a).endVertex();
        wr.pos(x0, y0, z1).color(r,g,bl,a).endVertex();
        // Top (+Y)
        wr.pos(x0, y1, z0).color(r,g,bl,a).endVertex();
        wr.pos(x0, y1, z1).color(r,g,bl,a).endVertex();
        wr.pos(x1, y1, z1).color(r,g,bl,a).endVertex();
        wr.pos(x1, y1, z0).color(r,g,bl,a).endVertex();
        // North (-Z)
        wr.pos(x0, y0, z0).color(r,g,bl,a).endVertex();
        wr.pos(x0, y1, z0).color(r,g,bl,a).endVertex();
        wr.pos(x1, y1, z0).color(r,g,bl,a).endVertex();
        wr.pos(x1, y0, z0).color(r,g,bl,a).endVertex();
        // South (+Z)
        wr.pos(x0, y0, z1).color(r,g,bl,a).endVertex();
        wr.pos(x1, y0, z1).color(r,g,bl,a).endVertex();
        wr.pos(x1, y1, z1).color(r,g,bl,a).endVertex();
        wr.pos(x0, y1, z1).color(r,g,bl,a).endVertex();
        // West (-X)
        wr.pos(x0, y0, z0).color(r,g,bl,a).endVertex();
        wr.pos(x0, y0, z1).color(r,g,bl,a).endVertex();
        wr.pos(x0, y1, z1).color(r,g,bl,a).endVertex();
        wr.pos(x0, y1, z0).color(r,g,bl,a).endVertex();
        // East (+X)
        wr.pos(x1, y0, z0).color(r,g,bl,a).endVertex();
        wr.pos(x1, y1, z0).color(r,g,bl,a).endVertex();
        wr.pos(x1, y1, z1).color(r,g,bl,a).endVertex();
        wr.pos(x1, y0, z1).color(r,g,bl,a).endVertex();

        tess.draw();
    }

    @Override public int getWidth()  { return 0; }
    @Override public int getHeight() { return 0; }
}