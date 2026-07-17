package net.minecraft.apolloclient.gui.mods;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CPSMod extends HudMod {

    private final List<Long> clickTimes = new ArrayList<>();
    private boolean wasMouseDown = false;

    public CPSMod() {
        super("CPS", 5, 80, Category.HUD);
        
        this.textFormat = "%VALUE% CPS";
        this.icon = new ResourceLocation("apolloclient:textures/cps.png");
        this.renderBackground = true;
        this.backgroundOpacity = 0.3f;
        this.enabled = false;
    }

    @Override
    public void draw() {
        super.draw();
        

        boolean isMouseDown = Mouse.isButtonDown(0);
        
        if (isMouseDown && !wasMouseDown) {
            clickTimes.add(System.currentTimeMillis());
        }
        wasMouseDown = isMouseDown;
        
        long now = System.currentTimeMillis();
        Iterator<Long> iterator = clickTimes.iterator();
        while (iterator.hasNext()) {
            if (now - iterator.next() > 1000) {
                iterator.remove();
            }
        }
        
        int cps = clickTimes.size();
        String displayText = formatText(String.valueOf(cps));
        drawModText(displayText, getX(), getY());
    }

    @Override
    public int getWidth() {
        return getTextWidth(formatText("99"));
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }
}