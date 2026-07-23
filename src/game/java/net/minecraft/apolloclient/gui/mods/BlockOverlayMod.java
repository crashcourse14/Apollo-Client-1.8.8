package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.gui.hud.Category;

public class BlockOverlayMod extends HudMod {
    
    public int outlineColor = 0xFFFF0000;

    public int fillColor = 0x4DFFFFFF;

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
        if (opt == ModOption.HOVER_COLOR) return this.fillColor;
        return super.getOptionColor(opt);
    }

    @Override
    public void setOptionColor(ModOption opt, int val) {
        if (opt == ModOption.OUTLINE_COLOR) this.outlineColor = val;
        else if (opt == ModOption.HOVER_COLOR) this.fillColor = val;
        else super.setOptionColor(opt, val);
    }
}