package net.minecraft.monsoonclient.gui;

import java.util.ArrayList;
import net.minecraft.monsoonclient.gui.mods.TestMod;
import net.minecraft.monsoonclient.gui.mods.CoordinatesMod;
import net.minecraft.monsoonclient.gui.mods.FPSMod;

public class HudManager {
    public ArrayList<HudMod> hudMods = new ArrayList<>();

    public TestMod testMod;
    public CoordinatesMod coordinatesMod;
    public FPSMod fpsMod;

    public HudManager() {
        hudMods.add(testMod = new TestMod());
        hudMods.add(coordinatesMod = new CoordinatesMod());
        hudMods.add(fpsMod = new FPSMod());
    }

    public void renderMods() {
        for (HudMod m : hudMods) {
            if (m.isEnabled()) {
                m.draw();
            }
        }
    }

}
