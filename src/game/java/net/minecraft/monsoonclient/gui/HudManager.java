package net.minecraft.monsoonclient.gui;

import java.util.ArrayList;
import net.minecraft.monsoonclient.gui.mods.TestMod;

public class HudManager {
    public ArrayList<HudMod> hudMods = new ArrayList<>();

    public TestMod testMod;

    public HudManager() {
        hudMods.add(testMod = new TestMod());
    }

    public void renderMods() {
        for (HudMod m : hudMods) {
            if (m.isEnabled()) {
                m.draw();
            }
        }
    }

}
