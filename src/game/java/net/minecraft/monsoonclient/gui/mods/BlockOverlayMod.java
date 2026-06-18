package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.ModOption;
import net.minecraft.monsoonclient.gui.hud.Category;

public class BlockOverlayMod extends HudMod {
    
    public int outlineColor = 0xFFFF0000;

    public BlockOverlayMod() {
        super("Block Overlay", 0, 0, Category.MECHANIC);
        this.supportedOptions = new ModOption[] {
            ModOption.OUTLINE_COLOR,
            ModOption.HOVER_COLOR
        };
    }
    @Override
    public int getOptionColor(ModOption opt) {
        if (opt == ModOption.OUTLINE_COLOR) return this.outlineColor;
        return super.getOptionColor(opt);
    }

    @Override
    public void setOptionColor(ModOption opt, int val) {
        if (opt == ModOption.OUTLINE_COLOR) this.outlineColor = val;
        else super.setOptionColor(opt, val);
    }
}