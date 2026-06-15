package net.minecraft.monsoonclient.gui;

import java.util.ArrayList;
import net.minecraft.monsoonclient.gui.mods.TestMod;
import net.minecraft.monsoonclient.gui.mods.CoordinatesMod;
import net.minecraft.monsoonclient.gui.mods.FPSMod;
import net.minecraft.monsoonclient.gui.mods.FullBrightMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.monsoonclient.gui.hud.CategoryManager;
import net.minecraft.monsoonclient.gui.mods.DispatchDisplayMod;
import net.minecraft.monsoonclient.gui.mods.ClockMod;

public class HudManager {
    public ArrayList<HudMod> hudMods = new ArrayList<>();

    public TestMod testMod;
    public CoordinatesMod coordinatesMod;
    public FPSMod fpsMod;
    public FullBrightMod fullBrightMod;
    public DispatchDisplayMod dispatchDisplayMod;
    public ClockMod clockMod;

    public CategoryManager categoryManager;

    public HudManager() {
        hudMods.add(testMod = new TestMod());
        hudMods.add(coordinatesMod = new CoordinatesMod());
        hudMods.add(fpsMod = new FPSMod());
        hudMods.add(fullBrightMod = new FullBrightMod());
        hudMods.add(dispatchDisplayMod = new DispatchDisplayMod());
        hudMods.add(clockMod = new ClockMod());

        categoryManager = new CategoryManager(this);
    }

    public void renderMods() {
        for (HudMod m : hudMods) {
            if (m.isEnabled()) {
                m.draw();
            }
        }
    }

}
